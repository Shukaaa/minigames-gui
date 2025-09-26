package rip.shuka.minigamesGui.game.games.connectfour

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import rip.shuka.minigamesGui.game.GameInventory
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createBasicItemStack
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createPlayerHead

class ConnectFourInventory : GameInventory(6) {
	private val quitIndex = 53

	override fun initialize() {
		for (i in 0 until rows) {
			for (j in 0 until 9) {
				val index = i * 9 + j
				if (j < 7) {
					inventory.setItem(index, basicPlaygroundItemStack())
				} else {
					inventory.setItem(index, createBasicItemStack(Material.BLACK_STAINED_GLASS_PANE, "", NamedTextColor.DARK_GRAY))
				}
			}
		}
		this.setQuitItem(quitIndex)
	}

	override fun onClick(originalEvent: InventoryClickEvent) {
		val player = originalEvent.whoClicked as Player
		val index = originalEvent.rawSlot

		if (index == quitIndex) this.sendQuitEvent(player)

		val col = index % 9
		if (col in 0..6) {
			game.receiveEvent("click", player, col)
		}
	}

	fun initCurrentTurn(currentPlayer: Player, playerSymbol: Char, isCurrentTurn: Boolean) {
		val color = if (playerSymbol == 'X') NamedTextColor.RED else NamedTextColor.BLUE
		val status = if (isCurrentTurn) "Your Turn" else "You're Waiting..."
		val displayName = "${currentPlayer.name} ($playerSymbol, ${if (color == NamedTextColor.RED) "Red" else "Blue"})"
		inventory.setItem(52, createPlayerHead(currentPlayer.name, "$displayName - $status", color))
	}

	fun updateBoard(board: Array<CharArray>) {
		for (i in 0 until 6) {
			for (j in 0 until 7) {
				val c = board[i][j]
				val index = i * 9 + j
				when (c) {
					'X' -> inventory.setItem(index, createBasicItemStack(Material.RED_STAINED_GLASS_PANE, "X", NamedTextColor.RED))
					'O' -> inventory.setItem(index, createBasicItemStack(Material.BLUE_STAINED_GLASS_PANE, "O", NamedTextColor.BLUE))
					else -> inventory.setItem(index, basicPlaygroundItemStack())
				}
			}
		}
	}

	private fun basicPlaygroundItemStack(): ItemStack {
		return createBasicItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, "You can place here", NamedTextColor.GRAY)
	}
}
