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
New home screen
<img width="1920" alt="Screenshot 2024-11-06 at 18 52 11" src="https://github.com/user-attachments/assets/345bcdd2-3498-480d-b016-8da26f1379cf">

New setting screen
<img width="1920" alt="Screenshot 2024-11-06 at 18 52 18" src="https://github.com/user-attachments/assets/906591f4-a6ed-4bfa-812b-21c552da0219">
#### Model
We used assetManager to load models and add textures, and skinning control to add animated meshes. We also included collision and other physics to further implement our game design.
##### The Squirrel Model


<img width="407" alt="Screenshot 2024-11-06 at 22 06 28" src="https://github.com/user-attachments/assets/68906fa1-460f-40b2-bb7e-be5aee4824ac">


The environment is also updated with treebark and grass, simulating a real-world environment.
Lighting is added so that the character and other elements appear shaded.

### Third Deliverable
#### New Features
Since the second deliverable, the following features have been added to enhance gameplay and visuals:

##### Sound Component

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
The game is now fully playable, featuring clear objectives and rewards:

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
Restarting mechanism is implemented. When users press R in game, the game will confirm if users want to restart. The game will pause and the confirmation message will time out after 5 seconds. After users win the game, pressing R will restart the game without confirmation.

#### Animation Enhancements
We fixed the bug where several squirrels are generated. Additionally, idle animation will be propoerly triggered if the squirrel remains stationary. Every pressing on the space (jump upward) will interrupt the current animation and retrigger a jump. When squirrel is grounded again, the current jumping animation will stop shortly and retrigger random idle animation.

#### Scene Enhancements
New environmental assets such as monuments and more trees are added to the scene. Shadow rendering bug in the last deliverable (where shadow is too dark and possibly rendered after models) is fixed. 

A tree collision mechanism at the lower part of the tree is also implemented to guarantee that the squirrel will not pass throught the trunk.

## Demo


## Member Contributions
- Serena: Implemented ambient and positional sound components with dynamic responses to player actions and environmental cues. Updated the README.
- Anna: Enhanced animation and models, implemented restart mechanism, start menu, setting menu, and six visual effects
- Leo: Completed game mechanism (collecting and counting acorn), added end game mechanism, refined squirrel control and movement
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
If possible, we would like to expand the gameplay to more open-world and explorative as we initially proposed. 

Explore the vibrant campus from a squirrel's low perspective, navigating its iconic locations, from the quads to the historic Gilman clock tower. The journey can involve scavenging snacks dropped by students, evading predators, and uncovering pieces of campus history as you complete various objectives. The game features diverse and engaging missions, including sneaking into lecture halls to retrieve stolen acorns, exploring hidden nighttime events where nocturnal predators lurk, and gathering puzzle pieces that unlock secret areas of the campus. With dynamic gameplay like sprinting, gliding between buildings, and even riding scooters, you’ll experience the life of a squirrel with new challenges and achievements at every turn. As the final chapter unfolds, the story leads to an epic graduation day on the iconic Homewood campus.

Furthermore, the setting menu is not currently in use. We can also implement more setting so that users can customize setting parameter such as mouse sensitivity, with or without fog, and etc.

