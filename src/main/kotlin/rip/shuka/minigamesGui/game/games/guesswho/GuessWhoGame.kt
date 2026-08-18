package rip.shuka.minigamesGui.game.games.guesswho

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.ItemStacksUtil.formatMaterialName
import rip.shuka.minigamesGui.utils.SoundUtil

class GuessWhoGame : Game({ GuessWhoInventory() }) {
	override val key = "guesswho"
	override val name = "Guess Who"
	override val title = Component.text("Guess Who")
	override val description = "Voicechat required! Guess your opponent's item by asking questions. Use the notepad row to mark eliminated items and select the item you think your opponent has. The first player to guess correctly wins!"
	override val icon = Material.PLAYER_HEAD

	private val itemPool = Material.entries.filter { material ->
		material.isItem && !material.isBlock
	}

	private lateinit var board: Array<Array<Material>>
	private lateinit var playerMaterials: Map<Player, Material>

	private var currentTurn = 0

	override fun initialize() {
		val flatBoardSize = 3 * 7
		require(itemPool.size >= flatBoardSize) { "Item pool must be at least $flatBoardSize items." }
		val shuffledItems = itemPool.shuffled().take(flatBoardSize)
		board = Array(3) { row -> Array(7) { col -> shuffledItems[col + (row * 7)] } }

		val playerItems = board.flatten().distinct().shuffled().take(player.size)
		require(playerItems.size == player.size) { "There must be one unique item per player." }
		playerMaterials = player.mapIndexed { index, gamePlayer ->
			gamePlayer.player to playerItems[index]
		}.toMap()

		openPlayerInventories()
		forEachPlayerInventory<GuessWhoInventory> { gamePlayer, inventory ->
			inventory.initBoard(board, currentTurn == player.indexOf(gamePlayer), playerMaterials[gamePlayer.player]!!)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		val clickingPlayer = data[0] as Player
		val teamIndex = player.indexOfFirst { it.player == clickingPlayer }
		if (teamIndex == -1) return

		if (teamIndex != currentTurn) {
			SoundUtil.playErrorSound(clickingPlayer)
			MessageSender.send("It's not your turn! Wait for your opponent.", clickingPlayer, MessageStatus.FAILURE)
			return
		}

		when (eventId) {
			"select" -> {
				val item = data[1] as Material
				val enemyPlayer = player[(teamIndex + 1) % 2].player
				val enemyItem = playerMaterials[enemyPlayer]!!

				player.forEach { p ->
					MessageSender.send("${clickingPlayer.name} selected ${formatMaterialName(item)}.", p.player, MessageStatus.NEUTRAL)
				}

				if (item == enemyItem) {
					this.endGameWithWinner(
						clickingPlayer,
						"You guessed correctly! You win!",
						"Your opponent guessed correctly! You lose!"
					)
				} else {
					this.endGameWithWinner(enemyPlayer, "Your opponent guessed the wrong item! You win!", "You guessed wrong! You lose!")
				}
			}
			"next" -> {
				player.forEach { p ->
					SoundUtil.playSound("minecraft:block.note_block.pling", p.player)
					MessageSender.send("${clickingPlayer.name} ended their turn.", p.player, MessageStatus.NEUTRAL)
				}
				currentTurn = (currentTurn + 1) % 2
				forEachPlayerInventory<GuessWhoInventory> { gamePlayer, inventory ->
					inventory.setTurn(currentTurn == player.indexOf(gamePlayer))
				}
			}
		}
	}
}
