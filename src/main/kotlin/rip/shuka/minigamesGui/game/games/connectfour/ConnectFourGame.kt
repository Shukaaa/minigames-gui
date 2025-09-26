package rip.shuka.minigamesGui.game.games.connectfour

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.SoundUtil

class ConnectFourGame : Game({ ConnectFourInventory() }) {
	override val key: String = "connectfour"
	override val name: String = "Connect Four"
	override val title: Component = Component.text("Connect Four")
	override val description: String = "Play a game of Connect Four against another player. Whoever gets four in a row first wins!"

	private val rows = 6
	private val cols = 7
	private val board = Array(rows) { CharArray(cols) { '-' } }
	private var currentTurn = 0

	override fun initialize() {
		player.forEach { p ->
			val playerSymbol = if (player.indexOf(p) % 2 == 0) 'X' else 'O'
			val isCurrentTurn = player.indexOf(p) % 2 == currentTurn

			p.player.openInventory(p.inventoryHolder.inventory)
			(p.inventoryHolder as ConnectFourInventory).initCurrentTurn(player[currentTurn].player, playerSymbol, isCurrentTurn)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "quit") {
			val quittingPlayer = data[0] as Player
			SoundUtil.playQuitSound(*player.toTypedArray())
			player.forEach { p ->
				p.player.closeInventory()
				if (p.player != quittingPlayer) {
					MessageSender.send("${quittingPlayer.name} has left the game.", p.player, MessageStatus.FAILURE)
				} else {
					MessageSender.send("You have left the game.", p.player, MessageStatus.NEUTRAL)
				}
			}
		}

		if (eventId == "click") {
			val clickingPlayer = data[0] as Player
			val col = data[1] as Int

			val teamIndex = player.indexOfFirst { it.player == clickingPlayer } % 2
			if (teamIndex != currentTurn) {
				SoundUtil.playErrorSound(clickingPlayer)
				MessageSender.send("It's not your turn! Wait for your opponent.", clickingPlayer, MessageStatus.FAILURE)
				return
			}

			val row = getAvailableRow(col)
			if (row == -1) {
				SoundUtil.playErrorSound(clickingPlayer)
				MessageSender.send("This column is full! Choose another one.", clickingPlayer, MessageStatus.FAILURE)
				return
			}

			val symbol = if (currentTurn == 0) 'X' else 'O'
			board[row][col] = symbol

			SoundUtil.playDefaultSelectSound(*player.toTypedArray())

			player.forEach { p ->
				val playerSymbol = if (player.indexOf(p) % 2 == 0) 'X' else 'O'
				val isCurrentTurn = player.indexOf(p) % 2 == (currentTurn + 1) % 2

				(p.inventoryHolder as ConnectFourInventory).updateBoard(board)
				p.inventoryHolder.initCurrentTurn(player[(currentTurn + 1) % 2].player, playerSymbol, isCurrentTurn)
			}

			if (checkWin(row, col, symbol)) {
				player.forEach { p ->
					p.player.closeInventory()
					SoundUtil.playQuitSound(*player.toTypedArray())
					if (p.player == clickingPlayer) {
						SoundUtil.playWinningSound(p)
						MessageSender.send("You win!", p.player, MessageStatus.SUCCESS)
					} else {
						SoundUtil.playFailureSound(p)
						MessageSender.send("You lose!", p.player, MessageStatus.FAILURE)
					}
				}
			} else if (board.all { it.all { cell -> cell != '-' } }) {
				player.forEach { p ->
					p.player.closeInventory()
					SoundUtil.playQuitSound(*player.toTypedArray())
					MessageSender.send("It's a draw!", p.player, MessageStatus.NEUTRAL)
				}
			} else {
				currentTurn = (currentTurn + 1) % 2
			}
		}
	}

	private fun getAvailableRow(col: Int): Int {
		for (row in rows - 1 downTo 0) {
			if (board[row][col] == '-') return row
		}
		return -1
	}

	private fun checkWin(row: Int, col: Int, symbol: Char): Boolean {
		fun count(dx: Int, dy: Int): Int {
			var r = row + dx
			var c = col + dy
			var count = 0
			while (r in 0 until rows && c in 0 until cols && board[r][c] == symbol) {
				count++
				r += dx
				c += dy
			}
			return count
		}
		val directions = listOf(
			Pair(0, 1), Pair(1, 0), Pair(1, 1), Pair(1, -1)
		)
		for ((dx, dy) in directions) {
			val total = 1 + count(dx, dy) + count(-dx, -dy)
			if (total >= 4) return true
		}
		return false
	}
}
