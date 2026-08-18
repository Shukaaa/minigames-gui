# Minigames GUI Plugin

A Minecraft plugin providing a dynamic GUI for various minigames. <br>
Players can challenge each other in games like Tic Tac Toe, Connect Four, Rock Paper Scissors, Memory, Guess Who, and Numbers.

## Features

- Multiple minigames available in-game
- Dynamic game-selection GUI with game icons and descriptions
- Clickable player invitations
- Sound and message feedback for game events
- Extensible API for adding new minigames

## Installation

> Implementing automated releases soon...

## Usage

1. Use `/1v1` or `/1vs1` to open the game-selection GUI.
2. Select a game and enter an online player's name in chat. Press `Tab` for name completion.
3. The selected player receives a clickable invitation. The game starts after the invitation is accepted.
4. Type `/exit` to cancel the player-selection dialog.

## Supported Minigames

- Tic Tac Toe
- Connect Four
- Rock Paper Scissors
- Memory
- Guess Who
- Numbers Game

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
    override val icon: Material = Material.COMPASS

    override fun initialize() {
        // Initialize game state and open player inventories.
        openPlayerInventories()
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
	private val quitIndex = 26
	
    override fun initialize() {
        // Set up the inventory.
        setQuitItem(quitIndex)
    }

    override fun onClick(originalEvent: InventoryClickEvent) {
        val player = originalEvent.whoClicked as Player
        if (handleQuitClick(originalEvent, quitIndex)) return

        // Handle inventory clicks and send events to the game.
        game.receiveEvent("click", player, originalEvent.rawSlot)
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

- Start your server and verify your game appears in the `/1v1` GUI.
- Playtest to ensure all features work as expected.

### API Reference

- `Game`: Abstract class to extend for creating new games.
- `GameInventory`: Class to extend for creating custom game GUIs.
- `GamePlayer`: Data class linking a player to their inventory.
- `Game.icon`: Material displayed in the game-selection GUI.
- `Game.openPlayerInventories()`: Opens each player's dedicated inventory.
- `Game.forEachPlayerInventory()`: Applies logic to typed game inventories.
- `GameInventory.handleQuitClick()`: Handles the shared quit button.
- Utility classes: `SoundUtil`, `ItemStacksUtil`, `MessageSender` for common tasks.

## Requirements

- Paper 1.21.11
- Java 21 or newer

There are also existing games in the `games` package that you can refer to for examples. <br>
The `Game` and `GameInventory` classes provide a lot of functions and properties to make development easier and keep consistency across games. Make sure to check them out!