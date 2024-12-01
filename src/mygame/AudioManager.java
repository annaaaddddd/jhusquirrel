

package mygame;

import com.jme3.audio.AudioNode;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 * Audio Control
 * @author Serena Hu
 */
public class AudioManager {
    private AudioNode ambientNature;
    private AudioNode squirrelChirp;
    private AudioNode footsteps;

    public AudioManager(AssetManager assetManager, Node rootNode) {
        // Load Ambient Nature Sound
        ambientNature = new AudioNode(assetManager, "Sounds/Environment/Nature.ogg", true);
        ambientNature.setLooping(true);
        ambientNature.setPositional(false); // Global sound
        ambientNature.setVolume(0.5f);
        rootNode.attachChild(ambientNature);

        // Load Squirrel Chirp Sound
        /*
        squirrelChirp = new AudioNode(assetManager, "Sounds/Effects/Chirp.ogg", false);
        squirrelChirp.setPositional(true); // Positional sound
        squirrelChirp.setVolume(0.8f);
        rootNode.attachChild(squirrelChirp);

        // Load Footstep Sound
        /*
        footsteps = new AudioNode(assetManager, "Sounds/Effects/Footsteps.ogg", false);
        footsteps.setPositional(true);
        footsteps.setVolume(0.7f);
        rootNode.attachChild(footsteps);
        */
    }

    // Play Ambient Nature
    public void playAmbient() {
        ambientNature.play();
    }

    public void stopAmbient() {
        ambientNature.stop();
    }

    // Trigger Squirrel Chirp
    public void playChirp() {
        squirrelChirp.playInstance(); // Play once
    }

    // Trigger Footsteps
    public void playFootsteps() {
        footsteps.playInstance();
    }
}
