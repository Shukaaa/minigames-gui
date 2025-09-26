package rip.shuka.minigamesGui.game

import rip.shuka.minigamesGui.game.games.tictactoe.TicTacToeGame

class GameFactory {
	companion object {
		val games: List<() -> Game> = listOf { TicTacToeGame() }

		fun getAvailableGames(): List<Game> {
			return games.map { it() }
		}

		fun createGame(key: String): Game? {
			val entry = games.find { it().key == key }
			return entry?.invoke()
		}
	}
}