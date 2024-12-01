package mygame;

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
import java.util.ArrayList;
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
    private int collectedAcorns = 0;
    private float sensitivity = 0.2f;
    private InputManager inputManager;
    private float yaw = 0f;
    private float pitch = 10f;
    private RigidBodyControl squirrelPhysics;
    private float targetZLevel = -10f;

    public SquirrelControl(Camera cam, List<Spatial> trees, List<Spatial> acorns, Node rootNode,
                            BitmapText acornCounterText, InputManager inputManager, RigidBodyControl squirrelPhysics) {
        this.cam = cam;
        this.trees = trees;
        this.acorns = acorns;
        this.rootNode = rootNode;
        this.acornCounterText = acornCounterText;
        this.inputManager = inputManager;
        this.squirrelPhysics = squirrelPhysics;
        setupMouseControl();
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getWorldTranslation().z <= targetZLevel) {
            //squirrelPhysics.setGravity(Vector3f.ZERO);
            squirrelPhysics.setGravity(new Vector3f(0, -0.05f, 0));
        } else {
            squirrelPhysics.setGravity(new Vector3f(0, -0.05f, 0));
        }
        updateCameraPosition();
        updateSquirrelRotation();
        checkProximityToTree();
        checkProximityToAcorns();
    }
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // Not used in this case, so leave it empty
    }


    private void checkProximityToAcorns() {
        Vector3f squirrelPos = spatial.getWorldTranslation();
        List<Spatial> collected = new ArrayList<>(); // Temporary list to store collected acorns

        for (Spatial acorn : acorns) {
            float distance = squirrelPos.distance(acorn.getWorldTranslation());
            if (distance < 1.0f) {
                collected.add(acorn); // Mark the acorn for removal
            }
        }

        // Remove collected acorns safely
        for (Spatial acorn : collected) {
            rootNode.detachChild(acorn);
            updateAcornCounter();
            acorns.remove(acorn); 
        }

        // Update the counter text after all acorns are processed
     
    }


    private void updateAcornCounter() {
    if (acornCounterText != null) {
        System.out.println("Updating acorn counter text.");
        collectedAcorns++;
        acornCounterText.setText("Acorns Collected: " + collectedAcorns);
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
        squirrelPhysics.applyCentralForce(cam.getDirection().mult(intensity * 100));
    }

    public void moveBackward(float intensity) {
        squirrelPhysics.applyCentralForce(cam.getDirection().mult(-intensity * 100));
    }

    public void moveLeft(float intensity) {
        squirrelPhysics.applyCentralForce(cam.getLeft().mult(intensity * 100));
    }

    public void moveRight(float intensity) {
        squirrelPhysics.applyCentralForce(cam.getLeft().mult(-intensity * 100));
    }

    public void climbUp(float intensity) {
        //if (canClimb) {
            squirrelPhysics.applyCentralForce(new Vector3f(0, intensity * 100, 0));
        //}
    }

    public void climbDown(float intensity) {
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
}
