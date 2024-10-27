package mygame;

import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.List;

/**
 * This is the Control for Squirrel.
 * Handles movement and camera rotation to give a third-person POV experience.
 * Authors: Anna Dai, Serena Hu, and Leo Zhuang
 */
public class SquirrelControl extends AbstractControl {

    private Camera cam;
    private Vector3f cameraOffset = new Vector3f(0, 5, 10); // Initial camera offset with respect to the squirrel
    private boolean canClimb = false; // Flag to check if climbing is allowed
    private float climbDistanceThreshold = 5.0f; // Distance threshold for climbing
    private List<Spatial> trees; // Reference to tree spatial objects
    private float sensitivity = 0.2f; // Sensitivity for mouse movement
    private InputManager inputManager; // Reference to input manager for capturing mouse input
    private float yaw = 0f; // Camera yaw rotation angle around the squirrel
    private float pitch = 10f; // Camera pitch angle

    public SquirrelControl(Camera cam, List<Spatial> trees, InputManager inputManager) {
        this.cam = cam;
        this.trees = trees; // Initialize list of trees
        this.inputManager = inputManager; // Reference to input manager

        // Initialize the mouse control
        setupMouseControl();
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateCameraPosition();
        updateSquirrelRotation(); // Update squirrel rotation to face forward
        checkProximityToTree(); // Check if the squirrel is near any tree
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Not used in this case
    }

    /**
     * Method to update camera position and direction based on the squirrel's position and camera angles.
     */
    private void updateCameraPosition() {
        Vector3f squirrelPos = spatial.getWorldTranslation();

        // Calculate the new camera position based on yaw and pitch
        float offsetX = cameraOffset.z * FastMath.sin(yaw);
        float offsetZ = cameraOffset.z * FastMath.cos(yaw);
        float offsetY = cameraOffset.y * FastMath.sin(pitch);

        // Set the camera position based on the offsets
        Vector3f camPos = squirrelPos.add(offsetX, offsetY, offsetZ);
        cam.setLocation(camPos);

        // Make the camera look at the squirrel's position
        cam.lookAt(squirrelPos, Vector3f.UNIT_Y);
    }

    /**
     * Method to update squirrel rotation to face the camera's forward direction.
     */
    private void updateSquirrelRotation() {
        // Calculate the new rotation for the squirrel to face forward
        Quaternion newRotation = new Quaternion().fromAngleAxis(yaw, Vector3f.UNIT_Y);
        spatial.setLocalRotation(newRotation); // Update the squirrel's rotation
    }

    public void moveForward(float tpf) {
        spatial.move(0, 0, -5 * tpf);
    }

    public void moveBackward(float tpf) {
        spatial.move(0, 0, 5 * tpf);
    }

    public void moveLeft(float tpf) {
        spatial.move(-5 * tpf, 0, 0);
    }

    public void moveRight(float tpf) {
        spatial.move(5 * tpf, 0, 0);
    }

    public void climbUp(float tpf) {
        spatial.move(0, 5 * tpf, 0);
    }

    public void climbDown(float tpf) {
        spatial.move(0, -5 * tpf, 0);
    }

    // Check if the squirrel is near any tree
    private void checkProximityToTree() {
        Vector3f squirrelPos = spatial.getWorldTranslation();
        float closestDistance = Float.MAX_VALUE;

        for (Spatial tree : trees) {
            Vector3f treePos = tree.getWorldTranslation();
            float distance = squirrelPos.distance(treePos);
            if (distance < closestDistance) {
                closestDistance = distance;
            }
        }

        if (closestDistance <= climbDistanceThreshold) {
            canClimb = true;  // If the squirrel is within the threshold, climbing is allowed
        } else {
            canClimb = false; // If too far from trees, climbing is disabled
        }
    }

    // Climb the tree if within proximity
    public void climbTree() {
        if (canClimb) {
            System.out.println("Climbing the tree...");
        } else {
            System.out.println("Too far from a tree to climb.");
        }
    }

    // Set up mouse control to rotate the camera around the squirrel
    private void setupMouseControl() {

        inputManager.setCursorVisible(false);
        inputManager.addMapping("MouseLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("MouseRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addListener(analogListener, "MouseLeft", "MouseRight", "MouseUp", "MouseDown");
    }

    // Listener to handle mouse movement
    private final AnalogListener analogListener = (name, value, tpf) -> {
        if (name.equals("MouseLeft")) {
            yaw += value * sensitivity; 
        } else if (name.equals("MouseRight")) {
            yaw -= value * sensitivity; 
        } else if (name.equals("MouseUp")) {
            pitch += value * sensitivity; // Rotate camera upward
            pitch = FastMath.clamp(pitch, -FastMath.HALF_PI, FastMath.HALF_PI);
        } else if (name.equals("MouseDown")) {
            pitch -= value * sensitivity; // Rotate camera downward
            pitch = FastMath.clamp(pitch, -FastMath.HALF_PI, FastMath.HALF_PI); 
        }
        updateCameraPosition(); 
        updateSquirrelRotation(); 
    };
}
