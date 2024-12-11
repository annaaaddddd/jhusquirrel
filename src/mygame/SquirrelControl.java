package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.anim.AnimComposer;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.Iterator;
import java.util.List;

public class SquirrelControl extends AbstractControl {

    private Camera cam;
    private Vector3f cameraOffset = new Vector3f(0, 5, 10);
    private boolean canClimb = false;
    private float climbDistanceThreshold = 3.0f;
    private List<Spatial> trees;
    private List<Spatial> acorns;
    private Node rootNode;
    private BitmapText acornCounterText;
    private static int collectedAcorns = 2;
    private GameRunningAppState gameRunningAppState;
    
    private float sensitivity = 0.4f;
    private InputManager inputManager;
    private float yaw = 0f;
    private float pitch = 10f;
    private RigidBodyControl squirrelPhysics;
    private float targetZLevel = -10f;
    private final float idleChirpInterval = 5.0f; // Chirp every 5 seconds of idling
    private AudioNode chirpSound;
    private AudioNode acornCollectSound;
    private AssetManager assetManager;
    
    // Animation related fields
    private boolean isJumping = false; // Track if the squirrel is jumping
    private boolean isFlying = false; // Track if the squirrel is mid-air
    private boolean playedJumpEnd = false; // Track if Jump.End has been played
    private final AnimComposer animComposer;
    private float idleTimer = 0f;
    private final float idleAnimationInterval = 8f; // Trigger idle animation every 5 seconds
    private String currentAnimation = null; // Keeps track of the current animation

    
    // Camera shake related fields
    private boolean isShaking = false;
    private float shakeIntensity = 0f;
    private float shakeDuration = 0f;
    private float shakeTimeElapsed = 0f;
    private Vector3f originalCameraPosition = null;


    public SquirrelControl(GameRunningAppState gameRunningAppState, Camera cam, List<Spatial> trees, List<Spatial> acorns, Node rootNode,
                            BitmapText acornCounterText, InputManager inputManager, RigidBodyControl squirrelPhysics, 
                            AssetManager assetManager, AnimComposer animComposer) {
        this.gameRunningAppState = gameRunningAppState;
        this.cam = cam;
        this.trees = trees;
        this.acorns = acorns;
        this.rootNode = rootNode;
        this.acornCounterText = acornCounterText;
        this.inputManager = inputManager;
        this.squirrelPhysics = squirrelPhysics;
        this.assetManager = assetManager; // Assign the AssetManager
        setupMouseControl();
        this.animComposer = animComposer;
    }

@Override
protected void controlUpdate(float tpf) {
    processCameraShake(tpf);
    updateSquirrelState(tpf);

    // Get the squirrel's current position
    Vector3f position = spatial.getWorldTranslation();

    // Print the XYZ values in real-time
    // System.out.printf("Squirrel Position - X: %.2f, Y: %.2f, Z: %.2f%n", position.x, position.y, position.z);

    // Prevent the squirrel from sinking below the ground
    if (position.y < 0) {
        spatial.setLocalTranslation(position.x, 0.1f, position.z); // Slightly above ground
        Vector3f currentVelocity = squirrelPhysics.getLinearVelocity();
        // Remove any downward velocity when clamping
        squirrelPhysics.setLinearVelocity(new Vector3f(currentVelocity.x, Math.max(0, currentVelocity.y), currentVelocity.z));
    }
    else {
        squirrelPhysics.setGravity(new Vector3f(0, -3.15f, 0));
    }
    


    updateCameraPosition();
    updateSquirrelRotation();
    checkProximityToTree();
    checkProximityToAcorns();

    Vector3f velocity = squirrelPhysics.getLinearVelocity();
    if (velocity.length() < 0.1f) { // If not moving
        idleTimer += tpf;
        if (idleTimer >= idleChirpInterval) {
            chirpSound.setLocalTranslation(spatial.getWorldTranslation()); // Update position
            chirpSound.playInstance();
            idleTimer = 0; // Reset timer after chirp
        }
    } else {
        idleTimer = 0; // Reset timer when moving
    }
}

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Not used in this case, so leave it empty
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            acornCollectSound = new AudioNode(assetManager, "Sounds/Effects/acorn-achievement-bell.wav", false);
            acornCollectSound.setPositional(true);
            acornCollectSound.setVolume(0.8f);
            rootNode.attachChild(acornCollectSound);
            
            chirpSound = new AudioNode(assetManager, "Sounds/Effects/Chirp.wav", false);
            chirpSound.setPositional(true);
            chirpSound.setVolume(0.7f);
            rootNode.attachChild(chirpSound);
        }
    }
    
    private void checkProximityToAcorns() {
        Vector3f squirrelPos = spatial.getWorldTranslation();
        Iterator<Spatial> iterator = acorns.iterator();
        int acornnumber = 0;
        while (iterator.hasNext()) {
            Spatial acorn = iterator.next();
            float distance = squirrelPos.distance(acorn.getWorldTranslation());
            if (distance < 1.0f) {
                System.out.println("Collecting acorn at: " + acorn.getWorldTranslation());
                acornCollectSound.setLocalTranslation(acorn.getWorldTranslation());
                acornCollectSound.playInstance();
                rootNode.detachChild(acorn);
                iterator.remove(); // Safely remove acorn
                collectedAcorns++;
                acornnumber++;
                System.out.println("Updating counter display: " + collectedAcorns);

                
                updateAcornCounter(collectedAcorns);
            }
        }
    }
    
    private String toRomanNumeral(int number) {
        switch (number) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IIII";
            case 5: return "IIIII";
            default: return ""; // Handle unexpected cases gracefully
        }
    }

    private void endGame() {
        if (gameRunningAppState == null) {
            System.err.println("GameRunningAppState reference is null!");
            return;
        }

        if (!gameRunningAppState.isGameCompleted()) { // Only call once
            System.out.println("Trigger endGame.");
            gameRunningAppState.endGame();
        } else {
            System.out.println("Game already marked as completed. Skipping duplicate call.");
        }
    }

    private void updateAcornCounter(int count) {
        if (acornCounterText != null) {
            // Convert count to Roman numeral using the hardcoded method
            String romanCount = toRomanNumeral(count);

            // Update the text directly
            acornCounterText.setText("Acorns Collected: " + romanCount + " out of III");

            System.out.println("Acorns collected (in Roman numerals): " + romanCount);
            if (count >= 3){
                endGame();
            }
        } else {
            System.out.println("acornCounterText is null!");
        }
    }



    private void updateCameraPosition() {
        Vector3f squirrelPos = spatial.getWorldTranslation();
        float offsetX = -cameraOffset.z * FastMath.sin(yaw);
        float offsetZ = -cameraOffset.z * FastMath.cos(yaw);
        float offsetY = cameraOffset.y * FastMath.sin(pitch);

        Vector3f camPos = squirrelPos.add(offsetX, offsetY, offsetZ);
        cam.setLocation(camPos);
        cam.lookAt(squirrelPos, Vector3f.UNIT_Y);
    }

    private void updateSquirrelRotation() {
        Quaternion newRotation = new Quaternion().fromAngleAxis(yaw, Vector3f.UNIT_Y);
        spatial.setLocalRotation(newRotation);
    }

    public void moveForward(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.setLinearVelocity(cam.getDirection().mult(2).setY(squirrelPhysics.getLinearVelocity().y)); // Burst forward
    }

    public void moveBackward(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.setLinearVelocity(cam.getDirection().mult(-2).setY(squirrelPhysics.getLinearVelocity().y)); // Burst backward
    }

    public void moveLeft(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.setLinearVelocity(cam.getLeft().mult(2).setY(squirrelPhysics.getLinearVelocity().y)); // Burst left
    }

    public void moveRight(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.setLinearVelocity(cam.getLeft().mult(-2).setY(squirrelPhysics.getLinearVelocity().y)); // Burst right
    }

    public void climbUp() {
        startJump();
        
        squirrelPhysics.setLinearVelocity(new Vector3f(0, 3, 0)); // Burst upward
    }

    public void climbDown(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.setLinearVelocity(new Vector3f(0, -2, 0)); // Burst downward
    }

    private void checkProximityToTree() {
        Vector3f squirrelPos = spatial.getWorldTranslation();
        float closestDistance = Float.MAX_VALUE;

        for (Spatial tree : trees) {
            float distance = squirrelPos.distance(tree.getWorldTranslation());
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }

        canClimb = closestDistance <= climbDistanceThreshold;
    }

    private void setupMouseControl() {
        inputManager.setCursorVisible(false);

        // Add mappings only if they don't already exist
        if (!inputManager.hasMapping("MouseLeft")) {
            inputManager.addMapping("MouseLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        }
        if (!inputManager.hasMapping("MouseRight")) {
            inputManager.addMapping("MouseRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        }
        if (!inputManager.hasMapping("MouseUp")) {
            inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        }
        if (!inputManager.hasMapping("MouseDown")) {
            inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        }

        // Add the listener only once
        inputManager.addListener(analogListener, "MouseLeft", "MouseRight", "MouseUp", "MouseDown");
    }


    private final AnalogListener analogListener = (name, value, tpf) -> {
        if (name.equals("MouseLeft")) {
            yaw += value * sensitivity;
        } else if (name.equals("MouseRight")) {
            yaw -= value * sensitivity;
        } else if (name.equals("MouseUp")) {
            pitch += value * sensitivity;
            pitch = FastMath.clamp(pitch, -FastMath.HALF_PI, FastMath.HALF_PI);
        } else if (name.equals("MouseDown")) {
            pitch -= value * sensitivity;
            pitch = FastMath.clamp(pitch, -FastMath.HALF_PI, FastMath.HALF_PI);
        }
        updateCameraPosition();
        updateSquirrelRotation();
    };
    
    private void updateSquirrelState(float tpf) {
        Vector3f velocity = squirrelPhysics.getLinearVelocity();
        Vector3f position = spatial.getWorldTranslation();
        
        float groundLevel = 0.1f; // Ground height
        boolean isGrounded = position.y - 0.02f <= groundLevel;
        if (isGrounded) {
            // If transitioning from air to ground
            if (isFlying && !playedJumpEnd) { // || isJumping
                animComposer.reset();
                playAnimation("Jump.End"); // Play landing animation
                System.out.println("No jump animation.");
                isFlying = false;
                isJumping = false;
                playedJumpEnd = true;
            }

            // Stop downward motion and ensure smooth landing
            squirrelPhysics.setLinearVelocity(new Vector3f(velocity.x, Math.max(0, velocity.y), velocity.z));
            spatial.setLocalTranslation(position.x, groundLevel, position.z);
            
            // Increment idle timer when stationary
            if (velocity.length() < 0.055f) {
                idleTimer += tpf;
                if (idleTimer >= idleAnimationInterval) {
                    animComposer.reset();
                    triggerIdleAnimation();
                    idleTimer = 0f; // Reset idle timer
                }
            } else if (velocity.length() > 0.2f) { // Add buffer to avoid resetting idleTimer unnecessarily
                idleTimer = 0f;
            }
        } else {
            // Squirrel is in the air
            idleTimer = 0f; // Reset idle timer
            if (velocity.y > 0) {
                // Ascending
                if (!isJumping) {
                    playAnimation("Jump.Fly"); // Play flying animation
                    System.out.println("flying upwards");
                    isJumping = true;
                }
            } else if (velocity.y < 0.02f) {
                // Descending
                if (!isFlying) {
                    playAnimation("Jump.Fly"); // Play flying animation
                    System.out.println("flying downwards");
                    isFlying = true;
                }
            }
        }
    }

    private void startJump() {
        if (animComposer != null) {
            if (isJumping || isFlying) {
                // Interrupt the current jump
                animComposer.reset(); // Stop current animation
            }

            // Start a new jump
            isJumping = true; // Set jumping state
            playAnimation("Jump.Begin"); // Play the jump start animation
            cameraShake(1f, 1f); // Add camera shake for effect
        }
    }
    
    private void cameraShake(float intensity, float duration) {
        isShaking = true;
        shakeIntensity = intensity;
        shakeDuration = duration;
        shakeTimeElapsed = 0f;

        if (originalCameraPosition == null) {
            originalCameraPosition = cam.getLocation().clone(); // Save the original position
        }
    }
    
    private void processCameraShake(float tpf) {
        if (isShaking) {
            shakeTimeElapsed += tpf;

            if (shakeTimeElapsed < shakeDuration) {
                // Generate random offsets for the shake
                float xOffset = (FastMath.rand.nextFloat() - 0.5f) * shakeIntensity;
                float yOffset = (FastMath.rand.nextFloat() - 0.5f) * shakeIntensity;

                // Apply the shake to the camera's position
                cam.setLocation(originalCameraPosition.add(xOffset, yOffset, 0));
            } else {
                // Shake is complete, reset camera position
                cam.setLocation(originalCameraPosition);
                isShaking = false;
            }
        }
    }
    
    /**
     * Helper function that plays the specified animation in the animComposer list. Do nothing if not found
     * @param animationName 
     */
    private void playAnimation(String animationName) {
        if (animComposer == null) {
            System.out.println("AnimComposer is null.");
            return;
        }

        // Check if the animation is already playing
        if (animationName.equals(currentAnimation)) {
            System.out.println("Animation " + animationName + " is already playing. Skipping.");
            return;
        }

        if (animComposer.getAnimClipsNames().contains(animationName)) {
            animComposer.setCurrentAction(animationName);
            currentAnimation = animationName; // Update the current animation tracker
            System.out.println("Playing animation: " + animationName);
        } else {
            System.out.println("Animation " + animationName + " not found.");
        }
    }



    private void stopAnimation(String animationName) {
        if (animComposer != null && animComposer.getAnimClipsNames().contains(animationName)) {
            animComposer.reset(); // Stops the current animation
            System.out.println("Stopping animation: " + animationName);
            currentAnimation = null; // Reset the current animation tracker
        }
    }

    
    private void triggerIdleAnimation() {
        int idleAnimationNumber = 5; // Total idle animations available
        if (animComposer != null) {
            // Randomly select an idle animation
            int randomIndex = (int) (Math.random() * idleAnimationNumber);
            String idleAnimation = "Idle.00" + randomIndex;

            if (animComposer.getCurrentAction() == null) {
                playAnimation(idleAnimation);
                // System.out.println("Playing idle animation: " + idleAnimation);
            }
        }
    }

    public int getCollectedAcorns() {
        return collectedAcorns;
    }


}
