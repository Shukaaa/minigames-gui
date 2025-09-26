package rip.shuka.minigamesGui.game.games.numbers

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import rip.shuka.minigamesGui.game.GameInventory
import rip.shuka.minigamesGui.utils.ItemStacksUtil

class NumbersInventory : GameInventory(3) {
	val quitIndex = 26
	val scoreBookIndex = 22

	val numberItemConfig = mapOf(
		1 to Material.CANDLE,
		2 to Material.WHITE_CANDLE,
		3 to Material.LIGHT_GRAY_CANDLE,
		4 to Material.GRAY_CANDLE,
		5 to Material.BLACK_CANDLE,
		6 to Material.BROWN_CANDLE,
		7 to Material.RED_CANDLE,
		8 to Material.ORANGE_CANDLE,
		9 to Material.YELLOW_CANDLE
	)

	var alreadySelectedNumbers: MutableList<Int> = mutableListOf()
	var scores = mutableMapOf<Player, Int>()

	override fun initialize() {
		for (i in 0..8) {
			inventory.setItem(i, ItemStacksUtil.createBasicItemStack(numberItemConfig[i + 1]!!, "${i + 1}", NamedTextColor.GRAY))
		}

		for (i in 9..26) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, ItemStacksUtil.createBasicItemStack(Material.BLACK_STAINED_GLASS_PANE, "", NamedTextColor.DARK_GRAY))
			}
		}

		this.setQuitItem(quitIndex)
		inventory.setItem(scoreBookIndex, ItemStacksUtil.createBasicItemStack(Material.BOOK, "You: 0, Opponent: 0", NamedTextColor.GOLD))
	}

	override fun onClick(originalEvent: InventoryClickEvent) {
		val player = originalEvent.whoClicked as Player
		val index = originalEvent.rawSlot

		if (index == quitIndex) {
			this.sendQuitEvent(player)
			return
		}

		if (index in 0..8) {
			val number = index + 1
			game.receiveEvent("choose", originalEvent.whoClicked, number)
		}
	}

	fun updateAlreadySelectedNumbers(numbers: List<Int>) {
		alreadySelectedNumbers = numbers.toMutableList()
		for (number in alreadySelectedNumbers) {
			val index = number - 1
			inventory.setItem(index, ItemStacksUtil.createNamelessItemStack(Material.AIR))
		}
	}

	fun updateScores(scores: MutableMap<Player, Int>) {
		this.scores = scores
		inventory.setItem(
			scoreBookIndex,
			ItemStacksUtil.createBasicItemStack(
				Material.BOOK,
				"You: ${scores.keys.first().let { scores[it] ?: 0 }}, Opponent: ${scores.keys.last().let { scores[it] ?: 0 }}",
				NamedTextColor.GOLD
			)
		)
	}
}