package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * AppState for running the game.
 * 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class GameRunningAppState extends AbstractAppState {
    private SimpleApplication app;
    private Node rootNode;
    private Camera cam;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Spatial squirrelModel;  // Updated to use squirrelModel instead of squirrelGeom
    private List<Spatial> trees; // List to store tree references
    
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
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.assetManager = this.app.getAssetManager();
        trees = new ArrayList<>();  // Initialize the list to hold trees

        // Ambient light to make sure the model is visible
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(ambient);
        
        // Directional light to simulate sunlight
        /*
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        rootNode.addLight(sun);
        */

        
        startGame();
    }

    /**
     * Initialize game level and logic (creating level content, setting up physics, etc.
     * Potential TODO: Add a WorldManagerState object that attaches nodes to the rootNode object, 
     * a PhysicsState object that handles falling and colliding objects, and a 
     * ScreenshotAppState object that saves rendered scenes as image files to the user's desktop
     */
    private void startGame() {
        // Initialize squirrel, buildings, trees, and the quad
        initializeSquirrelAndCampus();
        addMapping();
        attachCenterMark();   
    }
    
    /**
     * Method that add mapping for keyboard triggers
     */
    private void addMapping() {
        if (!inputManager.hasMapping(MAPPING_RUN_FORWARD)) {
            inputManager.addMapping(MAPPING_RUN_FORWARD, TRIGGER_RUN_FORWARD);
            inputManager.addMapping(MAPPING_RUN_BACKWARD, TRIGGER_RUN_BACKWARD);
            inputManager.addMapping(MAPPING_RUN_LEFT, TRIGGER_RUN_LEFT);
            inputManager.addMapping(MAPPING_RUN_RIGHT, TRIGGER_RUN_RIGHT);
            inputManager.addMapping(MAPPING_CLIMB_UP, TRIGGER_CLIMB_UP);
            inputManager.addMapping(MAPPING_CLIMB_DOWN, TRIGGER_CLIMB_DOWN);
        }
        
        inputManager.addListener(analogListener, 
                MAPPING_RUN_FORWARD, MAPPING_RUN_BACKWARD, MAPPING_RUN_LEFT, MAPPING_RUN_RIGHT, 
                MAPPING_CLIMB_UP, MAPPING_CLIMB_DOWN);  
    }
    
    /**
     * Analog listener to handle movement actions
     */
    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            SquirrelControl control = squirrelModel.getControl(SquirrelControl.class);
            //if (control == null) {
                //System.out.println("Control is null, skipping movement.");
                //return;
            //}
            if (name.equals(MAPPING_RUN_FORWARD)) {
                control.moveForward(tpf);
            } else if (name.equals(MAPPING_RUN_BACKWARD)) {
                control.moveBackward(tpf);
            } else if (name.equals(MAPPING_RUN_LEFT)) {
                control.moveLeft(tpf);
            } else if (name.equals(MAPPING_RUN_RIGHT)) {
                control.moveRight(tpf);
            } else if (name.equals(MAPPING_CLIMB_UP)) {
                control.climbUp(tpf);
            } else if (name.equals(MAPPING_CLIMB_DOWN)) {
                control.climbDown(tpf);
            }
        }
    };

    /**
     * Method to create squirrel model and campus assets (currently as boxes)
     */
    private void initializeSquirrelAndCampus() {
        // Create a parent node for both the squirrel and environment objects
        Node campusNode = new Node("CampusNode");

        // Add the squirrel
        addSquirrel(campusNode);

        // Add trees manually and add them to the trees list
        Spatial tree1 = createTree(campusNode, 5, 2.5f, 0);
        trees.add(tree1);

        Spatial tree2 = createTree(campusNode, 10, 2.5f, -3);
        trees.add(tree2);

        Spatial tree3 = createTree(campusNode, -5, 2.5f, 5);
        trees.add(tree3);

        // Add the quad in the center
        createQuad(campusNode, 0, 0.01f, 0); // Flat box for the quad

        // Attach the campus node to the root node
        rootNode.attachChild(campusNode);
    }
    
private void addSquirrel(Node parentNode) {
    // Load the squirrel model with animations from the Squirrel2 folder
    System.out.println("Loading squirrel model...");
    squirrelModel = assetManager.loadModel("Textures/Squirrel2/squirrel-anim.j3o");

    if (squirrelModel == null) {
        System.out.println("Failed to load squirrel model!");
    } else {
        System.out.println("Squirrel model loaded successfully.");
    }

    // Set textures from the Squirrel2 folder
    Material squirrelMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    squirrelMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Squirrel2/squirrel-body.png"));
    squirrelModel.setMaterial(squirrelMaterial);

    // Position, scale, and rotation adjustments
    squirrelModel.setLocalTranslation(0, 1, 0);
    squirrelModel.setLocalScale(0.3f); // Adjust scale if needed
    squirrelModel.rotate(0, (float)Math.PI, 0);  // Rotate to face forward if necessary

    // Attach the model to the parent node
    parentNode.attachChild(squirrelModel);
    
    com.jme3.anim.SkinningControl skinningControl = squirrelModel.getControl(com.jme3.anim.SkinningControl.class);
    if (skinningControl != null) {
        // Use skinningControl for animation-related functionality
        System.out.println("SkinningControl found. Attempting to engage animations.");
    } else {
        System.out.println("No AnimComposer or SkinningControl found for the squirrel model.");
    }
    

    // Add control for squirrel-specific movement
    SquirrelControl squirrelControl = new SquirrelControl(cam, trees, inputManager);
    squirrelModel.addControl(squirrelControl);
}






    private Spatial createTree(Node parentNode, float x, float y, float z) {
        Box treeBox = new Box(1, 5, 1);
        Geometry treeGeom = new Geometry("Tree", treeBox);
        
        // Load and set a tree bark texture
        Material treeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        treeMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/tree-bark.jpg")); 
        treeGeom.setMaterial(treeMat);
        
        treeGeom.setLocalTranslation(x, y, z);
        parentNode.attachChild(treeGeom);
        return treeGeom;  // Return the tree geometry so we can track it
    }
    
    private void createQuad(Node parentNode, float x, float y, float z) {
        // Flat box for the quad
        Box quad = new Box(10, 0.1f, 10);
        Geometry quadGeom = new Geometry("Quad", quad);
        
        // Apply a grass or dirt texture to the ground
        Material quadMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        quadMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Ground.jpg")); // Use a ground texture
        quadGeom.setMaterial(quadMat);
        
        quadGeom.setLocalTranslation(x, y, z);
        parentNode.attachChild(quadGeom);
    }
    
    /**
     * Method that attaches a square center mark for picking
     */
    private void attachCenterMark() {
        Geometry c = new Geometry("center mark", new Box(1, 1, 1));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        c.setMaterial(mat);
        c.scale(2);
        
        int screenWidth = app.getContext().getSettings().getWidth();
        int screenHeight = app.getContext().getSettings().getHeight();
        c.setLocalTranslation(screenWidth / 2, screenHeight / 2, 0);
        
        app.getGuiNode().attachChild(c); // Attach to 2D user interface
    }


    @Override
    public void cleanup() {
        super.cleanup();
        rootNode.detachAllChildren();
        inputManager.removeListener(analogListener);
    }
}
