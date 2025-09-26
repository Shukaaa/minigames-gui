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

	private val itemPool = listOf(
		Material.DIAMOND,
		Material.GOLD_INGOT,
		Material.IRON_INGOT,
		Material.EMERALD,
		Material.REDSTONE,
		Material.LAPIS_LAZULI,
		Material.COAL,
		Material.QUARTZ,
		Material.COPPER_INGOT,
		Material.NETHERITE_SCRAP,
		Material.PRISMARINE_SHARD,
		Material.BLAZE_ROD,
		Material.GHAST_TEAR,
		Material.SLIME_BALL,
		Material.MAGMA_CREAM,
		Material.SPIDER_EYE,
		Material.RABBIT_FOOT,
		Material.FEATHER
	)

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
		player.forEach { p ->
			foundPairs[p.player] = 0
			p.player.openInventory(p.inventoryHolder.inventory)
			(p.inventoryHolder as MemoryInventory).initCurrentTurn(player[turn].player, revealed, board)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "quit") {
			val quittingPlayer = data[0] as Player
			SoundUtil.playQuitSound(*player.toTypedArray())
			player.forEach { p ->
				p.player.closeInventory()
				if (p.player != quittingPlayer) {
					MessageSender.send("${quittingPlayer.name} has quit the game.", p.player, MessageStatus.FAILURE)
				} else {
					MessageSender.send("You have quit the game.", p.player, MessageStatus.NEUTRAL)
				}
			}
			return
		}

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

			SoundUtil.playDefaultSelectSound(*player.toTypedArray())
			revealed[row][col] = true
			player.forEach { p ->
				(p.inventoryHolder as MemoryInventory).updateBoard(revealed, board)
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
					player.forEach { p ->
						(p.inventoryHolder as MemoryInventory).initCurrentTurn(player[turn].player, revealed, board)
					}
				} else {
					MessageSender.send("No pair!", pickingPlayer, MessageStatus.FAILURE)
					SoundUtil.playErrorSound(pickingPlayer)
					Bukkit.getScheduler().runTaskLater(MinigamesGUI.instance, Runnable {
						revealed[r1][c1] = false
						revealed[row][col] = false
						player.forEach { p ->
							(p.inventoryHolder as MemoryInventory).updateBoard(revealed, board)
						}
						firstPick = null
						turn = (turn + 1) % 2
						player.forEach { p ->
							(p.inventoryHolder as MemoryInventory).initCurrentTurn(player[turn].player, revealed, board)
						}
					}, 40L)
				}

				if (foundPairs.values.sum() == (size * size) / 2) {
					val winner = foundPairs.maxByOrNull { it.value }?.key
					player.forEach { p ->
						p.player.closeInventory()
						if (p.player == winner) {
							SoundUtil.playWinningSound(p)
							MessageSender.send("You win!", p.player, MessageStatus.SUCCESS)
						} else {
							SoundUtil.playFailureSound(p)
							MessageSender.send("You lose!", p.player, MessageStatus.FAILURE)
						}
					}
				}
			}
		}
	}
}
