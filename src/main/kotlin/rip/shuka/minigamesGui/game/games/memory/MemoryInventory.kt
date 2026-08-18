package rip.shuka.minigamesGui.game.games.memory

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import rip.shuka.minigamesGui.game.GameInventory
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createBasicItemStack
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createNamelessItemStack
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createPlayerHead
import rip.shuka.minigamesGui.utils.ItemStacksUtil.formatMaterialName

class MemoryInventory : GameInventory(4) {
	private val quitIndex = 35
	private val currentTurnIndex = 8

	override fun initialize() {
		for (i in 0 until rows * 9) {
			inventory.setItem(i, createBasicItemStack(Material.GRAY_STAINED_GLASS_PANE, "", NamedTextColor.DARK_GRAY))
			if (i % 9 == 4) {
				inventory.setItem(i, createNamelessItemStack(Material.BLACK_STAINED_GLASS_PANE))
			}
		}
		this.setQuitItem(quitIndex)
	}

	override fun onClick(originalEvent: InventoryClickEvent) {
		val player = originalEvent.whoClicked as Player
		val index = originalEvent.rawSlot

		if (handleQuitClick(originalEvent, quitIndex)) return

		val row = index / 9
		val col = index % 9
		if (row in 0..3 && col in 0..3) {
			game.receiveEvent("pick", player, row, col)
		}
	}

	fun initCurrentTurn(currentPlayer: Player, revealed: Array<BooleanArray>, board: Array<Array<Material>>) {
		inventory.setItem(currentTurnIndex, createPlayerHead(currentPlayer.name, "Current Turn: ${currentPlayer.name}", NamedTextColor.GOLD))
		updateBoard(revealed, board)
	}

	fun updateBoard(revealed: Array<BooleanArray>, board: Array<Array<Material>>) {
		for (i in 0..3) {
			for (j in 0..3) {
				val index = i * 9 + j
				if (revealed[i][j]) {
					inventory.setItem(index, createBasicItemStack(board[i][j], formatMaterialName(board[i][j]), NamedTextColor.GREEN))
				} else {
					inventory.setItem(index, createBasicItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "Hidden", NamedTextColor.GRAY))
				}
			}
		}
	}
}
