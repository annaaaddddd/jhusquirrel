## Game Summary
In this game, you embody a **squirrel on the Johns Hopkins University Homewood campus**—but with a twist: the squirrel is actually the resurrected form of an alumnus who graduated over a decade ago. Your mission is straightforward yet demanding: navigate a dense forest of towering trees to collect every acorn scattered among the high branches. Using quick bursts of velocity in any direction, you must master the squirrel’s unique movement while battling the constant pull of gravity.
The forest presents a mix of opportunity and challenge. Mistimed bursts can send you crashing into branches or plummeting to the forest floor, forcing you to restart your ascent. Success requires sharp reflexes, impeccable timing, and strategic planning as you soar, dip, and dart your way through the canopy. With every acorn collected, you inch closer to victory, proving that even a resurrected squirrel can conquer the treetops!

## Genre
Simulation, Open World, Action-Adventure

## Inspiration
### Flappy Bird
Flappy Bird inspires the game by introducing its signature movement mechanic: each key press provides a short, controlled burst of velocity, allowing players to navigate obstacles with precision. This creates a challenging yet rewarding gameplay loop where timing and coordination are key, blending simplicity with skill-based movement in a dynamic environment.

![15561733929231_ pic](https://github.com/user-attachments/assets/3da31e3c-5578-454e-b748-a948e176520c)


### [Untitled Goose Game](https://store.steampowered.com/app/837470/Untitled_Goose_Game/)
In this game, players control a goose who bothers the inhabitants of an English village. Players must use the goose's abilities to manipulate objects and interact with non-player characters to complete objectives. 


https://github.com/user-attachments/assets/a67e124b-2b30-40ac-b5a9-e67b2ac17491


### [Sneaky Sasquatch](https://apps.apple.com/us/app/sneaky-sasquatch/id1098342019)
The player plays as a Sasquatch, who lives near a campground, and is friends with local wildlife NPCs. Players initially revolves around stealing food from campers and hiding from park rangers. After obtaining human clothes, Sasquatch mingles among humans and participates in human activities, competes in sports, and even can get a bank account and get jobs like a doctor, police officer or an office executive. 
Sasquatch also need food and sleep, and has a food meter that can deplete through various means. There are currently four chapters of main storyline, but players can explore the world without being obligated to complete all missions.
![MV5BMmZiNzA4MTYtMTdlOC00NGZjLWFjMzUtZDM4NmVjNDA4YzZmXkEyXkFqcGc@ _V1_](https://github.com/user-attachments/assets/751577cf-7e9d-48f9-b0fc-f78f79c13ab9)

### [Persona 5 Royal](https://persona.atlus.com/p5r/?lang=en)
Taking place in modern-day Tokyo, the story follows a high school student known by the codename Joker who transfers to a new school after he is falsely accused of assault and put on probation. Over the course of a school year, he and other students awaken to a special power, becoming a group of secret vigilantes known as the Phantom Thieves of Hearts.
The game contains a day-night cycle and weather systems. The year is punctuated by events as Joker attends school, and outside of school he can have part-time jobs, pursue leisure activities, or create battle items.


https://github.com/user-attachments/assets/92af0005-95d3-4f6d-bf8d-7c3ee8c4f2fc



The **visual art** style in this game is **realism/semi-realism** (somewhat realistic environment modeling, uses animation as transition between scenes, cartoon style of conversations boxes), which is the style our game aims for.

[Persona 5 Royal - PC Gameplay 4K60FPS](https://www.youtube.com/watch?v=mz0g9hxq7L8&t=296s)


### [Dave the Diver](https://store.steampowered.com/app/1868140/DAVE_THE_DIVER/)
The game sets up at the Blue Hole, a spot said to contain fish from all over the world, Dave (controlled by players) fishes for ingredients during the day and helps Bancho with the restaurant at night. During his exploration, Dave comes across a race of sea people which leads him to the mystery behind the deepsea earthquakes.

![dave2](https://github.com/user-attachments/assets/1b852b1f-2d99-4562-925f-6a0884ee528c)
![dave1](https://github.com/user-attachments/assets/8924548a-ce32-4484-bcf0-c003f3b97d99)

**Music** in this game is mostly instrumental and changes when switched to different parts of the map, thereby giving various atmosphere. Our game also wants to incorporate this ideally.
[Dave the Diver Original Soundtrack (Full)](https://www.youtube.com/watch?v=JBKhYkRc9u8&t=72s)

## Gameplay
The game is played from a *third-person perspective*, with the player controlling the squirrel, who is always positioned at the center of the screen. The player navigates a dense forest of towering trees, using precise controls inspired by "Flappy Bird." Each key press—W, A, S, D, Space, or Shift—gives the squirrel a short burst of velocity in the corresponding direction, while gravity constantly pulls them downward, adding a layer of challenge to every move.

The objective is to collect 3 acorns scattered across the treetops. Players must master the physics-based movement to maintain control, timing bursts carefully to avoid crashing into branches or falling to the ground. 

## Development
### First Deliverable
#### Scene Setup and Grouped Geometry
We used a hierarchical scene graph structure to organize the environment elements, including:
- Squirrel: Represented by a brown box that can move and climb.
- Trees: Green boxes representing trees, placed at different locations on the campus.
- Buildings: Larger white boxes placed around a central quad, representing campus buildings.
- Quad: A flat, wide light gray box that serves as the central area around which the trees and buildings are arranged.

All these elements are attached to a parent node called campusNode, allowing for efficient transformations and grouped scene management


![IMG_E73B587611CB-1](https://github.com/user-attachments/assets/7bd0426f-ce84-4f1c-a427-9491c45c29c6)



#### User Interaction
- Movement: The squirrel is controlled using the following keys:
  - WASD keys to move forward, backward, left, and right.
  - Spacebar to climb up.
  - Left Shift to climb down.

#### Control and Input Handling:
We used the InputManager to handle user input via key mappings for movement and climbing.
The SquirrelControl class manages the movement and climbing behavior, encapsulating the squirrel’s controls and integrating distance checks for climbing.

#### Object-Oriented Structure:
We extended AbstractControl to create the SquirrelControl class, which manages the squirrel's movement and interaction logic.
The game state management is handled using an AbstractAppState, as seen in the GameRunningAppState.java file, allowing for organized code for starting game.

The GameRunningAppState includes keyboard mapping and initialization code. It manages input actions, including running and climbing, through the use of AnalogListener. This enables fluid and responsive controls, enhancing player interaction with the game environment.

Home screen:
<img width="1512" alt="Screenshot 2024-11-06 at 23 26 40" src="https://github.com/user-attachments/assets/7ad17b75-0afc-4c65-ae24-4e983fd4f15e">

### Second Deliverable
#### Updated home screen design
We utilized the guiNode to enhance the graphical user interface (GUI) by adding essential elements such as text, icons, and an acorn collection indicator for better gameplay tracking.


New home screen
<img width="1920" alt="Screenshot 2024-11-06 at 18 52 11" src="https://github.com/user-attachments/assets/345bcdd2-3498-480d-b016-8da26f1379cf">

New setting screen
<img width="1920" alt="Screenshot 2024-11-06 at 18 52 18" src="https://github.com/user-attachments/assets/906591f4-a6ed-4bfa-812b-21c552da0219">
#### Model
We leveraged the assetManager to efficiently load 3D models into the game. Textures were added using the loadTexture method, TextureKey class, and the Material class. Animated meshes were incorporated using SkinningControl, while UV mapping and model details were fine-tuned in Blender to improve the visual quality and overall experience.
##### The Squirrel Model
<img width="407" alt="Screenshot 2024-11-06 at 22 06 28" src="https://github.com/user-attachments/assets/68906fa1-460f-40b2-bb7e-be5aee4824ac">

![image](https://github.com/user-attachments/assets/c0647f0c-9f3e-4e82-a1c8-8f155a0453e8)

##### The Tree Model
<img width="1002" alt="Screenshot 2024-12-11 at 1 24 57 PM" src="https://github.com/user-attachments/assets/1b312bdb-6a44-47f4-b1f9-e2cc499b2a55" />

#### Environment: Textures & Lighting
The environment has been enhanced with realistic textures for tree and grass, creating an immersive setting for players. 
Lighting was implemented using AmbientLight and DirectionalLight to add depth and shading, improving the appearance of characters and environmental elements.

####  Physics: Gravity & Collision
A tree collision system has been introduced, allowing the squirrel to interact with trees naturally. When close to a tree, pressing "T" transitions the squirrel from running to climbing. Although functional, the climbing transition requires further refinement for consistent performance and smoother gameplay.

Additionally, we experimented with new gameplay mechanics, including a flying squirrel mode and a climbing squirrel mode. Both modes allow players to collect acorns, enabling diverse playing styles by integrating unique physics interactions into the game. We intended to add a transition method that allows the player to switch between the flying and climbing mode.

### Third Deliverable
#### Sound Component

- Ambient sound: Background nature audio such as wind blowing sets the immersive atmosphere.

- Positional sound: Dynamic sound effects like squirrel chirping when idle and acorn collecting sound adjust based on the player's position. A church bell loop sound is also added in the background playing every 60 seconds into the game, mimicking the Gilman Hall bell.

- Additional sound effects: Added menu music in the background and button-clicking sound to increase playability.


#### Six Distinct Effects

- Particles: 
A debris effect appears when users first enter the game. This may be subject to change once we find a more suitable effect.
<img width="1512" alt="Screenshot 2024-12-03 at 9 43 45 PM" src="https://github.com/user-attachments/assets/6e39ce19-57c3-4d5f-ab5d-655196809d9d">

- Shadows: 
Real-time dynamic shadows cast by the squirrel and environmental elements, improve realism.
<img width="1512" alt="Screenshot 2024-12-03 at 9 46 00 PM" src="https://github.com/user-attachments/assets/4ac17d26-5775-4036-993b-2af05e5a5679">

- Ambient Occlusion: 
Enhanced depth and lighting contrast, particularly around tree trunks.

- Fog: 
Subtle fog effects for added environmental depth and slightly increased difficulty.
<img width="1512" alt="Screenshot 2024-12-03 at 9 47 19 PM" src="https://github.com/user-attachments/assets/ee9e4303-444c-433f-b6a7-1f67433498ec">

- Skybox: 
Added skybox to resemble outdoor environment.
<img width="1512" alt="Screenshot 2024-12-03 at 9 39 52 PM" src="https://github.com/user-attachments/assets/8ed440ad-39b2-43b1-861e-c0a9a5de8061">

- Volumetric lighting: 
Added light beams that pass through the fog, creating a more realistic environment.
![1061733273100_ pic](https://github.com/user-attachments/assets/be812801-56ca-4f98-9b45-4e47c14e7c57)

#### Playability Enhancements
The game is now playable, featuring clear objectives and winning state:

Players can collect scattered acorns to complete the mission.
Dynamic feedback provides mission status updates and rewards.
<img width="1512" alt="Screenshot 2024-12-03 at 9 47 00 PM" src="https://github.com/user-attachments/assets/1d33fd65-090d-4016-8a04-2e11a544f2f9">



https://github.com/user-attachments/assets/79d02bb0-93dc-418c-81a6-e1eef0892c95

#### Animation and Model Enhancements
We replaced the tree polygons with a new tree model, increasing the game's visual appeal. 

We also modified the squirrel's animation logic as below:

Case 1: Squirrel is stationary -> a random idle animation (e.g., Idle.000) plays.

Case 2: Squirrel jumps and lands -> Jump.Begin, Jump.Fly, and Jump.End play in sequence. Idle animations are not triggered until the squirrel is stationary for the required interval.

Case 3: Squirrel moves after being idle -> Idle animation stops immediately, and movement or jumping animations take over.

However, there are still problems such as 1) the squirrel jumps several times after it stops before it plays idle animation; 2) when the idle animations play there seem to be 2 squirrels.

### Fourth Deliverable
#### Modified Gameplay
The changes include:

Preventing Below-Plane Falling: Implement a restriction to stop the squirrel from falling below the ground plane, ensuring gameplay continuity.

Adding Flappy Bird Movement: Introduce a movement mechanism where pressing directional keys gives the squirrel a velocity burst in the respective direction, mimicking "flappy bird" mechanics. Note that this differs from the first proposed gameplay we suggested.

Stabilizing Gravity: Adjust gravity to apply a more stable and consistent downward force for smoother, predictable movement.

#### New User Interaction
A restarting mechanism has been implemented to enhance user control. When players press R during gameplay, a confirmation prompt appears, asking if they want to restart. The game pauses during this prompt, which times out after 5 seconds if no response is provided. However, after winning the game, pressing R will restart the game immediately without requiring confirmation, streamlining the experience.

Additionally, a timeout mechanism has been introduced to increase gameplay challenge. If players fail to collect three acorns before the timer runs out, the game ends, and a prompt appears, giving users the option to restart. This feature adds a strategic element to the game, encouraging players to manage their time effectively.

#### Animation Enhancements
We fixed the bug where several squirrels are generated. Additionally, idle animation will be properly triggered if the squirrel remains stationary. Every pressing on the space (jump upward) will interrupt the current animation and retrigger a jump. When squirrel is grounded again, the current jumping animation will stop shortly and retrigger random idle animation.

#### Scene Enhancements
The game environment has been enriched with new assets, including monuments and additional trees, creating a more immersive and visually dynamic scene. A shadow rendering issue from the previous deliverable, where shadows appeared overly dark and were possibly rendered after the models, has been resolved, improving overall visual quality.
<img width="1512" alt="Screenshot 2024-12-11 at 1 24 06 PM" src="https://github.com/user-attachments/assets/a8c923d3-bbb5-4015-8cb7-770723fbcfc5" />

A tree collision mechanism has also been implemented, specifically for the lower part of the tree, ensuring that the squirrel cannot pass through the trunk. This enhancement adds realism and consistency to the game physics. However, collision mechanics were intentionally omitted for the upper parts of the trees, as adding them would make gameplay overly challenging. This balance maintains realism while keeping the game enjoyable and accessible.

## Demo


## Member Contributions
- Serena: Implemented initial scene graph, textures,  ambient and positional sound components with dynamic responses to player actions and environmental cues. Updated the README.
- Anna: Enhanced animation and models, implemented squirrel control (partially), lighting, restart and timeout mechanism, start menu, setting menu, and post-processing effects
- Leo: Implemented gameplay (collecting and counting acorn) with physics, added win game mechanism, refined squirrel control and movement
- Collaborative Work:
Team discussions finalized the selection of six distinct effects.
Joint playtesting ensured cohesive gameplay and smooth feature integration.

## Acknowledgments
- Assets and References:
  - Models, sounds, and particle templates were sourced from [Pixabay](https://pixabay.com/music/) [Mixkit](https://mixkit.co/free-sound-effects/) [Textures](https://www.textures.com/).
  - Learning materials from the jMonkeyEngine Beginner’s Guide and other tutorials were used for development.
  - Online sources or tools used to implement visual and sound effects, including Blender, Audacity, etc.
 

## Download

## Future Work
We envision expanding the gameplay to embrace a more open-world and explorative design, aligning with our initial proposal. Players will explore the vibrant Johns Hopkins Homewood campus from a squirrel's unique low perspective, navigating iconic locations such as the quads and the historic Gilman clock tower. The journey will involve scavenging snacks dropped by students, evading predators, and uncovering hidden pieces of campus history while completing various objectives.

The game will feature a diverse range of engaging missions, such as sneaking into lecture halls to retrieve stolen acorns, exploring hidden nighttime events filled with lurking nocturnal predators, and gathering puzzle pieces to unlock secret areas of the campus. Dynamic gameplay mechanics like sprinting, gliding between buildings, and even riding scooters will provide players with fresh challenges and achievements at every turn. The narrative culminates in an epic graduation day, celebrating the player's journey as the squirrel on the iconic Homewood campus.

In addition, we aim to enhance the game’s usability and customization options. The currently unused settings menu will be developed to allow users to adjust parameters such as mouse sensitivity, enable or disable visual features like fog, and configure other gameplay settings. We also plan to create a cleaner and more intuitive quitting mechanism to improve user experience.

Finally, the incorporation of terrain into the game environment will add depth and realism to the campus landscape, enriching the player’s exploration. These enhancements will bring the game closer to its full potential, offering players an immersive and dynamic experience.
