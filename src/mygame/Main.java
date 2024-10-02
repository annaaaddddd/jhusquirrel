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
import com.jme3.scene.Node;

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
        //initializeSquirrelAndTree();
        // Initialize squirrel, buildings, trees, and the quad
        initializeSquirrelAndCampus();

        attachCenterMark();

        //Leo: I added squirrel control here
        SquirrelControl squirrelControl = new SquirrelControl(cam);
        squirrelGeom.addControl(squirrelControl);

        inputManager.addMapping(MAPPING_RUN_FORWARD, TRIGGER_RUN_FORWARD);
        inputManager.addMapping(MAPPING_RUN_BACKWARD, TRIGGER_RUN_BACKWARD);
        inputManager.addMapping(MAPPING_RUN_LEFT, TRIGGER_RUN_LEFT);
        inputManager.addMapping(MAPPING_RUN_RIGHT, TRIGGER_RUN_RIGHT);
        inputManager.addMapping(MAPPING_CLIMB_UP, TRIGGER_CLIMB_UP);
        inputManager.addMapping(MAPPING_CLIMB_DOWN, TRIGGER_CLIMB_DOWN);
        //Leo: I added this listener
        inputManager.addListener(analogListener, MAPPING_RUN_FORWARD, MAPPING_RUN_BACKWARD, MAPPING_RUN_LEFT, MAPPING_RUN_RIGHT, MAPPING_CLIMB_UP, MAPPING_CLIMB_DOWN);  
        
    }
    
    private void initializeSquirrelAndCampus() {
        // Create a parent node for both the squirrel and environment objects
        Node campusNode = new Node("CampusNode");

        // Add the squirrel
        addSquirrel(campusNode);

        // Add trees manually around the campus
        createTree(campusNode, 5, 2.5f, 0);  // Tree near the center
        createTree(campusNode, 10, 2.5f, -3); // Tree near the quad
        createTree(campusNode, -5, 2.5f, 5);  // Tree near a building

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

    // Method to add the squirrel
    private void addSquirrel(Node parentNode) {
        Box squirrelBox = new Box(1, 1, 1);
        squirrelGeom = new Geometry("Squirrel", squirrelBox); // Geometry for squirrel
        Material squirrelMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        squirrelMat.setColor("Color", ColorRGBA.Brown);  // Set a brown color for the squirrel
        squirrelGeom.setMaterial(squirrelMat);
        squirrelGeom.setLocalTranslation(0, 1, 0); // Position the squirrel above the ground
        parentNode.attachChild(squirrelGeom); // Attach the squirrel to the parent node
    }

    // Method to create trees
    private void createTree(Node parentNode, float x, float y, float z) {
        Box treeBox = new Box(1, 5, 1);
        Geometry treeGeom = new Geometry("Tree", treeBox);
        Material treeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        treeMat.setColor("Color", ColorRGBA.Green);
        treeGeom.setMaterial(treeMat);
        
        treeGeom.setLocalTranslation(x, y, z);
        parentNode.attachChild(treeGeom);
    }

    // Method to create buildings
    private void createBuilding(Node parentNode, float x, float y, float z) {
        Box buildingBox = new Box(3, 6, 3); // Larger dimensions for a building
        Geometry buildingGeom = new Geometry("Building", buildingBox);
        Material buildingMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        buildingMat.setColor("Color", ColorRGBA.White);  // White color for the building
        buildingGeom.setMaterial(buildingMat);

        buildingGeom.setLocalTranslation(x, y, z); // Set building position
        parentNode.attachChild(buildingGeom);
    }

    // Method to create a flat quad (representing the central grassy area)
    private void createQuad(Node parentNode, float x, float y, float z) {
        Box quadBox = new Box(10, 0.01f, 10); // Wide and flat box for the quad
        Geometry quadGeom = new Geometry("Quad", quadBox);
        Material quadMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        quadMat.setColor("Color", ColorRGBA.LightGray);  // Light Gray color to represent quad
        quadGeom.setMaterial(quadMat);

        quadGeom.setLocalTranslation(x, y, z); // Center the quad at the origin
        parentNode.attachChild(quadGeom);
    }

    /*
    * Simple initialization method that creates a box squirrel and a few box trees
    */
    /*
    private void initializeSquirrelAndTree(){
        // Create a parent node for both the squirrel and tree
        Node parentNode = new Node("ParentNode");
        
        // Squirrel Box: unit box
        Box squirrelBox = new Box(1, 1, 1); 
        squirrelGeom = new Geometry("Squirrel", squirrelBox); // Temporal geom object for squirrel
        Material squirrelMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        squirrelMat.setColor("Color", ColorRGBA.Brown);  // Set a brown color for the squirrel
        squirrelGeom.setMaterial(squirrelMat);
        squirrelGeom.setLocalTranslation(0, 1, 0); // Position the squirrel box on the ground (centered at (0, 0, 0))
                
        // Tree Box: tall box
        Box treeBox = new Box(1, 5, 1); 
        Geometry treeGeom = new Geometry("Tree", treeBox); // Temporal geom object for a tree
        Material treeMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        treeMat.setColor("Color", ColorRGBA.Green);  // Set a green color for the tree
        treeGeom.setMaterial(treeMat);
        treeGeom.setLocalTranslation(5, 0, 0);  // Move it up so it’s above the ground and next to the squirrel

        // Attach squirrel and tree to the parent node
        parentNode.attachChild(squirrelGeom);
        parentNode.attachChild(treeGeom);

        // Apply a transformation to the parent node
        parentNode.setLocalTranslation(0, 0, 0);

        // Attach the parent node to the root node
        rootNode.attachChild(parentNode);
    }
    */
    
    
    /*
    * Action listener to handle climbing actions
   
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                if (name.equals(MAPPING_CLIMB_UP)) {
                    squirrelGeom.move(0, 5 * tpf, 0);
                    System.out.println("You triggered: Climb Up");
                } else if (name.equals(MAPPING_CLIMB_DOWN) && !isPressed) {
                    squirrelGeom.move(0, -5 * tpf, 0);
                }
            }
        }
    };
     */

    /*
    * Analog listener to handle movement actions
    */
    //Leo: I encapsulated the move function into squirrel control
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            SquirrelControl control = squirrelGeom.getControl (SquirrelControl.class);
            if (name.equals(MAPPING_RUN_FORWARD)) {
                // Move squirrel forward
                squirrelGeom.move(0, 0, -5 * tpf);
                System.out.println("You triggered: Run Forward");
            } else if (name.equals(MAPPING_RUN_BACKWARD)) {
                // Move squirrel backward
                squirrelGeom.move(0, 0, 5 * tpf);
                System.out.println("You triggered: Run Backward");
            } else if (name.equals(MAPPING_RUN_LEFT)) {
                // Move squirrel left
                squirrelGeom.move(-5 * tpf, 0, 0);
                System.out.println("You triggered: Run Left");
            } else if (name.equals(MAPPING_RUN_RIGHT)) {
                // Move squirrel right
                squirrelGeom.move(5 * tpf, 0, 0);
                System.out.println("You triggered: Run Right");
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
