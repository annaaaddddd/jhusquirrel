package mygame;

import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.font.BitmapFont;
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
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.controls.ActionListener;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
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
    private ViewPort viewPort;
    
    private Spatial squirrelModel;  // Updated to use squirrelModel instead of squirrelGeom
    private Spatial treeModel;
    private List<Spatial> trees; // List to store tree references
    private BulletAppState bulletAppState;
    private AnimComposer composer;
    final private Queue<String> anims = new LinkedList<>();
    private boolean playAnim = true;
    
    private List<Spatial> acorns = new ArrayList<>(); // List to store acorn geometries
    private int collectedAcorns = 0; // Counter for collected acorns
    private BitmapText acornCounterText; // GUI element to display the counter

    private Picture settingsIcon;
    private Picture saveIcon;
    private Geometry missionBlock;
    private BitmapText missionText;
    
    private AudioNode ambientSound;
    private AudioNode bellSound;
    
    private Vector3f sunDir = new Vector3f(0.1f, -1f, 0.95f);
    private FilterPostProcessor fpp;
    private FogFilter fogFilter;
    private LightScatteringFilter sunLightFilter;
    private SSAOFilter ssaoFilter;
    
    private ParticleEmitter debrisEmitter;
    private float debrisTimer = 0f;
    private boolean debrisTriggered = false; // Flag to ensure the shockwave is emitted only once
    
    private boolean awaitingRestartConfirmation = false;
    private boolean gameCompleted = false;
    private boolean restartInProgress = false;
    
    // Movement triggers
    private final static Trigger TRIGGER_RUN_FORWARD = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_RUN_BACKWARD = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_RUN_LEFT = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_RUN_RIGHT = new KeyTrigger(KeyInput.KEY_D);

    // Climbing triggers
    private final static Trigger TRIGGER_CLIMB_UP = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_CLIMB_DOWN = new KeyTrigger(KeyInput.KEY_LSHIFT);
    
    // Restart trigger
    private final static Trigger TRIGGER_RESTART = new KeyTrigger(KeyInput.KEY_R);
    
    // Mappings
    private final static String MAPPING_RUN_FORWARD = "Run Forward";
    private final static String MAPPING_RUN_BACKWARD = "Run Backward";
    private final static String MAPPING_RUN_LEFT = "Run Left";
    private final static String MAPPING_RUN_RIGHT = "Run Right";
    private final static String MAPPING_CLIMB_UP = "Climb Up";
    private final static String MAPPING_CLIMB_DOWN = "Climb Down";
    private final static String MAPPING_RESTART = "Restart";
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.assetManager = this.app.getAssetManager();
        this.viewPort = this.app.getViewPort();
        trees = new ArrayList<>();  // Initialize the list to hold trees

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        // Set global gravity to pull objects downwards
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0, -0.81f, 0));

        createGUI();
        startGame();
        
        fpp = new FilterPostProcessor(assetManager);
        initializeLight();
        activateFog();
        viewPort.addProcessor(fpp);

        // Load ambient nature sound
        ambientSound = new AudioNode(app.getAssetManager(), "Sounds/Environment/Nature.ogg", true);
        ambientSound.setLooping(true); // Continuous sound
        ambientSound.setPositional(false); // Global sound
        ambientSound.setVolume(0.6f);
        ((SimpleApplication) app).getRootNode().attachChild(ambientSound);
        ambientSound.play();
        
        bellSound = new AudioNode(assetManager, "Sounds/Environment/church-bell-loop-mono.wav", false);
        bellSound.setPositional(true);
        bellSound.setLocalTranslation(10, 15, 0); // Position near Gilman Hall
        bellSound.setVolume(1.0f);
        rootNode.attachChild(bellSound);
        
    }
    /*
    private void generateRandomCubes(int count) {
        for (int i = 0; i < count; i++) {
            // Create a cube (acorn)
            Box acornBox = new Box(0.2f, 0.2f, 0.2f);
            Geometry acorn = new Geometry("Acorn", acornBox);

            // Assign a material with a highlight effect
            Material acornMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            acornMaterial.setBoolean("UseMaterialColors", true); // Enable color-based shading
            acornMaterial.setColor("Diffuse", ColorRGBA.Brown); // Diffuse color
            acornMaterial.setColor("Ambient", ColorRGBA.Brown.mult(0.5f)); // Slight ambient lighting
            acornMaterial.setColor("Specular", ColorRGBA.Yellow); // Highlight color
            acornMaterial.setFloat("Shininess", 128f); // High shininess for glossy effect
            acorn.setMaterial(acornMaterial);

            // Randomly select a tree to place the acorn near
            Spatial tree = trees.get((int) (Math.random() * trees.size()));
            Vector3f treePosition = tree.getLocalTranslation();

            // Place the acorn slightly higher above the tree
            float xOffset = (float) (Math.random() * 0.5f - 0.25f); // Small random horizontal offset
            float zOffset = (float) (Math.random() * 0.5f - 0.25f); // Small random horizontal offset
            float yOffset = (float) (Math.random() * 10f + 7f);// Higher than the tree top

            acorn.setLocalTranslation(
                treePosition.x + xOffset,
                treePosition.y + yOffset,
                treePosition.z + zOffset
            );

            // Add a glowing effect by enabling blending
            acornMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            acorn.setQueueBucket(RenderQueue.Bucket.Transparent);

            // Add the acorn to the scene and acorn list
            rootNode.attachChild(acorn);
            acorns.add(acorn);

            // Enable shadow casting and receiving
            acorn.setShadowMode(ShadowMode.CastAndReceive);
        }
    }
    */
    
    private void generateRandomAcorns(int count) {
        for (int i = 0; i < count; i++) {
            // Load the acorn model
            Spatial acorn = assetManager.loadModel("Models/Acorn/Eichel_C.j3o");

            // Scale the acorn model to fit the scene
            acorn.setLocalScale(0.02f); // Adjust the scale as needed

            // Randomly select a tree to place the acorn near
            Spatial tree = trees.get((int) (Math.random() * trees.size()));
            Vector3f treePosition = tree.getLocalTranslation();

            // Place the acorn slightly higher above the tree
            float xOffset = (float) (Math.random() * 0.5f - 0.25f); // Small random horizontal offset
            float zOffset = (float) (Math.random() * 0.5f - 0.25f); // Small random horizontal offset
            float yOffset = (float) (Math.random() * 10f + 7f);// Higher than the tree top

            acorn.setLocalTranslation(
                treePosition.x + xOffset,
                treePosition.y + yOffset,
                treePosition.z + zOffset
            );

            // Add the acorn model to the scene and acorn list
            rootNode.attachChild(acorn);
            acorns.add(acorn);

            // Optional: Add shadow casting and receiving
            acorn.setShadowMode(ShadowMode.CastAndReceive);
        }
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
        sun.setDirection(sunDir.normalizeLocal());
        rootNode.addLight(sun);
        
        
        Spatial sky = SkyFactory.createSky(assetManager, 
                "Textures/Sky/Bright/FullskiesBlueClear03.dds", false);
        sky.setQueueBucket(RenderQueue.Bucket.Sky);
        sky.setCullHint(Spatial.CullHint.Never);
        rootNode.attachChild(sky);
        
        DirectionalLightShadowFilter dlsf =
            new DirectionalLightShadowFilter(assetManager, 1024, 2);
        dlsf.setShadowIntensity(0.8f);
        dlsf.setLight(sun);
        dlsf.setEnabled(true);
        fpp.addFilter(dlsf);
        
        ssaoFilter = new SSAOFilter(12.94f,43.93f,.33f,.60f);
        fpp.addFilter(ssaoFilter);
        
        // make light beams appear from where sun is on skybox
        sunLightFilter = new LightScatteringFilter(sunDir.mult(-3000));
        fpp.addFilter(sunLightFilter);
    }


    /**
     * Initialize game level and logic (creating level content, setting up physics, etc.
     * Potential TODO: Add a WorldManagerState object that attaches nodes to the rootNode object, 
     * a PhysicsState object that handles falling and colliding objects, and a 
     * ScreenshotAppState object that saves rendered scenes as image files to the user's desktop
     */
    private void startGame() {
        // Initialize squirrel, buildings, trees, and the quadmodel.setQueueBucket(RenderQueue.Bucket.Opaque);
        rootNode.setShadowMode(ShadowMode.Off);
        createStaticQuad(rootNode);        
        initializeSquirrelAndCampus();
        addMapping();
        attachCenterMark();   
        //generateRandomCubes(8);
        generateRandomAcorns(2);
    }
    
    private void createGUI() {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
    
        // Acorn counter text
        acornCounterText = new BitmapText(font, false);
        acornCounterText.setSize(font.getCharSet().getRenderedSize()*3);
        acornCounterText.setColor(ColorRGBA.White);
        acornCounterText.setText("Acorns Collected:");
        acornCounterText.setLocalTranslation(20, cam.getHeight() - 50, 0); // Position on the screen
        guiNode.attachChild(acornCounterText);
        System.out.println("guiNode children count: " + guiNode.getQuantity());
        System.out.println("acornCounterText is attached: " + (guiNode.hasChild(acornCounterText)));

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
            
            inputManager.addMapping(MAPPING_RESTART, TRIGGER_RESTART);
        }
        
        inputManager.addListener(analogListener, 
                MAPPING_RUN_FORWARD, MAPPING_RUN_BACKWARD, MAPPING_RUN_LEFT, MAPPING_RUN_RIGHT, 
                MAPPING_CLIMB_UP, MAPPING_CLIMB_DOWN);  
        inputManager.addListener(actionListener, MAPPING_RESTART);
        
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
    
    
    private ActionListener actionListener = new ActionListener() {
        @Override 
        public void onAction(String name, boolean isPressed, float tpf) {
            if (MAPPING_RESTART.equals(name) && isPressed) {
                if (restartInProgress) {
                    System.out.println("Input ignored during restart.");
                    return; // Ignore all inputs during the restart process
                }
                if (isPressed && MAPPING_RESTART.equals(name)) {
                    if (gameCompleted) {
                        // Restart directly if the game is completed
                        System.out.println("Restarting game without confirmation.");
                        restartGame();
                    } else if (!awaitingRestartConfirmation) {
                        awaitingRestartConfirmation = true;
                        showRestartConfirmation(); // Show confirmation message
                        registerConfirmationInputs(); // Register Y and N inputs
                    }
                }
            }
        }
    };
    
    private ActionListener confirmationListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                if ("ConfirmRestart".equals(name)) {
                    System.out.println("Restart confirmed.");
                    awaitingRestartConfirmation = false;
                    unregisterConfirmationInputs(); // Remove Y and N listeners
                    restartGame(); // Restart the game
                } else if ("CancelRestart".equals(name)) {
                    System.out.println("Restart canceled.");
                    awaitingRestartConfirmation = false;
                    unregisterConfirmationInputs(); // Remove Y and N listeners
                }
            }
        }
    };

    private void registerConfirmationInputs() {
        if (!inputManager.hasMapping("ConfirmRestart")) {
            inputManager.addMapping("ConfirmRestart", new KeyTrigger(KeyInput.KEY_Y));
        }
        if (!inputManager.hasMapping("CancelRestart")) {
            inputManager.addMapping("CancelRestart", new KeyTrigger(KeyInput.KEY_N));
        }
        inputManager.addListener(confirmationListener, "ConfirmRestart", "CancelRestart");
    }

    private void unregisterConfirmationInputs() {
        if (inputManager.hasMapping("ConfirmRestart")) {
            inputManager.deleteMapping("ConfirmRestart");
        }
        if (inputManager.hasMapping("CancelRestart")) {
            inputManager.deleteMapping("CancelRestart");
        }
        inputManager.removeListener(confirmationListener);
    }

    private void showRestartConfirmation() {
        if (gameCompleted) {
            return; // Do not show confirmation if the game is completed
        }
        
        BitmapText confirmationText = new BitmapText(assetManager.loadFont("Interface/Fonts/Default.fnt"), false);
        confirmationText.setSize(30); // Adjust font size
        confirmationText.setColor(ColorRGBA.Yellow);
        confirmationText.setText("Are you sure you want to restart? Press Y to confirm or N to cancel.");
        confirmationText.setLocalTranslation(200, cam.getHeight() / 2, 0); // Adjust position
        guiNode.attachChild(confirmationText);
        
        app.enqueue(() -> {
            guiNode.attachChild(confirmationText); // Safely attach to guiNode
            return null;
        });

        // Automatically remove the text when the confirmation is complete
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                app.enqueue(() -> {
                    guiNode.detachChild(confirmationText);
                    return null;
                });
            }
        }, 5000); // Remove the message after 5 seconds
    }

    /**
     * Method to create squirrel model and campus assets (currently as boxes)
     */
    private void initializeSquirrelAndCampus() {
        // Create a parent node for both the squirrel and environment objects
        Node campusNode = new Node("CampusNode");

        // Add the squirrel
        addSquirrel(campusNode);

        // Add trees manually and add them to the trees list
        Spatial tree1 = createTree(campusNode, 5, 0.0f, 0);
        trees.add(tree1);

        Spatial tree2 = createTree(campusNode, 20, 0.0f, -9);
        trees.add(tree2);

        Spatial tree3 = createTree(campusNode, -10, 0.0f, 20);
        trees.add(tree3);

        // Add the quad in the center
        //createQuad(campusNode, 0, 0.01f, 0); // Flat box for the quad

        // Attach the campus node to the root node
        rootNode.attachChild(campusNode);
    }
    
    private void addSquirrel(Node parentNode) {
        System.out.println("Passing acornCounterText to SquirrelControl: " + (acornCounterText != null));
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
        squirrelMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);
        squirrelMaterial.getAdditionalRenderState().setDepthTest(true);
        squirrelModel.setMaterial(squirrelMaterial);
        
        TangentBinormalGenerator.generate(squirrelModel);
        
        // Position, scale, and rotation adjustments
        squirrelModel.setLocalTranslation(0, 0, 0);
        squirrelModel.setLocalScale(0.3f); // Adjust scale if needed
        RigidBodyControl squirrelPhysics = new RigidBodyControl(1.0f); // mass > 0
        squirrelModel.addControl(squirrelPhysics);
        bulletAppState.getPhysicsSpace().add(squirrelPhysics);

        // Add control for squirrel-specific movement
        SquirrelControl squirrelControl = new SquirrelControl(
        this,
        cam,                  // Camera
        trees,                // List of trees
        acorns,               // List of acorns
        rootNode,             // Root node
        acornCounterText,     // GUI element for the acorn counter
        inputManager,         // Input manager
        squirrelPhysics,       // Squirrel's physics control
        assetManager,        // Pass the AssetManager       
        findAnimComposer(squirrelModel) // Squirrel's animComposer
    );
        squirrelModel.addControl(squirrelControl);
        rotateSquirrelToFront();
        
        squirrelModel.setQueueBucket(RenderQueue.Bucket.Opaque);
        squirrelModel.setShadowMode(ShadowMode.CastAndReceive);
        
        parentNode.attachChild(squirrelModel);
        rotateSquirrelToFront();
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
    
    private Spatial createTree(Node parentNode, float x, float y, float z) {
        Spatial treeGeo = assetManager.loadModel("Models/Tree/laubbaum.j3o");
        treeGeo.scale(2);

        // Create a material and apply the texture
        Material treeMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        treeMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Tree/texture_laubbaum.png"));

        // Enable transparency for leaves and branches
        // treeMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        treeGeo.setQueueBucket(RenderQueue.Bucket.Opaque);
        treeMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Off);

        treeGeo.setMaterial(treeMaterial);

        // Position the tree
        float terrainHeight = 0; // TODO: Replace this with actual terrain height if implemented
        Vector3f treeLoc = new Vector3f(x, terrainHeight + y, z);
        treeGeo.setLocalTranslation(treeLoc);

        parentNode.attachChild(treeGeo);
        
        treeGeo.setShadowMode(ShadowMode.CastAndReceive);
        return treeGeo;
    }
    
    /*
    private void createQuad(Node parentNode, float x, float y, float z) {
        // Flat box for the quad
        Box quad = new Box(35, 0.1f, 35);
        Geometry quadGeom = new Geometry("Quad", quad);
        
        // Apply a grass or dirt texture to the ground
        Material quadMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        quadMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Ground.jpg")); // Use a ground texture
        quadGeom.setMaterial(quadMat);
        
        quadGeom.setLocalTranslation(x, y, z);
        parentNode.attachChild(quadGeom);
        quadGeom.setShadowMode(ShadowMode.Receive);
    }
    */
    private void createStaticQuad(Node parentNode) {
        float terrainSize = 300; // Width and depth of the terrain
        Box quad = new Box(terrainSize, 0.1f, terrainSize);
        Geometry quadGeom = new Geometry("Quad", quad);

        // Apply a ground texture
        Material quadMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        quadMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Ground.jpg")); // Use your ground texture
        quadMat.setFloat("Shininess", 0); // Reduce shininess
        quadGeom.setMaterial(quadMat);

        // Center the quad at the origin
        quadGeom.setLocalTranslation(0, 0, 0);
        parentNode.attachChild(quadGeom);
        
        quadGeom.setShadowMode(ShadowMode.Receive);
        
        // Apply texture scaling
        //quadMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha); // Optional transparency
        //quadGeom.getMesh().scaleTextureCoordinates(new Vector2f(50, 50)); // Adjust the scaling factor
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
    
    private void rotateSquirrelToFront() {
        if (squirrelModel == null) {
            System.out.println("Error: squirrelModel is null!");
        } else {
            System.out.println("squirrelModel exists. Current rotation: " + squirrelModel.getLocalRotation());
        }

        // Rotate the squirrel to face forward along the Z-axis
        Quaternion frontRotation = new Quaternion();
        frontRotation.fromAngleAxis( FastMath.PI , Vector3f.UNIT_Y);
        squirrelModel.setLocalRotation(frontRotation);
        squirrelModel.updateGeometricState();
    }
    
    private float bellTimer = 0;
    private final float bellInterval = 60.0f; // Ring every 60 seconds (1 minute in-game)

    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        debrisTimer += tpf;
        // Trigger debris effect after 5 seconds
        if (!debrisTriggered && debrisTimer >= 3.0f) {
            triggerDebrisEffect();
            debrisTriggered = true; // Ensure it triggers only once
            System.out.println("Debris effect triggered after 5 seconds.");
        }
        
        bellTimer += tpf;
        if (bellTimer >= bellInterval) {
            bellSound.playInstance();
            bellTimer = 0; // Reset timer
        }
        updateBellVolume(cam.getLocation());
    }
    
    private void updateBellVolume(Vector3f playerPosition) {
        float distance = bellSound.getLocalTranslation().distance(playerPosition);
        float maxDistance = 50.0f; // Maximum distance to hear the bell
        float volume = Math.max(0, 1 - (distance / maxDistance));
        bellSound.setVolume(volume);
    }

    private void activateFog(){
        // activate fog
        fogFilter = new FogFilter();
        fogFilter.setFogDistance(155);
        fogFilter.setFogDensity(0.3f);
        fogFilter.setFogColor(ColorRGBA.Gray);
        fpp.addFilter(fogFilter);
    }
    
    private void initDebris() {
        debrisEmitter = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 5);
        Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        debrisMat.setTexture("Texture", assetManager.loadTexture("Effects/debris.png"));
        debrisEmitter.setMaterial(debrisMat);
        debrisEmitter.setImagesX(3);
        debrisEmitter.setImagesY(3);
        debrisEmitter.setSelectRandomImage(true);

        debrisEmitter.setRandomAngle(true);
        debrisEmitter.setRotateSpeed(FastMath.TWO_PI);
        debrisEmitter.setStartColor(new ColorRGBA(0.8f, 0.8f, 1f, 1.0f));
        debrisEmitter.setEndColor(new ColorRGBA(.5f, 0.5f, 0.5f, 1f));
        debrisEmitter.setStartSize(.2f);
        debrisEmitter.setEndSize(.7f);
        debrisEmitter.setGravity(0, 30f, 0);
        debrisEmitter.setLowLife(1.4f);
        debrisEmitter.setHighLife(1.5f);
        debrisEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 15, 0));
        debrisEmitter.getParticleInfluencer().setVelocityVariation(.50f);
        debrisEmitter.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
    }

    /**
    * Trigger the shockwave effect around the squirrel when the game starts.
    */
    private void triggerDebrisEffect() {
        if (debrisEmitter == null) {
            initDebris();
        }
        
        if (debrisEmitter.getParent() == null) {
            Node squirrelParentNode = (Node) squirrelModel.getParent();
            squirrelParentNode.attachChild(debrisEmitter);
        }
       
        debrisEmitter.setLocalTranslation(squirrelModel.getLocalTranslation());

        // Emit all particles at once and stop continuous emission
        debrisEmitter.emitAllParticles(); // Emit all particles in one burst
        debrisEmitter.setParticlesPerSec(0); // Stop continuous emission
        
        System.out.println("Particle effect emitted.");
    }
   
    private void restartGame() {
        System.out.println("Restarting game...");
        if (restartInProgress) {
            System.out.println("Restart already in progress. Skipping duplicate restart in restartGame().");
            return; // Avoid duplicate restarts
        }
        restartInProgress = true;
        gameCompleted = false; // Reset game state
        awaitingRestartConfirmation = false; // Clear restart confirmation state
        collectedAcorns = 0; // Reset acorn count
        acorns.clear(); // Clear existing acorns
        
        inputManager.reset(); // Clear all current key states to avoid lingering input
    
        app.enqueue(() -> {
            rootNode.detachAllChildren(); // Detach all children from the root node
            guiNode.detachAllChildren(); // Detach all GUI elements

            // Recreate GUI and game objects
            createGUI();
            startGame();
            ambientSound.play(); // Restart ambient sound
            restartInProgress = false;
            return null;
        });
    }
    
    private void setGameCompleted(boolean completed) {
        this.gameCompleted = completed;
        System.out.println("GameCompleted state updated to: " + completed);
    }
    
    public boolean isGameCompleted() {
        return gameCompleted;
    }
    
    public void endGame() {
        app.enqueue(() -> {
            BitmapText acornCounterText = this.acornCounterText; // Access the GUI element safely
            if (acornCounterText != null) {
                acornCounterText.setText("CONGRATULATIONS!!!!\nPress R to Restart");
                acornCounterText.setSize(cam.getHeight() / 8);
                acornCounterText.setColor(ColorRGBA.Yellow);
                float textWidth = acornCounterText.getLineWidth();
                float textHeight = acornCounterText.getLineHeight();
                acornCounterText.setLocalTranslation(
                    (cam.getWidth() - textWidth) / 2,
                    (cam.getHeight() + textHeight) / 2,
                    0
                );
            }
            setGameCompleted(true); // Update the game completion state
            awaitingRestartConfirmation = false;
            return null;
        });
    }


    @Override
    public void cleanup() {
        super.cleanup();
        if (debrisEmitter != null && debrisEmitter.getParent() != null) {
            debrisEmitter.getParent().detachChild(debrisEmitter);
        }
        rootNode.detachAllChildren();
        guiNode.detachAllChildren();
        inputManager.removeListener(analogListener);
    }
}


