package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * This is the Control for Squirrel. 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class SquirrelControl extends AbstractControl {

    private Camera cam;
    private Vector3f cameraOffset = new Vector3f(0, 5, 10); // Camera offset with respect to the squirrel
    
    public SquirrelControl(Camera cam) {
        this.cam = cam; 
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateCameraPosition();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

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
}
