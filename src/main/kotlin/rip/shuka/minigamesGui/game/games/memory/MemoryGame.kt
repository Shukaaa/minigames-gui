package rip.shuka.minigamesGui.game.games.memory

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.MinigamesGUI
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.SoundUtil
import kotlin.collections.set
import kotlin.random.Random

class MemoryGame() : Game({ MemoryInventory() }) {
	override val key = "memory"
	override val name = "Memory"
	override val title = Component.text("Memory")
	override val description = "Find all matching pairs. The player with the most pairs wins!"
	override val icon = Material.CHEST

	private val itemPool = Material.entries.filter { material ->
		material.isItem && !material.isBlock
	}

	private val size = 4
	private val board = Array(size) { Array(size) { Material.AIR } }
	private val revealed = Array(size) { BooleanArray(size) { false } }
	private val foundPairs = mutableMapOf<Player, Int>()
	private var turn = 0
	private var firstPick: Pair<Int, Int>? = null

	override fun initialize() {
		val pairsNeeded = (size * size) / 2
		val items = this.itemPool.shuffled().take(pairsNeeded)
		val allItems = (items + items).shuffled(Random(System.currentTimeMillis()))
		var idx = 0
		for (i in 0 until size) {
			for (j in 0 until size) {
				board[i][j] = allItems[idx++]
			}
		}
		openPlayerInventories()
		player.forEach { p ->
			foundPairs[p.player] = 0
			(p.inventoryHolder as MemoryInventory).initCurrentTurn(player[turn].player, revealed, board)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "pick") {
			val pickingPlayer = data[0] as Player
			val row = data[1] as Int
			val col = data[2] as Int

			if (player[turn].player != pickingPlayer) {
				SoundUtil.playErrorSound(pickingPlayer)
				MessageSender.send("It's not your turn!", pickingPlayer, MessageStatus.FAILURE)
				return
			}

			if (revealed[row][col]) {
				SoundUtil.playErrorSound(pickingPlayer)
				MessageSender.send("This card is already revealed!", pickingPlayer, MessageStatus.FAILURE)
				return
			}

			SoundUtil.playDefaultSelectSound(*(player.map { it.player }).toTypedArray())
			revealed[row][col] = true
			forEachPlayerInventory<MemoryInventory> { _, inventory ->
				inventory.updateBoard(revealed, board)
			}

			if (firstPick == null) {
				firstPick = Pair(row, col)
				MessageSender.send("Pick another card.", pickingPlayer, MessageStatus.NEUTRAL)
			} else {
				val (r1, c1) = firstPick!!
				val m1 = board[r1][c1]
				val m2 = board[row][col]
				if (m1 == m2) {
					foundPairs[pickingPlayer] = foundPairs.getOrDefault(pickingPlayer, 0) + 1
					SoundUtil.playSuccessSound(pickingPlayer)
					MessageSender.send("Pair found!", pickingPlayer, MessageStatus.SUCCESS)
					firstPick = null
					// Same player picks again
					forEachPlayerInventory<MemoryInventory> { _, inventory ->
						inventory.initCurrentTurn(player[turn].player, revealed, board)
					}
				} else {
					MessageSender.send("No pair!", pickingPlayer, MessageStatus.FAILURE)
					SoundUtil.playErrorSound(pickingPlayer)
					Bukkit.getScheduler().runTaskLater(MinigamesGUI.instance, Runnable {
						revealed[r1][c1] = false
						revealed[row][col] = false
						forEachPlayerInventory<MemoryInventory> { _, inventory ->
							inventory.updateBoard(revealed, board)
						}
						firstPick = null
						turn = (turn + 1) % 2
						forEachPlayerInventory<MemoryInventory> { _, inventory ->
							inventory.initCurrentTurn(player[turn].player, revealed, board)
						}
					}, 40L)
				}

				if (foundPairs.values.sum() == (size * size) / 2) {
					val winner = foundPairs.maxByOrNull { it.value }?.key
					val winnerPairsFound = foundPairs[winner] ?: 0

					val looser = foundPairs.minByOrNull { it.value }?.key
					val looserPairsFound = foundPairs[looser] ?: 0

					if (winnerPairsFound == looserPairsFound) {
						this.endGameWithDraw("It's a draw! Both players found $winnerPairsFound pairs.")
						return
					}

					val winnerMsg = "You win! You found $winnerPairsFound pairs and the opponent found $looserPairsFound pairs."
					val looserMsg = "You lose! You found $looserPairsFound pairs and the opponent found $winnerPairsFound pairs."
					this.endGameWithWinner(winner!!, winnerMsg, looserMsg)
				}
			}
		}
	}
}
