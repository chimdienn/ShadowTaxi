# 🚕 ShadowTaxi

A 2D taxi simulation game built in Java using the Bagel game engine.
This project was developed as part of SWEN20003 Object Oriented Software Development at the University of Melbourne.

---

## 🎮 Gameplay

- You are a taxi driver stuck on an endless road in the middle of an economic crisis.  
- Control the taxi with the **arrow keys**:
  - ⬅️ ➡️ Move left / right  
  - ⬆️ Drive forward  
- Pick up **passengers** and drop them off at their **trip end flags**.  
- Earn money based on:
  - Distance travelled  
  - Passenger priority (boosted by collecting coins)  
- Avoid **enemy cars, random traffic, and hazards** — collisions cause damage.  
- The game ends if:
  - Taxi, driver, or passenger health drops to 0  
  - You exceed 15,000 frames without beating the target  
  - A taxi leaves the screen without you boarding it  

To win: reach the target score of 500 before time runs out

---
## 🖥️ How to Run

1. Open the project in **IntelliJ IDEA** (or another Java IDE).  
2. Navigate to: src/ShadowTaxi.java
3. Run the `main` method.  
4. The game window will appear:
- Enter your **username** on the player info screen.  
- Press **Enter** to start playing.  
5. After each game, your **username and score** are recorded in: /res/scores.csv
6. From the end screen, press **Space** to restart and play again.

---
## 🛠️ Tech Stack

- **Language**: Java  
- **Engine**: Bagel (Basic Academic Game Engine Library)  
- **IDE**: IntelliJ IDEA

---
## 🏆 Features
- Home screen, player info screen, main game screen, and end screen  
- Continuous scrolling background  
- Passengers with varying priorities  
- Coins and power-ups  
- Random traffic and enemy cars (with fireballs)  
- Health systems for driver, passenger, and taxi  
- Score recording and leaderboard display  
  
