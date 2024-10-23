package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
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
    private Geometry squirrelGeom;
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
    public void initialize(AppStateManager stateManager,Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.assetManager = this.app.getAssetManager();
        trees = new ArrayList<>();  // Initialize the list to hold trees
        
        startGame();
    }

    /**
     * Initialize game level and logic(creating level content, setting up physics, etc.
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
            SquirrelControl control = squirrelGeom.getControl (SquirrelControl.class);
            if (name.equals(MAPPING_RUN_FORWARD)) {
                control.moveForward(tpf);
                // System.out.println("You triggered: Run Forward");
            } else if (name.equals(MAPPING_RUN_BACKWARD)) {
                control.moveBackward(tpf);
                // System.out.println("You triggered: Run Backward");
            } else if (name.equals(MAPPING_RUN_LEFT)) {
                control.moveLeft(tpf);
                // System.out.println("You triggered: Run Left");
            } else if (name.equals(MAPPING_RUN_RIGHT)) {
                control.moveRight(tpf);
                // System.out.println("You triggered: Run Right");
            } else if (name.equals(MAPPING_CLIMB_UP)) {
                control.climbUp(tpf);
                // System.out.println("You triggered: Climb up");
            } else if (name.equals(MAPPING_CLIMB_DOWN)) {
                control.climbDown(tpf);
                // System.out.println("You triggered: Climb down");
            }
        }
    };

    /**
     * Method to creates squirrel box and campus assets (currently as boxes)
     */
    private void initializeSquirrelAndCampus() {
        // Create a parent node for both the squirrel and environment objects
        Node campusNode = new Node("CampusNode");

        // Add the squirrel
        addSquirrel(campusNode);

        // Add trees manually and add them to the trees list
        Spatial tree1 = createTree(campusNode, 5, 2.5f, 0);
        trees.add(tree1);  // Add tree to list

        Spatial tree2 = createTree(campusNode, 10, 2.5f, -3);
        trees.add(tree2);

        Spatial tree3 = createTree(campusNode, -5, 2.5f, 5);
        trees.add(tree3);

        // Add buildings around the quad
        createBuilding(campusNode, 10, 3, 10);  // Building 1
        createBuilding(campusNode, -10, 3, 10); // Building 2
        createBuilding(campusNode, 10, 3, -10); // Building 3
        createBuilding(campusNode, -10, 3, -10); // Building 4

        // Add the quad in the center
        createQuad(campusNode, 0, 0.01f, 0); // Flat box for the quad

        // Attach the campus node to the root node
        rootNode.attachChild(campusNode);
    }

    /**
     * Method to add the squirrel
     * @param parentNode 
     */
    private void addSquirrel(Node parentNode) {
        Box squirrelBox = new Box(1, 1, 1);
        squirrelGeom = new Geometry("Squirrel", squirrelBox);
        Material squirrelMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        squirrelMat.setColor("Color", ColorRGBA.Brown);
        squirrelGeom.setMaterial(squirrelMat);
        squirrelGeom.setLocalTranslation(0, 1, 0);
        squirrelGeom.setLocalScale(0.3f);

        // Pass the list of trees to SquirrelControl
        SquirrelControl squirrelControl = new SquirrelControl(cam, trees);
        squirrelGeom.addControl(squirrelControl);

        parentNode.attachChild(squirrelGeom);
    }

    /**
     * Method to create green tree boxes at given coordinate and under given parentNode.
     * @param parentNode
     * @param x - x coordinate of its location
     * @param y - y coordinate of its location
     * @param z - z zoordinate of its location
     */
    private Spatial createTree(Node parentNode, float x, float y, float z) {
        Box treeBox = new Box(1, 5, 1);
        Geometry treeGeom = new Geometry("Tree", treeBox);
        Material treeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        treeMat.setColor("Color", ColorRGBA.Green);
        treeGeom.setMaterial(treeMat);
        treeGeom.setLocalTranslation(x, y, z);
        parentNode.attachChild(treeGeom);
        return treeGeom;  // Return the tree geometry so we can track it
    }

    /**
     * Method to create boxlike buildings at given coordinate and under given parentNode.
     * @param parentNode
     * @param x - x coordinate of its location
     * @param y - y coordinate of its location
     * @param z - z zoordinate of its location
     */
    private void createBuilding(Node parentNode, float x, float y, float z) {
        Box buildingBox = new Box(3, 6, 3); // Larger dimensions for a building
        Geometry buildingGeom = new Geometry("Building", buildingBox);
        Material buildingMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        buildingMat.setColor("Color", ColorRGBA.White);  // White color for the building
        buildingGeom.setMaterial(buildingMat);

        buildingGeom.setLocalTranslation(x, y, z); // Set building position
        parentNode.attachChild(buildingGeom);
    }

    /**
     * Method to create a flat quad (representing the central grassy area) at 
     * given coordinate and under given parentNode.
     * @param parentNode
     * @param x - x coordinate of its location
     * @param y - y coordinate of its location
     * @param z - z zoordinate of its location
     */
    private void createQuad(Node parentNode, float x, float y, float z) {
        Box quadBox = new Box(10, 0.01f, 10); // Wide and flat box for the quad
        Geometry quadGeom = new Geometry("Quad", quadBox);
        Material quadMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        quadMat.setColor("Color", ColorRGBA.LightGray);  // Light Gray color to represent quad
        quadGeom.setMaterial(quadMat);

        quadGeom.setLocalTranslation(x, y, z); // Center the quad at the origin
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
