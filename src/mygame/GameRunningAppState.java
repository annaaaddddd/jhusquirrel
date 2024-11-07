package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.font.BitmapFont;
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
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.Vector3f;
import com.jme3.scene.shape.Quad;
import java.util.ArrayList;
import java.util.List;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.ui.Picture;
import com.jme3.bullet.control.RigidBodyControl; 
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.anim.SkinningControl;
import java.util.LinkedList;
import java.util.Queue;

/**
 * AppState for running the game.
 * 
 * @author Anna Dai, Serena Hu, and Leo Zhuang
 */
public class GameRunningAppState extends AbstractAppState {
    private SimpleApplication app;
    private Node rootNode;
    private Node guiNode;
    private Camera cam;
    private InputManager inputManager;
    private AssetManager assetManager;
    private Spatial squirrelModel;  // Updated to use squirrelModel instead of squirrelGeom
    private Spatial treeModel;
    private List<Spatial> trees; // List to store tree references
    private BulletAppState bulletAppState;
    private AnimComposer composer;
    final private Queue<String> anims = new LinkedList<>();
    private boolean playAnim = true;
    
    private Picture settingsIcon;
    private Picture saveIcon;
    private Geometry missionBlock;
    private BitmapText missionText;
    
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
        this.guiNode = this.app.getGuiNode();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.assetManager = this.app.getAssetManager();
        trees = new ArrayList<>();  // Initialize the list to hold trees

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        // Set global gravity to pull objects downwards
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -0.81f, 0));

        initializeLight();
        startGame();
        createGUI();
    }
    
    /**
     * Initialize light setting. Add ambient light and sunlight (directional) to the scene.
     */
    private void initializeLight() {
        // Ambient light to make sure the model is visible
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(ambient);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        rootNode.addLight(sun);
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
    
    private void createGUI() {
        // Create Settings icon
        settingsIcon = new Picture("Settings Icon");
        settingsIcon.setImage(assetManager, "Interface/In Game GUI/setting.png", true); 
        settingsIcon.setWidth(64);
        settingsIcon.setHeight(64);
        settingsIcon.setPosition(cam.getWidth() - 80, cam.getHeight() - 80); // Top-right corner
        guiNode.attachChild(settingsIcon);

        // Create Save icon
        saveIcon = new Picture("Save Icon");
        saveIcon.setImage(assetManager, "Interface/In Game GUI/save.png", true);
        saveIcon.setWidth(64);
        saveIcon.setHeight(64);
        saveIcon.setPosition(cam.getWidth() - 160, cam.getHeight() - 80); // Next to Settings icon
        guiNode.attachChild(saveIcon);

        // Create and position the Mission Block
        Quad missionQuad = new Quad(300, 150);
        missionBlock = new Geometry("Mission Block", missionQuad);

        // Set up the semi-transparent material for mission block
        Material missionMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        missionMat.setBoolean("UseMaterialColors", true);
        missionMat.setColor("Diffuse", new ColorRGBA(1, 1, 1, 0.5f)); // Semi-transparent white
        missionMat.setColor("Ambient", new ColorRGBA(1, 1, 1, 0.5f));
        missionMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // Enable alpha blending

        missionBlock.setMaterial(missionMat);
        missionBlock.setQueueBucket(RenderQueue.Bucket.Transparent); // Set to transparent bucket

        // Adjust position to ensure it's visible within the screen area
        missionBlock.setLocalTranslation(20, cam.getHeight() - 200, 0); // Adjusted for better visibility
        guiNode.attachChild(missionBlock);

        // Debugging print to confirm mission block is added
        System.out.println("Mission block added at position: " + missionBlock.getLocalTranslation());

        // Create and position mission text
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        missionText = new BitmapText(font, false);
        missionText.setSize(font.getCharSet().getRenderedSize() * 1.2f); // Set font size
        missionText.setColor(ColorRGBA.White); // Text color
        missionText.setText("Missions:\n- Bite 10 students\n- Collect 10 acorns");

        // Position the text inside the mission block, relative to its translation
        missionText.setLocalTranslation(30, cam.getHeight() - 130, 0); // Adjust to align within mission block
        guiNode.attachChild(missionText);

        // Debugging print to confirm mission text is added
        System.out.println("Mission text added at position: " + missionText.getLocalTranslation());
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
        
//        
//        inputManager.addMapping("ToggleToSecondState", new KeyTrigger(KeyInput.KEY_T));
//        inputManager.addListener(actionListener, "ToggleToSecondState");
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
        animateModel(squirrelModel, "squirrel");

        // Add trees manually and add them to the trees list
        Spatial tree1 = createTree(campusNode, 5, 0.5f, 0);
        trees.add(tree1);

        Spatial tree2 = createTree(campusNode, 10, 0.5f, -3);
        trees.add(tree2);

        Spatial tree3 = createTree(campusNode, -10, 0.5f, 5);
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
        
        TextureKey squirrelDiffuse = new TextureKey(
           "Textures/Squirrel2/squirrel-body.png", false);
        squirrelMaterial.setTexture("DiffuseMap",
            assetManager.loadTexture(squirrelDiffuse));
        TextureKey squirrelNormal = new TextureKey(
         "Textures/Squirrel2/squirrel-body-norm.png", false);
        squirrelMaterial.setTexture("NormalMap",
            assetManager.loadTexture(squirrelNormal));
        
        squirrelMaterial.setBoolean("UseMaterialColors",true);
        squirrelMaterial.setColor("Ambient", ColorRGBA.Gray);
        squirrelMaterial.setColor("Diffuse", ColorRGBA.White);
        squirrelModel.setMaterial(squirrelMaterial);
        
        TangentBinormalGenerator.generate(squirrelModel);
        
        // Position, scale, and rotation adjustments
        squirrelModel.setLocalTranslation(0, 1, 0);
        squirrelModel.setLocalScale(0.3f); // Adjust scale if needed
        squirrelModel.rotate(0, (float)Math.PI, 0);  // Rotate to face forward if necessary
        RigidBodyControl squirrelPhysics = new RigidBodyControl(1.0f); // mass > 0
        squirrelModel.addControl(squirrelPhysics);
        bulletAppState.getPhysicsSpace().add(squirrelPhysics);

        // Add control for squirrel-specific movement
        SquirrelControl squirrelControl = new SquirrelControl(cam, trees, inputManager, squirrelPhysics);
        squirrelModel.addControl(squirrelControl);

        parentNode.attachChild(squirrelModel);
    }
    
    /**
     * Recursively search for the AnimComposer of the model.
     * @param model
     * @return AnimComposer
     */
    private AnimComposer findAnimComposer(Spatial model) {
        if (model instanceof Node) {
            for (Spatial child : ((Node) model).getChildren()) {
                AnimComposer composer = child.getControl(AnimComposer.class);
                if (composer != null) {
                    return composer;
                }
                composer = findAnimComposer(child); 
                if (composer != null) {
                    return composer;
                }
            }
        }
        return model.getControl(AnimComposer.class); // Check at the root level as well
    }
    
    
    /**
     * Helper function that plays the first animation in the animComposer list. Do nothing if not found
     * @param model
     * @param modelname 
     */
    private void animateModel(Spatial model, String modelname) {
        composer = findAnimComposer(model); // Use the recursive method to find AnimComposer

        if (composer != null) {
            String animName = composer.getAnimClipsNames().iterator().next(); // Playing one animation
            composer.setCurrentAction(animName);
            composer.setEnabled(true);  // Enable the animation

            System.out.println("Playing animation: " + animName);
        } else {
            System.out.println("No AnimComposer found for the model " + modelname);
        }
    }
    
    /**
     * Helper function that specifically play animation with corresponding animationName. Do nothing if not found
     * @param model
     * @param modelname
     * @param animationName 
     */
    private void animateModel(Spatial model, String modelname, String animationName) {
        composer = model.getControl(AnimComposer.class);

        if (composer != null) {
            // Check if the specified animation name exists
            if (composer.getAnimClipsNames().contains(animationName)) {
                composer.setCurrentAction(animationName);
                composer.setEnabled(true);  // Enable the animation
                System.out.println("Playing animation: " + animationName);
            } else {
                System.out.println("Animation " + animationName + " not found in model " + modelname);
            }
        } else {
            System.out.println("No AnimComposer found for the model " + modelname);
        }
    }

    private Spatial createTree(Node parentNode, float x, float y, float z) {
        Box treeBox = new Box(1, 5, 1);
        Geometry treeGeom = new Geometry("Tree", treeBox);
        //Load and set a tree bark texture
        Material treeMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        treeMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/tree-bark.jpg")); 
        treeGeom.setMaterial(treeMat);
        parentNode.attachChild(treeGeom);
        treeGeom.setLocalTranslation(x, y, z);
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
