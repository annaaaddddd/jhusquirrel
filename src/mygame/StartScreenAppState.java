/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;

/**
 * AppState for Home Screen.
 * @author annadai
 */
public class StartScreenAppState extends AbstractAppState {
    private SimpleApplication app;
    private Node rootNode; //final 
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera cam;
    
    private float spacing = 50;

    // Starting Y position for vertical centering
    private float startY;
    
    private String playButtonPath = "Textures/Home Menu/Buttons/homePlay.png";
    private String settingButtonPath  = "Textures/Home Menu/Buttons/homeSetting.png";
    private String exitButtonPath = "Textures/Home Menu/Buttons/homeExit.png";
    
    private AudioNode menuMusic;
    private AudioNode buttonClickSound;

    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();

        this.startY = cam.getHeight() * 0.3f; // Y coordinate to place buttons
        createStartMenu();
        
        // Load background music for the menu
        menuMusic = new AudioNode(app.getAssetManager(), "Sounds/Environment/MenuMusic.wav", true);
        menuMusic.setLooping(true);
        menuMusic.setPositional(false);
        menuMusic.setVolume(0.5f);
        ((SimpleApplication) app).getRootNode().attachChild(menuMusic);
        menuMusic.play();
        
        // Load button click sound
        buttonClickSound = new AudioNode(assetManager, "Sounds/Effects/ButtonClick.wav", false);
        buttonClickSound.setPositional(false); // Global sound
        buttonClickSound.setVolume(0.8f);
        ((SimpleApplication) app).getRootNode().attachChild(buttonClickSound);
    }

    /**
     * Private helper method that initializes home menu. 
     * This includes setting up background & buttons and making cursor visible.
     */
    private void createStartMenu() {
        System.out.println("Creating start menu");

        // This background is temporary
        Picture background = new Picture("MenuBackground");
        background.setImage(assetManager, "Textures/Home Menu/menu_background2.png", true);
        background.setPosition(0, 0); 
        background.setWidth(cam.getWidth()); // Full screen width
        background.setHeight(cam.getHeight()); // Full screen height
        rootNode.attachChild(background);

        // Calculate the center X position for buttons
        float centerX = (cam.getWidth()) / 2;
        
        // Create and position buttons dynamically based on their texture dimensions
        float playButtonY = startY;
        addButton("PlayButton", playButtonPath, centerX, playButtonY);

        float optionsButtonY = playButtonY - getButtonHeight(playButtonPath) - spacing;
        addButton("OptionsButton", settingButtonPath, centerX, optionsButtonY);

        float quitButtonY = optionsButtonY - getButtonHeight(settingButtonPath) - spacing;
        addButton("QuitButton", exitButtonPath, centerX, quitButtonY);

        // Add mouse input listener for buttons
        if (!inputManager.hasMapping("Play")) {
            inputManager.addMapping("Play", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        }
        if (!inputManager.hasMapping("Quit")) {
            inputManager.addMapping("Quit", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        }
        if (!inputManager.hasMapping("Options")) {
            inputManager.addMapping("Options", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        }
        inputManager.addListener(actionListener, "Play", "Quit", "Options");

        // Make cursor visible so users can click buttons they want to
        inputManager.setCursorVisible(true);
    }

    
    /**
     * Creates and adds a button to the screen.
     *
     * @param buttonName  The name of the button.
     * @param texturePath The file path to the button image.
     * @param centerX     The X position for centering the button.
     * @param yPos        The Y position for the button.
     */
    private void addButton(String buttonName, String texturePath, float centerX, float yPos) {
        Texture buttonTexture = assetManager.loadTexture(texturePath);
        int textureWidth = buttonTexture.getImage().getWidth();
        int textureHeight = buttonTexture.getImage().getHeight();

        Picture button = new Picture(buttonName);
        button.setImage(assetManager, texturePath, true);
        button.setWidth(textureWidth);
        button.setHeight(textureHeight);
        button.setPosition(centerX - (textureWidth / 2), yPos); // Center the button on X axis
        rootNode.attachChild(button);
    }


       private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                buttonClickSound.playInstance(); // Play click sound
                
                // Get mouse click position
                float clickX = inputManager.getCursorPosition().x;
                float clickY = inputManager.getCursorPosition().y;

                // Center X position for buttons
                float buttonX = cam.getWidth() / 2;

                // Check if the Play button was clicked
                float playY = startY;
                if (isClickOnButton(clickX, clickY, buttonX, playY, playButtonPath)) {
                    System.out.println("Navigating to running game.");
                    GameRunningAppState gameRunning = new GameRunningAppState();
                    app.getStateManager().detach(StartScreenAppState.this);
                    // Stop menu music before transitioning
                    menuMusic.stop();
                    app.getRootNode().detachChild(menuMusic);
                    app.getStateManager().attach(gameRunning);
                }

                // Check if the Options button was clicked
                float optionsY = playY - getButtonHeight(playButtonPath) - spacing;
                if (isClickOnButton(clickX, clickY, buttonX, optionsY, settingButtonPath)) {
                    System.out.println("Navigating to options menu.");
                    OptionsScreenAppState optionsScreen = new OptionsScreenAppState();
                    app.getStateManager().detach(StartScreenAppState.this);
                    app.getStateManager().attach(optionsScreen);
                }

                // Check if the Quit button was clicked
                float quitY = optionsY - getButtonHeight(settingButtonPath) - spacing;
                if (isClickOnButton(clickX, clickY, buttonX, quitY, exitButtonPath)) {
                    System.out.println("Exiting game.");
                    app.stop();
                }
            }
        }
    };
       
    /**
     * Gets the height of a button based on its texture.
     *
     * @param texturePath The file path to the button image.
     * @return The height of the button texture.
     */
    private float getButtonHeight(String texturePath) {
        Texture buttonTexture = assetManager.loadTexture(texturePath);
        return buttonTexture.getImage().getHeight();
    }

     /**
     * Determines if a mouse click is within the bounds of a button.
     *
     * @param clickX      The X position of the mouse click.
     * @param clickY      The Y position of the mouse click.
     * @param centerX     The centered X position of the button.
     * @param buttonY     The Y position of the button.
     * @param texturePath The file path to the button image.
     * @return True if the mouse click is within the button bounds, otherwise false.
     */
    private boolean isClickOnButton(float clickX, float clickY, float centerX, float buttonY, String texturePath) {
        Texture buttonTexture = assetManager.loadTexture(texturePath);
        float buttonWidth = buttonTexture.getImage().getWidth();
        float buttonHeight = buttonTexture.getImage().getHeight();
        
        float buttonX = centerX - (buttonWidth / 2);
        return clickX >= buttonX && clickX <= buttonX + buttonWidth &&
               clickY >= buttonY && clickY <= buttonY + buttonHeight;
    }


       
    @Override
    public void update(float tpf) {}
    
    @Override
    public void cleanup() {
        super.cleanup();
        rootNode.detachAllChildren();
        inputManager.removeListener(actionListener);
    }
}
