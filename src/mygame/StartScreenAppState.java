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
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
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
    
    // Button dimensions and spacing
    private float buttonWidth = 150;
    private float buttonHeight = 150;
    private float spacing = 50;

    // Starting Y position for vertical centering
    private float startY;

    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();

        this.startY = (cam.getHeight() - ((buttonHeight * 3) + (spacing * 2))) / 2; // 3 buttons with 2 spaces
        createStartMenu();
    }

    /**
     * Private helper method that initializes home menu. 
     * This includes setting up background & buttons and making cursor visible.
     */
    private void createStartMenu() {
        System.out.println("Creating start menu");

        // This background is temporary
        Picture background = new Picture("MenuBackground");
        background.setImage(assetManager, "Textures/menu_background.png", true);
        background.setPosition(0, 0); 
        background.setWidth(cam.getWidth()); // Full screen width
        background.setHeight(cam.getHeight()); // Full screen height
        rootNode.attachChild(background);

        // Calculate the center X position for buttons
        float centerX = (cam.getWidth() - buttonWidth) / 2;

        // Create Play button
        addButton("PlayButton", "Textures/play_button.png", centerX, startY);

        // Create Quit button
        addButton("QuitButton", "Textures/quit_button.png", centerX, startY + buttonHeight + spacing);

        // Create Options button
        addButton("OptionsButton", "Textures/options_button.png", centerX, startY + 2 * (buttonHeight + spacing));

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
    * @param buttonName  The name of the button (for internal identification).
    * @param texturePath The file path to the button image.
    * @param xPos        The X position for the button.
    * @param yPos        The Y position for the button.
    */
   private void addButton(String buttonName, String texturePath, float xPos, float yPos) {
       Picture button = new Picture(buttonName);
       button.setImage(assetManager, texturePath, true);
       button.setWidth(buttonWidth);
       button.setHeight(buttonHeight);
       button.setPosition(xPos, yPos);
       rootNode.attachChild(button);
   }


    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                // Get mouse click position
                float clickX = inputManager.getCursorPosition().x;
                float clickY = inputManager.getCursorPosition().y;

                // Check if the Play button was clicked
                float playX = (cam.getWidth() - buttonWidth) / 2;
                float playY = startY;
                if (isClickOnButton(clickX, clickY, playX, playY)) {
                    System.out.println("Navigating to running game.");
                    GameRunningAppState gameRunning = new GameRunningAppState();
                    app.getStateManager().detach(StartScreenAppState.this);
                    app.getStateManager().attach(gameRunning);
                }

                // Check if the Quit button was clicked
                float quitY = startY + buttonHeight + spacing;
                if (isClickOnButton(clickX, clickY, playX, quitY)) {
                    System.out.println("Exiting game.");
                    app.stop();
                    return;
                }

                // Check if the Options button was clicked
                float optionsY = startY + 2 * (buttonHeight + spacing);
                if (isClickOnButton(clickX, clickY, playX, optionsY)) {
                    System.out.println("Navigating to options menu.");
                    OptionsScreenAppState optionsScreen = new OptionsScreenAppState();
                    app.getStateManager().detach(StartScreenAppState.this);
                    app.getStateManager().attach(optionsScreen);
                }
            }
        }
    };
    
    /**
    * Determines if a mouse click is within the bounds of a button.
    *
    * @param clickX The X position of the mouse click.
    * @param clickY The Y position of the mouse click.
    * @param buttonX The X position of the button.
    * @param buttonY The Y position of the button.
    * @param buttonWidth The width of the button.
    * @param buttonHeight The height of the button.
    * @return True if the mouse click is within the button bounds, otherwise false.
    */
   private boolean isClickOnButton(float clickX, float clickY, float buttonX, float buttonY) {
       return clickX >= buttonX && clickX <= buttonX + this.buttonWidth &&
              clickY >= buttonY && clickY <= buttonY + this.buttonHeight;
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
