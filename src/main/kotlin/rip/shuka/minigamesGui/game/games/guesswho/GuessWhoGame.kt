package rip.shuka.minigamesGui.game.games.guesswho

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.utils.SoundUtil

class GuessWhoGame : Game({ GuessWhoInventory() }) {
	override val key = "guesswho"
	override val name = "Guess Who"
	override val title = Component.text("Guess Who")
	override val description = "Voicechat required! Guess your opponent's item by asking questions. Use the notepad row to mark eliminated items."

	val ingredientKeywords = listOf("_INGOT", "GEM", "DUST", "CRYSTAL", "NUGGET", "STAR", "_AXE", "_HOE", "_PICKAXE", "_SHOVEL", "_SWORD", "_HELMET", "_CHESTPLATE", "_LEGGINGS", "_BOOTS")
	val autoItemPool = Material.entries.filter { mat ->
		ingredientKeywords.any { keyword -> mat.name.contains(keyword) }
	}
	private val itemPool = listOf(
		*this.autoItemPool.toTypedArray(),
		// INGREDIENTS
		Material.COAL, Material.CHARCOAL, Material.REDSTONE, Material.EMERALD, Material.DIAMOND, Material.LAPIS_LAZULI,
		Material.QUARTZ, Material.AMETHYST_SHARD, Material.NETHERITE_SCRAP, Material.STICK, Material.FLINT,
		Material.BLAZE_ROD, Material.GHAST_TEAR, Material.SLIME_BALL, Material.MAGMA_CREAM, Material.SPIDER_EYE,
		Material.RABBIT_FOOT, Material.FEATHER, Material.LEATHER, Material.STRING, Material.INK_SAC, Material.GUNPOWDER,
		Material.PHANTOM_MEMBRANE, Material.PRISMARINE_SHARD, Material.NAUTILUS_SHELL, Material.HEART_OF_THE_SEA,
		Material.SUGAR, Material.HONEYCOMB, Material.BONE, Material.BONE_MEAL,
		// FOOD
		Material.APPLE, Material.BAKED_POTATO, Material.BREAD, Material.CARROT, Material.CHORUS_FRUIT, Material.COOKIE,
		Material.GOLDEN_CARROT, Material.GOLDEN_APPLE, Material.MELON_SLICE, Material.POTATO, Material.PUMPKIN_PIE, Material.SWEET_BERRIES,
		Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.COOKED_COD, Material.COOKED_MUTTON, Material.COOKED_PORKCHOP,
		Material.COOKED_SALMON, Material.COOKED_RABBIT, Material.COD, Material.MUTTON, Material.PORKCHOP,
		Material.RABBIT, Material.CHICKEN, Material.SALMON, Material.TROPICAL_FISH, Material.MUSHROOM_STEW, Material.RABBIT_STEW,
		Material.SUSPICIOUS_STEW, Material.BEEF, Material.EGG, Material.HONEY_BOTTLE,
		// Not Keyworded Tools
		Material.FISHING_ROD, Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK, Material.SHEARS,
		Material.BOW, Material.CROSSBOW, Material.TRIDENT, Material.SHIELD, Material.FLINT_AND_STEEL,
		// Miscellaneous
		Material.BOOK, Material.CLOCK, Material.COMPASS, Material.FIRE_CHARGE, Material.MAP, Material.NAME_TAG, Material.PAPER,
		Material.TNT, Material.TORCH, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.BUCKET, Material.ELYTRA,
		Material.LEAD, Material.SADDLE, Material.SHIELD, Material.CAULDRON, Material.BELL, Material.CAMPFIRE, Material.LANTERN, Material.SOUL_LANTERN,
		// Not Keyworded Redstone Stuff
		Material.DISPENSER, Material.DROPPER, Material.HOPPER, Material.COMPARATOR, Material.REPEATER, Material.PISTON, Material.STICKY_PISTON,
		Material.TRIPWIRE_HOOK
	)

	private lateinit var board: Array<Array<Material>>
	private lateinit var playerMaterials: Map<Player, Material>

	private var currentTurn = 0

	override fun initialize() {
		val flatBoardSize = 3 * 7
		require(itemPool.size >= flatBoardSize) { "Item pool must be at least $flatBoardSize items." }
		val shuffledItems = itemPool.shuffled().take(flatBoardSize)
		board = Array(3) { row -> Array(7) { col -> shuffledItems[col + (row * 7)] } }

		playerMaterials = player.associate { p ->
			val randomItem = board.flatten().shuffled().first()
			p.player to randomItem
		}

		player.forEach { p ->
			p.player.openInventory(p.inventoryHolder.inventory)
			(p.inventoryHolder as GuessWhoInventory).initBoard(board, currentTurn == player.indexOf(p), playerMaterials[p.player]!!)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		val clickingPlayer = data[0] as Player
		val teamIndex = player.indexOfFirst { it.player == clickingPlayer }
		if (teamIndex != currentTurn) {
			SoundUtil.playErrorSound(clickingPlayer)
			MessageSender.send("It's not your turn! Wait for your opponent.", clickingPlayer)
			return
		}

		when (eventId) {
			"select" -> {
				val item = data[1] as Material
				val enemyPlayer = player[(teamIndex + 1) % 2].player
				val enemyItem = playerMaterials[enemyPlayer]!!

				player.forEach { p ->
					MessageSender.send("${clickingPlayer.name} selected ${item.name}.", p.player)
				}

				if (item == enemyItem) {
					this.endGameWithWinner(clickingPlayer, "You guessed correctly! You win!", "You guessed correctly! You win!")
				} else {
					this.endGameWithWinner(enemyPlayer, "Your opponent guessed the wrong item! You win!", "You guessed wrong! You lose!")
				}
			}
			"next" -> {
				player.forEach { p ->
					SoundUtil.playSound("minecraft:block.note_block.pling", p.player)
					MessageSender.send("${clickingPlayer.name} ended their turn.", p.player)
				}
				currentTurn = (currentTurn + 1) % 2
				player.forEach { p ->
					(p.inventoryHolder as GuessWhoInventory).setTurn(currentTurn == player.indexOf(p))
				}
			}
		}
	}
}
