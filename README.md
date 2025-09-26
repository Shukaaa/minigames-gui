# Minigames GUI Plugin

A Minecraft plugin providing a GUI for various minigames. <br>
Players can challenge each other in games like Tic Tac Toe, Connect Four, Rock Paper Scissors, and Memory, and more all within a custom GUI.

## Features

- Multiple minigames available in-game
- Easy-to-use graphical interface
- Sound and message feedback for game events
- Extensible API for adding new minigames

## Installation

> Implementing automated releases soon...

## Usage

- Use `/minigames` to see the list of available minigames.
- Use `/minigames <game> <player>` to invite a player to a specific game.

## Supported Minigames

- Tic Tac Toe
- Connect Four
- Rock Paper Scissors
- Memory
- Guess Who

## How to Contribute: Adding New Minigames

You can easily add new minigames by implementing the provided API. Follow these steps:

### 1. Create a Game Class

Extend the abstract `Game` class and implement required properties and methods:

```kotlin
class MyNewGame : Game({ MyNewGameInventory() }) {
    override val key: String = "mynewgame"
    override val name: String = "My New Game"
    override val title: Component = Component.text("My New Game")
    override val description: String = "Description of your new game."

    override fun initialize() {
        // Initialize game state and open inventories for players for example: 
	players.forEach { it.player.openInventory(inventory) }
    }

    override fun receiveEvent(eventId: String, vararg data: Any) {
        // Handle game events (e.g., click)
    }
}
```

### 2. Create an GameInventory Class

Extend the `GameInventory` class to define the GUI for your game:

```kotlin
class MyNewGameInventory : GameInventory(3) {
	val QUIT_INDEX = 26
	
    override fun initialize() {
        // Set up inventory for example:
        setQuitItem(QUIT_INDEX)
    }

    override fun onClick(originalEvent: InventoryClickEvent) {
        // Handle inventory clicks and send events to the game for example:
        this.game.receiveEvent("click", originalEvent)
    }
}
```

### 3. Register Your Game

Add your game to the GameFactory:

```kotlin
val games: List<() -> Game> = listOf(
    { TicTacToeGame() },
    ... // other games
    { MyNewGame() } // <-- Add your game here
)
```

### 4. Test Your Game

- Start your server and verify your game appears in the commands.
- Playtest to ensure all features work as expected.

### API Reference

- `Game`: Abstract class to extend for creating new games.
- `GameInventory`: Class to extend for creating custom game GUIs.
- `GamePlayer`: Data class linking a player to their inventory.
- Utility classes: `SoundUtil`, `ItemStackUtil`, `MessageSender` for common tasks.

There are also existing games in the `games` package that you can refer to for examples. <br>
The `Game` and `GameInventory` classes provide a lot of functions and properties to make development easier and keep consistency across games. Make sure to check them out!