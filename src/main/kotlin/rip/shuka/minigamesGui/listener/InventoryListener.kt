package rip.shuka.minigamesGui.listener

import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import rip.shuka.minigamesGui.game.GameFactory
import rip.shuka.minigamesGui.game.GameInventory

class InventoryListener : Listener {
	private val customInventoryTitles: MutableList<Component?> = GameFactory.getAvailableGames().map { it.title }.toMutableList()

	@EventHandler
	fun onInventoryDrag(event: InventoryDragEvent) {
		for (title in customInventoryTitles) {
			if (event.view.title() == title) {
				event.isCancelled = true
				break
			}
		}
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		for (title in customInventoryTitles) {
			if (event.view.title() == title) {
				event.isCancelled = true
				break
			}
		}

		val inventory = event.clickedInventory ?: return

		if (inventory.holder is GameInventory) {
			val holder = inventory.holder as GameInventory
			holder.onClick(event)
		}
	}
}