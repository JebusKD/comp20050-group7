# UCD COMP20050 Project (Quax)
This repository was created and developed by Sophie Lyons (CrypticDemon), Senan O'Connor (Sock_06) and Jamie Kildea (JebusKD) for the COMP20050 (Software Engineering Project 2) module as part of Stage 2 (spring semester 2026) of the University College Dublin course DN201 (Computer Science).

This project implements the game of Quax with a computer opponent in Java, using JavaFX as the graphical library. The original author of Quax is Bill Taylor who was a maths lecturer at the University of Canterbury in Christchurch, NZ.

## Features
* Turn-based gameplay
* Win conditions
* Enforcement of valid piece placement
* Pie Rule
* Computer Opponent
* Player or computer will play first randomly
* Overlay displaying opponent's strategy

## Launch Instructions

Install the latest version of the Java Runtime Environment.<br>
https://www.java.com/download/<br>
*On Windows, java should be added as a command to PATH during installation. On Linux, you may need to locate the java binary and call the launch command using the location of the binary.*

Download and extract the JavaFX SDK for your system (version 26.0.2 or later).<br>
https://gluonhq.com/products/javafx/

In the extracted SDK folder, navigate to the `lib` folder and note the folder directory, replacing `<JAVAFX-libdir>` with the directory in the following commands.

Download the `QuaxGame.jar` file from releases, or compile the source code yourself into a JAR.<br>
https://github.com/JebusKD/comp20050-group7/releases/tag/v1.0.0

**Windows:**<br>
Open Command Prompt, navigate to the directory where `QuaxGame.jar` is located and run the following command, replacing `<JAVAFX-libdir>` with the directory of the JavaFX library.<br>
`java --module-path "<JAVAFX-libdir>" --add-modules javafx.controls --enable-native-access=javafx.graphics -jar QuaxGame.jar`<br>
*Alternatively, you can open File Explorer, navigate to the folder the file is located, and type* `cmd` *into the address bar to open Command Prompt in the correct location.*

**Linux:**<br>
In your shell of choice, navigate to the directory where `QuaxGame.jar` is located and run the following command, replacing `<JAVAFX-libdir>` with the directory of the JavaFX library, and if needed, replace `java` with the location where the java binaries are installed.<br>
`java --module-path "<JAVAFX-libdir>" --add-modules javafx.controls --enable-native-access=javafx.graphics -jar QuaxGame.jar`

To restart the game, it is required to close the application and launch it again.

## How to Play

<img width="564" height="384" alt="Screenshot of the empty Quax board. Black to play." src="https://github.com/user-attachments/assets/83a47feb-7898-4ef8-8186-3931f0e3f0a8" />

This is the board that Quax is played on - an 11x11 grid of octagonal tiles with rhombic tiles filling the spaces. Black plays first. The goal of Black is to connect the top and bottom of the board (indicated with the black borders) together using their pieces, and the goal of White is to connect the left and right sides of the board together (indicated with the white borders) together. Text on the side indicates which player is next to move.

<img width="575" height="401" alt="Screenshot of the Quax board after Black placed an octagonal tile. White to play." src="https://github.com/user-attachments/assets/4a82ed82-0ccd-4e5c-bd1d-127782765453" />

On your turn, you can place an octagonal piece in any unoccupied space on the board, or a rhombic piece in any empty rhombic space which connects diagonally to two octagons of your colour.

<img width="549" height="385" alt="Screenshot of the Quax board after White placed an octagonal tile. Black to play." src="https://github.com/user-attachments/assets/7df1cbed-3474-408d-980e-d3184cc15789" />

Turns alternate between Black and White until the game ends. A finished game may look something like this, where Black has won by connecting the top and bottom of their board together.<br>
<img width="751" height="512" alt="Screenshot of the Quax board in a final state, Black has won by connecting the top and bottom of the board together. White loses as they didn't complete their connection." src="https://github.com/user-attachments/assets/de555ab2-7e4b-4b4c-b53d-bbf369b5483d" />

### Winning Chains

<img width="354" height="249" alt="Screenshot of a Quax board demonstrating a connected chain of black pieces across the top and bottom of the board." src="https://github.com/user-attachments/assets/972a6e70-d3c9-4f77-bfe3-46e6812785e3" />

Black wins by connecting the top and bottom of the board together - pieces of your colour are connected if they are adjacent to each other (octagon pieces cardinally adjacent to other octagonal pieces, or rhombic pieces diagonally adjacent to octagonal pieces).

<img width="358" height="250" alt="Screenshot of a Quax board demonstrating a connected chain of white pieces across the left and right sides of the board." src="https://github.com/user-attachments/assets/bb1e86e2-fc70-42b4-8681-5ed4158e187a" />

White wins by connecting the left and right sides of the board together.

### Rhombic Tiles

Rhombic tiles cannot be placed anywhere on the board unless there are two octagonal pieces of your colour that are diagonally aligned and adjacent to the rhombic tile.

Valid placement of a rhombic tile (connecting two of Black's octagons together)<br>
<img width="255" height="252" alt="A snippet of the Quax board showing two of Black's octagons being connected together with a rhombic tile." src="https://github.com/user-attachments/assets/41377e2a-9493-4371-8126-fc2b1bcc1c4d" />

Invalid placement of a rhombic tile (only adjacent to one of White's octagons with no diagonal connection)<br>
<img width="268" height="252" alt="A snippet of the Quax board showing one of White's rhombuses in an illegal position, not diagonally connected to a pair of its octagonal pieces." src="https://github.com/user-attachments/assets/e2020876-c6dc-4b33-a04c-aa6cf4b72b76" />

### Pie Rule

On White's first turn of the game, they may choose to use the Pie Rule by clicking the "Pie Rule" button that appears on the sidebar on their turn. If they do so, they swap seats with Black, and now control the black pieces. The player who was Black previously now plays the white pieces, and continues play immediately from the position. This balances the game by giving the player going second a chance to swap seats if the player who went first plays a powerful opening move.

### Bot Strategy

Not relevant to actual gameplay, but as part of the project's development, an overlay showcasing the bot's strategy when deciding its move can be shown by clicking the "Show Strategy" button in the sidebar. It can be hidden by pressing "Hide Strategy" afterwards. The move the bot just made is highlighted with a green tile, and the strategy values of each tile are shown through a coloured border around each tile.
