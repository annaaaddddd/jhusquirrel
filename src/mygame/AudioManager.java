package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;

/**
 * This is the Audio Manager 
 * @author Serena Hu
 */
public class AudioManager extends SimpleApplication {
    
    @Override
    public void simpleInitApp() {
        StartScreenAppState runningGame = new StartScreenAppState();
        stateManager.attach(runningGame);
    }

    public static void main(String[] args) {
        AudioManager app = new AudioManager();
        app.start();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
