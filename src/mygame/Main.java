package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.input.*;
import com.jme3.input.controls.*;
import com.jme3.math.Vector3f;

/**
 * This is the Main Class of your Game. 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class Main extends SimpleApplication {
    
    // Movement triggers
    private final static Trigger TRIGGER_RUN_FORWARD = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_RUN_BACKWARD = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_RUN_LEFT = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_RUN_RIGHT = new KeyTrigger(KeyInput.KEY_D);

    // Climbing triggers
    private final static Trigger TRIGGER_CLIMB_UP = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_CLIMB_DOWN = new KeyTrigger(KeyInput.KEY_LSHIFT);
    
    // Mappings
    private final static String MAPPING_RUN_FORWARD = "Run Forward";
    private final static String MAPPING_RUN_BACKWARD = "Run Backward";
    private final static String MAPPING_RUN_LEFT = "Run Left";
    private final static String MAPPING_RUN_RIGHT = "Run Right";
    
    private final static String MAPPING_CLIMB_UP = "Climb Up";
    private final static String MAPPING_CLIMB_DOWN = "Climb Down";
    
    // Squirrel geometry object
    private Geometry squirrelGeom;
    
    @Override
    public void simpleInitApp() {
        // Input mappings for movement and climbing
        inputManager.addMapping(MAPPING_RUN_FORWARD, TRIGGER_RUN_FORWARD);
        inputManager.addMapping(MAPPING_RUN_BACKWARD, TRIGGER_RUN_BACKWARD);
        inputManager.addMapping(MAPPING_RUN_LEFT, TRIGGER_RUN_LEFT);
        inputManager.addMapping(MAPPING_RUN_RIGHT, TRIGGER_RUN_RIGHT);
        inputManager.addMapping(MAPPING_CLIMB_UP, TRIGGER_CLIMB_UP);
        inputManager.addMapping(MAPPING_CLIMB_DOWN, TRIGGER_CLIMB_DOWN);
        
        // Add the input listener for the mappings
        inputManager.addListener(actionListener, 
            MAPPING_CLIMB_UP, MAPPING_CLIMB_DOWN);
        inputManager.addListener(analogListener, 
            MAPPING_RUN_FORWARD, MAPPING_RUN_BACKWARD, MAPPING_RUN_LEFT, MAPPING_RUN_RIGHT);
        
        initializeSquirrelAndTree();
        
        attachCenterMark(); // Can modify the center mark later; show boundaries for targets instead?
        
    }
    
    /*
    * Simple initialization method that creates a box squirrel and a few box trees
    */
    private void initializeSquirrelAndTree(){
        // Squirrel Box: unit box
        Box squirrelBox = new Box(1, 1, 1); 
        squirrelGeom = new Geometry("Squirrel", squirrelBox); // Temporal geom object for squirrel
        Material squirrelMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        squirrelMat.setColor("Color", ColorRGBA.Brown);  // Set a brown color for the squirrel
        squirrelGeom.setMaterial(squirrelMat);

        squirrelGeom.setLocalTranslation(0, 1, 0); // Position the squirrel box on the ground (centered at (0, 0, 0))
        
        rootNode.attachChild(squirrelGeom);
        
        // Tree Box: tall box
        Box treeBox = new Box(1, 5, 1); 
        Geometry treeGeom = new Geometry("Tree", treeBox); // Temporal geom object for a tree
        Material treeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        treeMat.setColor("Color", ColorRGBA.Green);  // Set a green color for the tree
        treeGeom.setMaterial(treeMat);

        treeGeom.setLocalTranslation(5, 0, 0);  // Move it up so it’s above the ground and next to the squirrel

        rootNode.attachChild(treeGeom);
    }

    /*
    * Action listener to handle climbing actions
    */
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                if (name.equals(MAPPING_CLIMB_UP) && !isPressed) {
                    squirrelGeom.move(0, 5 * tpf, 0);
                } else if (name.equals(MAPPING_CLIMB_DOWN) && !isPressed) {
                    squirrelGeom.move(0, -5 * tpf, 0);
                }
            }
        }
    };

    /*
    * Analog listener to handle movement actions
    */
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if (name.equals(MAPPING_RUN_FORWARD)) {
                squirrelGeom.move(0, 0, -5 * tpf);
            } else if (name.equals(MAPPING_RUN_BACKWARD)) {
                squirrelGeom.move(0, 0, 5 * tpf);
            } else if (name.equals(MAPPING_RUN_LEFT)) {
                squirrelGeom.move(-5 * tpf, 0, 0);
            } else if (name.equals(MAPPING_RUN_RIGHT)) {
                squirrelGeom.move(5 * tpf, 0, 0);
            }
        }
    };
    
    /*
    * Method that provides a convenient way to mass produce cubes
    */
    private static Box mesh = new Box(Vector3f.ZERO, 1, 1, 1);
    public Geometry myCube(String name, Vector3f loc, ColorRGBA color)
    {
        Geometry geom = new Geometry(name, mesh);
        Material mat = new Material(assetManager,
            "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        return geom;
    }
    
    /*
    * Method that attaches a square center mark for picking
    */
    private void attachCenterMark() {
        Geometry c = myCube("center mark",Vector3f.ZERO, ColorRGBA.White);
        c.scale(2);
        c.setLocalTranslation( settings.getWidth()/2, settings.getHeight()/2, 0 );
        guiNode.attachChild(c); // Attach to 2D user interface
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
