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
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.ArrayList;
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
    private static int collectedAcorns = 0;
    
    private float sensitivity = 0.2f;
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
    
    // Camera shake related fields
    private boolean isShaking = false;
    private float shakeIntensity = 0f;
    private float shakeDuration = 0f;
    private float shakeTimeElapsed = 0f;
    private Vector3f originalCameraPosition = null;


    public SquirrelControl(Camera cam, List<Spatial> trees, List<Spatial> acorns, Node rootNode,
                            BitmapText acornCounterText, InputManager inputManager, RigidBodyControl squirrelPhysics, 
                            AssetManager assetManager, AnimComposer animComposer) {
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
        
        if (spatial.getWorldTranslation().z <= targetZLevel) {
            //squirrelPhysics.setGravity(Vector3f.ZERO);
            squirrelPhysics.setGravity(new Vector3f(0, -0.05f, 0));
        } else {
            squirrelPhysics.setGravity(new Vector3f(0, -0.05f, 0));
        }
        
        // Ensure the squirrel does not fall below the ground level
        if (spatial.getWorldTranslation().y < 0) {
            Vector3f currentPos = spatial.getLocalTranslation();
            spatial.setLocalTranslation(currentPos.x, 0, currentPos.z); // Reset to ground level
            squirrelPhysics.setLinearVelocity(Vector3f.ZERO); // Stop downward motion
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
        System.out.println("Congratulations! You have collected all the acorns!");

        // Display a congratulatory message on the screen
        BitmapText congratsText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
        congratsText.setSize(40); // Adjust font size
        congratsText.setColor(ColorRGBA.Yellow);
        congratsText.setText("Congratulations!\nYou have collected all the acorns!");
        congratsText.setLocalTranslation(cam.getWidth() / 2 - 200, cam.getHeight() / 2, 0); // Centered on the screen
        rootNode.attachChild(congratsText);
    }


    private void updateAcornCounter(int count) {
        if (acornCounterText != null) {
            // Convert count to Roman numeral using the hardcoded method
            String romanCount = toRomanNumeral(count);

            // Update the text directly
            acornCounterText.setText("Acorns Collected: " + romanCount);

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
        squirrelPhysics.applyCentralForce(cam.getDirection().mult(intensity * 100));
    }

    public void moveBackward(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.applyCentralForce(cam.getDirection().mult(-intensity * 100));
    }

    public void moveLeft(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.applyCentralForce(cam.getLeft().mult(intensity * 100));
    }

    public void moveRight(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.applyCentralForce(cam.getLeft().mult(-intensity * 100));
    }

    public void climbUp(float intensity) {
        if (!isJumping) {
            startJump();
        }
        //if (canClimb) {
            squirrelPhysics.applyCentralForce(new Vector3f(0, intensity * 100, 0));
        //}
    }

    public void climbDown(float intensity) {
        if (!isJumping) {
            startJump();
        }
        squirrelPhysics.applyCentralForce(new Vector3f(0, -intensity * 100, 0));
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
        inputManager.addMapping("MouseLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MouseRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
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

        // Thresholds for stopping the jump
        float verticalThreshold = 0.01f; // Small vertical velocity threshold
        float totalVelocityThreshold = 0.1f; // Small total velocity threshold

        // Transition to Jump.Fly while in the air
        if (isJumping && !isFlying && velocity.y > verticalThreshold) {
            playAnimation("Jump.Fly");
            isFlying = true;
            playedJumpEnd = false; // Reset the flag to allow Jump.End later
            idleTimer = 0f; // Reset idle timer since the squirrel is moving
        }

        // Transition to Jump.End when landing (but only once)
        if (isFlying && Math.abs(velocity.y) < verticalThreshold && velocity.length() < totalVelocityThreshold) {
            if (!playedJumpEnd) { // Play Jump.End only once
                playAnimation("Jump.End");
                playedJumpEnd = true;
                isJumping = false;
                isFlying = false;
            }
            idleTimer = 0f; // Reset idle timer since landing just occurred
        }

        // If the squirrel is stationary and not jumping or flying, stop movement and trigger idle animations
        if (!isJumping && !isFlying && velocity.length() < totalVelocityThreshold) {
            // Stop residual movement
            squirrelPhysics.setLinearVelocity(Vector3f.ZERO);

            // Increment idle timer
            idleTimer += tpf;

            if (idleTimer >= idleAnimationInterval) {
                triggerIdleAnimation(); // Play idle animation
                idleTimer = 0f; // Reset idle timer
            }
        } else {
            // Reset idle timer when the squirrel starts moving
            idleTimer = 0f;
        }
    }


    
    private void startJump() {
        if (animComposer != null) {
            isJumping = true; // Set jumping state
            playAnimation("Jump.Begin");
            
            cameraShake(1f, 1f);
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
        if (animComposer != null && animComposer.getAnimClipsNames().contains(animationName)) {
            animComposer.setCurrentAction(animationName);
            System.out.println("Playing animation: " + animationName);
        } else {
            System.out.println("Animation " + animationName + " not found.");
        }
    }

    private void stopAnimation(String animationName) {
        if (animComposer != null && animComposer.getAnimClipsNames().contains(animationName)) {
            animComposer.reset(); // Stops the current animation
            System.out.println("Stopping animation: " + animationName);
        }
    }
    
    private void triggerIdleAnimation() {
        int idleAnimationNumber = 5; // Total idle animations available
        if (animComposer != null) {
            // Randomly select an idle animation
            int randomIndex = (int) (Math.random() * idleAnimationNumber);
            String idleAnimation = "Idle.00" + randomIndex;

            // Check if an idle animation is already playing
            String currentAnimation = animComposer.getCurrentAction() != null
                ? animComposer.getCurrentAction().toString() // Get the current animation name
                : "";

            if (!currentAnimation.startsWith("Idle.")) {
                playAnimation(idleAnimation);
                // System.out.println("Playing idle animation: " + idleAnimation);
            }
        }
    }



}
