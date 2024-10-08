package mygame;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;

/**
 * AppState for the options screen.
 * @author: Anna Dai
 */
public class OptionsScreenAppState extends AbstractAppState {
    private SimpleApplication app;
    private Node rootNode; //final 
    private InputManager inputManager;
    private AssetManager assetManager;
    private Camera cam;
    
    // Button dimensions and spacing
    private float buttonWidth = 150;
    private float buttonHeight = 100;
    private float spacing = 50;

    // Starting Y position for vertical centering
    private float startY;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.inputManager = this.app.getInputManager();
        this.cam = this.app.getCamera();
        this.assetManager  = this.app.getAssetManager();
        this.startY = (cam.getHeight() - ((buttonHeight * 3) + (spacing * 2))) / 2; // 3 buttons with 2 spaces
        createOptionsMenu();
    }

    // TODO: add more options for customizing keyboard layout, graphic quality, or difficulty. (buttons, sliders, etc.)
    private void createOptionsMenu() {
        Picture background = new Picture("MenuBackground");
        background.setImage(assetManager, "Textures/menu_background.png", true);
        background.setPosition(0, 0);
        background.setWidth(cam.getWidth()); // Full screen width
        background.setHeight(cam.getHeight()); // Full screen height
        rootNode.attachChild(background);

        // Calculate position to center the save button
        float centerX = (cam.getWidth() - buttonWidth) / 2; // Center X position
        float centerY = (cam.getHeight() - buttonHeight) / 2; // Center Y position

        addButton("SaveButton", "Textures/save_button.png", centerX, centerY);

        // Add mouse input listener for buttons
        if (!inputManager.hasMapping("Save")) {
            inputManager.addMapping("Save", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        }
        inputManager.addListener(actionListener, "Save");
        
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

                // Use the same Y position as when you created the button
                float saveX = (cam.getWidth() - buttonWidth) / 2;
                float saveY = (cam.getHeight() - buttonHeight) / 2; // Use centerY as in createOptionsMenu

                // Check if the Save button was clicked
                if (isClickOnButton(clickX, clickY, saveX, saveY)) {
                    System.out.println("Returning to home screen.");
                    StartScreenAppState startScreen = new StartScreenAppState();
                    app.getStateManager().detach(OptionsScreenAppState.this);
                    app.getStateManager().attach(startScreen);
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
    public void cleanup() {
        super.cleanup();
        rootNode.detachAllChildren();
        inputManager.removeListener(actionListener);
    }
}
