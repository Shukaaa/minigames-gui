package rip.shuka.minigamesGui.game.games.rps

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import rip.shuka.minigamesGui.game.GameInventory
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createBasicItemStack

class RockPaperScissorsInventory : GameInventory(1) {
	private val rockIndex = 2
	private val paperIndex = 4
	private val scissorsIndex = 6
	private val quitIndex = 8

	override fun initialize() {
		inventory.setItem(rockIndex, createBasicItemStack(Material.STONE, "Rock", NamedTextColor.GRAY))
		inventory.setItem(paperIndex, createBasicItemStack(Material.PAPER, "Paper", NamedTextColor.WHITE))
		inventory.setItem(scissorsIndex, createBasicItemStack(Material.SHEARS, "Scissors", NamedTextColor.AQUA))
		inventory.setItem(quitIndex, createBasicItemStack(Material.CHERRY_SIGN, "Quit", NamedTextColor.RED))
		for (i in 0..8) {
			if (inventory.getItem(i) == null) {
				inventory.setItem(i, createBasicItemStack(Material.BLACK_STAINED_GLASS_PANE, "", NamedTextColor.DARK_GRAY))
			}
		}
	}

	override fun onClick(originalEvent: InventoryClickEvent) {
		val player = originalEvent.whoClicked as Player
		val index = originalEvent.rawSlot

		when (index) {
			rockIndex -> game.receiveEvent("choose", player, "Rock")
			paperIndex -> game.receiveEvent("choose", player, "Paper")
			scissorsIndex -> game.receiveEvent("choose", player, "Scissors")
			quitIndex -> game.receiveEvent("quit", player)
		}
	}
}
