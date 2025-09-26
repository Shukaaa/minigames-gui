package rip.shuka.minigamesGui.game.games.tictactoe

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit.createInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import rip.shuka.minigamesGui.game.GameInventory
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createBasicItemStack
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createNamelessItemStack
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createPlayerHead

class TicTacToeInventory : GameInventory() {
	private val quitIndex = 12

	override fun initialize() {
		val rows = 3
		this.inventory = createInventory(this, 9 * rows, this.title)

		for (i in 0..<rows) {
			for (j in 0..8) {
				if (j == 5) {
					inventory.setItem(i * 9 + j, createNamelessItemStack(Material.BLACK_STAINED_GLASS_PANE))
					continue
				}

				inventory.setItem(i * 9 + j, createNamelessItemStack(Material.GRAY_STAINED_GLASS_PANE))
			}
		}

		inventory.setItem(quitIndex, createBasicItemStack(Material.CHERRY_SIGN, "Quit", NamedTextColor.RED))
	}

	override fun onClick(originalEvent: InventoryClickEvent) {
		val player = originalEvent.whoClicked as Player
		val index = originalEvent.rawSlot

		if (index == quitIndex) {
			game.receiveEvent("quit", player)
		}

		if ((index + 1) % 9 === 0 || (index + 1) % 9 >= 7) {
			val row = index / 9
			val col = (index % 9) - 6

			game.receiveEvent("click", player, row, col)
		}
	}

	fun initCurrentTurn(currentPlayer: Player) {
		inventory.setItem(10, createPlayerHead(currentPlayer.name, currentPlayer.name + "'s Turn"))
	}

	fun updateBoard(board: Array<CharArray?>) {
		for (i in 0..2) {
			for (j in 0..2) {
				val c = board[i]!![j]
				val index = i * 9 + j + 6
				when (c) {
					'X' -> {
						inventory.setItem(
							index,
							createBasicItemStack(Material.RED_STAINED_GLASS_PANE, "X", NamedTextColor.RED)
						)
					}
					'O' -> {
						inventory.setItem(
							index,
							createBasicItemStack(Material.BLUE_STAINED_GLASS_PANE, "O", NamedTextColor.BLUE)
						)
					}
					else -> {
						inventory.setItem(index, createNamelessItemStack(Material.GRAY_STAINED_GLASS_PANE))
					}
				}
			}
		}
	}
}