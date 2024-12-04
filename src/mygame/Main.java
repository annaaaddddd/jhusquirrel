package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.renderer.RenderManager;

/**
 * This is the Main Class of your Game. 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class Main extends SimpleApplication {
    private AudioManager audioManager;
    
    @Override
    public void simpleInitApp() {
        StartScreenAppState runningGame = new StartScreenAppState();
        stateManager.attach(runningGame);
        
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        this.stateManager.attach(screenShotState);
    }

    public static void main(String[] args) {
        Main app = new Main();
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
