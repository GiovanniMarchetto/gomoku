[![Build Status](https://app.travis-ci.com/GiovanniMarchetto/gomoku.svg?token=zp6V5dbTEDGt116fLaVG&branch=main)](https://app.travis-ci.com/GiovanniMarchetto/gomoku)

Gomoku
======

Repository for the Software Development Method course's project (A.Y. 2020-2021).

## What is Gomoku
Gomoku, also called _Five in a Row_, is an abstract strategy board game. It is traditionally played with Go pieces (black and white stones) on a Go board. It is played using a 15×15 board, while in the past a 19×19 board was standard. Because pieces are typically not moved or removed from the board, gomoku may also be played as a paper-and-pencil game. The game is known in several countries under different names.

## Rules
Players alternate turns placing a stone of their color on an empty intersection. Black plays first. The winner is the first player to form an unbroken chain of five stones horizontally, vertically, or diagonally. Placing so that a line of more than five stones of the same color is created does not result in a win. These are called overlines.

## Structure
The main packages:
- _model_, in this part there is the model for the game
- _mvvm_library_, in this package there are some interfaces and abstract classes that are the base for the
  implementation of the Model-View-Viewmodel paradigm
- _ui_, this is the package for the user-interface where there are the MainViewmodel and 3 packages:
	- _cli_, command line interface package
	- _gui_, graphical user interface package
	- _support_, some support classes that are used in both cli and gui

## Screenshots from the GUI
### Start view
Where users can setup the match.
![startView](https://user-images.githubusercontent.com/74369390/143583304-3dd942fb-517f-449c-9f74-65b0cf1278a2.png)

### Main view
During the execution of a game.
![mainView](https://user-images.githubusercontent.com/74369390/143583367-05f43f84-90e9-46c6-bd86-51a267f421de.png)

### Summary view
Summarizes the result of the last game.
![summaryView](https://user-images.githubusercontent.com/74369390/143583483-7be82b00-54f9-4c71-adab-440892ef4604.png)

### Demo
Example of a CPU vs CPU game.
https://user-images.githubusercontent.com/74369390/143583560-fa5c1f00-7c3d-459a-aacb-864e3272107b.mp4
