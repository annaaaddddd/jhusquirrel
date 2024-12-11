package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.renderer.RenderManager;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This is the Main Class of your Game. 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class Main extends SimpleApplication {
    
    @Override
    public void simpleInitApp() {
        setDisplayFps(false);                         // hide frames-per-sec display
        setDisplayStatView(false);                    // hide debug statistics display
        
        StartScreenAppState startScreen = new StartScreenAppState();
        stateManager.attach(startScreen);
        
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        this.stateManager.attach(screenShotState);
    }

    public static void main(String[] args) {
        Logger tangentLogger = Logger.getLogger("com.jme3.util.TangentBinormalGenerator");
        tangentLogger.setLevel(Level.SEVERE); // Suppress warnings for tangents
        
        Logger animLogger = Logger.getLogger("com.jme3.anim.SkinningControl");
        animLogger.setLevel(Level.WARNING); // Suppress info logs for skinning
        
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
