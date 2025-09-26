package rip.shuka.minigamesGui.game.games.tictactoe

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.SoundUtil

class TicTacToeGame : Game({ TicTacToeInventory() }) {
	override val key: String = "tictactoe"
	override val name: String = "Tic Tac Toe"
	override val title: Component = Component.text("Tic Tac Toe")
	override val description: String = "Play a game of Tic Tac Toe against another player. Whoever gets three in a row first wins!"

	private val board = arrayOf<CharArray?>(
		charArrayOf('-', '-', '-'),
		charArrayOf('-', '-', '-'),
		charArrayOf('-', '-', '-')
	)

	override fun initialize() {
		player.forEach { p ->
			val isCurrentTurn = player.indexOf(p) == 0
			val playerSymbol = if (player.indexOf(p) == 0) 'X' else 'O'

			p.player.openInventory(p.inventoryHolder.inventory)
			(p.inventoryHolder as TicTacToeInventory).initCurrentTurn(player[0].player, playerSymbol, isCurrentTurn)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "click") {
			val clickingPlayer = data[0] as Player
			val row = data[1] as Int
			val col = data[2] as Int

			val currentPlayerIndex = player.indexOfFirst { it.player == clickingPlayer }
			if (currentPlayerIndex == -1) return

			val currentSymbol = if (currentPlayerIndex == 0) 'X' else 'O'

			val totalMoves = board.sumOf { it!!.count { cell -> cell != '-' } }
			if (totalMoves % 2 != currentPlayerIndex) {
				SoundUtil.playErrorSound(clickingPlayer)
				MessageSender.send("It's not your turn! Wait for your opponent.", clickingPlayer, MessageStatus.FAILURE)
				return
			}

			if (row !in 0..2 || col !in 0..2 || board[row]!![col] != '-') {
				SoundUtil.playErrorSound(clickingPlayer)
				MessageSender.send("Invalid move! Try again.", clickingPlayer, MessageStatus.FAILURE)
				return
			}

			board[row]!![col] = currentSymbol

			SoundUtil.playDefaultSelectSound(*player.toTypedArray())
			player.forEach { p ->
				val isCurrentTurn = player.indexOf(p) == (currentPlayerIndex + 1) % 2
				val playerSymbol = if (player.indexOf(p) == 0) 'X' else 'O'

				(p.inventoryHolder as TicTacToeInventory).updateBoard(board)
				p.inventoryHolder.initCurrentTurn(player[(currentPlayerIndex + 1) % 2].player, playerSymbol, isCurrentTurn)
			}

			if (checkWin(currentSymbol)) {
				player.forEach { p ->
					p.player.closeInventory()
					if (p.player == clickingPlayer) {
						SoundUtil.playWinningSound(p)
						MessageSender.send("You win!", p.player, MessageStatus.SUCCESS)
					} else {
						SoundUtil.playFailureSound(p)
						MessageSender.send("You lose! ${clickingPlayer.name} wins!", p.player, MessageStatus.FAILURE)
					}
				}
			} else if (totalMoves + 1 == 9) {
				player.forEach { p ->
					p.player.closeInventory()
					SoundUtil.playQuitSound(p)
					MessageSender.send("It's a draw!", p.player, MessageStatus.NEUTRAL)
				}
			}
		}
	}

	private fun checkWin(symbol: Char): Boolean {
		for (i in 0..2) {
			if ((board[i]!![0] == symbol && board[i]!![1] == symbol && board[i]!![2] == symbol) ||
				(board[0]!![i] == symbol && board[1]!![i] == symbol && board[2]!![i] == symbol)
			) {
				return true
			}
		}

		return (board[0]!![0] == symbol && board[1]!![1] == symbol && board[2]!![2] == symbol) ||
			(board[0]!![2] == symbol && board[1]!![1] == symbol && board[2]!![0] == symbol)
	}
}