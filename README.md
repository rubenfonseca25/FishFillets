# Fish & Fillets – Java OOP Project

## Overview

This project is a simplified implementation of the classic puzzle game **Fish Fillets NG**, developed as part of an **Object-Oriented Programming (OOP)** course project.

The game consists of controlling two fish (a **small fish** and a **big fish**) inside an underwater environment filled with different objects. The objective is to interact with those objects and solve each level so that **both fish reach the exit**.

The project focuses on applying core **Object-Oriented Programming concepts in Java**, including inheritance, interfaces, abstraction, data structures, and file handling.

---

## Game Concept

The player controls two characters:

- Small Fish
- Big Fish

The fish must cooperate to move objects and navigate the level until both reach the exit.

Some objects can be moved, others block movement, and some may cause the death of a fish.

If one fish dies, the level restarts.

Levels are loaded from configuration files and the player progresses through them sequentially.

---

## Main Features

- Grid-based 2D game environment
- Two controllable characters
- Object interaction system
- Gravity applied to movable objects
- Multiple levels loaded from configuration files
- Highscore system stored persistently
- Graphical interface provided by the course framework

---

## Controls

| Key | Action |
|-----|------|
| Arrow Keys | Move the selected fish |
| Space | Switch between fish |
| R | Restart current level |

---

## Game Objects

Different objects exist in the environment.

### Movable Objects

- Cup (light)
- Rock (heavy)
- Anchor (heavy, limited horizontal movement)
- Bomb (explodes when falling on objects)
- Trap

### Static Objects

- Wall
- Steel Pipe
- Log
- Wall with hole

Each object has specific interaction rules with the fish.

---

## Level System

Levels are loaded from text files named:

roomN.txt

Example characters used in level files:

| Symbol | Object |
|------|------|
| B | Big Fish |
| S | Small Fish |
| W | Wall |
| H | Horizontal Steel Pipe |
| V | Vertical Steel Pipe |
| C | Cup |
| R | Rock |
| A | Anchor |
| b | Bomb |
| T | Trap |
| Y | Log |
| X | Wall with hole |
| (space) | Water |

The game always starts at:

room0.txt

When both fish reach the exit, the next level is loaded automatically.

---

## Highscores

At the end of the game, the system records player performance based on:

- Total time taken
- Number of moves

The top 10 scores are stored persistently in the filesystem and displayed at the end of the game.

---

## Technologies

- Java
- Object-Oriented Programming
- File I/O
- Java Collections
- Exception handling

The graphical interface is provided by the course framework using:

- ImageGUI
- ImageTile

---

## OOP Concepts Used

This project demonstrates several Object-Oriented Programming concepts:

- Inheritance
- Abstract classes
- Interfaces
- Encapsulation
- Polymorphism
- Collections (Lists / Maps)
- Exception handling
- File reading and writing

---

## Running the Project

1. Clone the repository

git clone https://github.com/rubenfonseca25/FishFillets.git

2. Open the project in Eclipse (or another Java IDE)

3. Run the main class:

Main.java in src/pt/iscte/poo/game

Make sure the images folder is present in the project root so the graphical assets can be loaded correctly.

---

## Authors

- Rúben Lopes Fonseca  
- Luís Pacheco

Developed as a group project for the Object-Oriented Programming course.

---

## Academic Context

This project was developed for an Object-Oriented Programming course, focusing on:

- software design
- object interaction
- extensible architecture
- clean code organization