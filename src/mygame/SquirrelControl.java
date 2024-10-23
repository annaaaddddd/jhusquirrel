package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.List;

/**
 * This is the Control for Squirrel. 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class SquirrelControl extends AbstractControl {

    private Camera cam;
    private Vector3f cameraOffset = new Vector3f(0, 5, 10); // Camera offset with respect to the squirrel
    private boolean canClimb = false; // Flag to check if climbing is allowed
    private float climbDistanceThreshold = 5.0f; // Distance threshold for climbing
    private List<Spatial> trees; // Reference to tree spatial objects
    
    public SquirrelControl(Camera cam, List<Spatial> trees) {
        this.cam = cam;
        this.trees = trees; // Initialize list of trees
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateCameraPosition();
        checkProximityToTree(); // Check if the squirrel is near any tree
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    /**
     * Method that allows third perspective by updating camera position with the movement of squirrel
     */
    private void updateCameraPosition() {
        Vector3f squirrelPos = spatial.getWorldTranslation();
        Vector3f camPos = squirrelPos.add(cameraOffset);
        cam.setLocation(camPos);
        cam.lookAt(squirrelPos, Vector3f.UNIT_Y);
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
            // Implement climbing behavior here
            System.out.println("Climbing the tree...");
        } else {
            System.out.println("Too far from a tree to climb.");
        }
    }


}

