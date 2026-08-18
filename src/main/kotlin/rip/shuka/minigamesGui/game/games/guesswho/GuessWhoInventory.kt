package rip.shuka.minigamesGui.game.games.guesswho

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import rip.shuka.minigamesGui.game.GameInventory
import rip.shuka.minigamesGui.utils.ItemStacksUtil.createBasicItemStack
import rip.shuka.minigamesGui.utils.ItemStacksUtil.formatMaterialName
import rip.shuka.minigamesGui.utils.SoundUtil

class GuessWhoInventory : GameInventory(6) {
	private val quitIndex = 53
	private val nextIndex = 35
	private val infoIndex = 17
	private val playerMaterialIndex = 8

	private lateinit var board: Array<Array<Material>>
	private var isCurrentTurn = false

	private var itemNoteboxStates = Array(3) { BooleanArray(7) { true } }

	override fun initialize() {
		for (i in 0 until rows * 9) {
			inventory.setItem(i, createBasicItemStack(Material.BLACK_STAINED_GLASS_PANE, "", NamedTextColor.DARK_GRAY))
		}
		setQuitItem(quitIndex)
		inventory.setItem(infoIndex, createBasicItemStack(Material.BOOK, "Info: Voicechat required!", NamedTextColor.AQUA))
	}

	fun initBoard(board: Array<Array<Material>>, isCurrentTurn: Boolean, playersMaterial: Material) {
		this.board = board
		this.isCurrentTurn = isCurrentTurn

		// 3x8 grid
		for (row in 0 until 3) {
			for (col in 0 until 7) {
				val itemIndex = col + (9 * row * 2)
				val titleCaseName = formatMaterialName(board[row][col])
				inventory.setItem(itemIndex, createBasicItemStack(board[row][col], "$titleCaseName (Left Click: Submit)", NamedTextColor.GRAY))

				val itemCheckboxIndex = col + (9 * row * 2) + 9
				inventory.setItem(itemCheckboxIndex, createBasicItemStack(Material.LIME_STAINED_GLASS_PANE, "✓ $titleCaseName", NamedTextColor.GREEN))
			}
		}

		inventory.setItem(playerMaterialIndex, createBasicItemStack(playersMaterial, "Your Character", NamedTextColor.GOLD))
		val text = if (isCurrentTurn) "Finish Turn" else "Wait for your turn..."
		inventory.setItem(nextIndex, createBasicItemStack(Material.ARROW, text, NamedTextColor.GREEN))
	}

	fun setTurn(isCurrent: Boolean) {
		this.isCurrentTurn = isCurrent
		val text = if (isCurrentTurn) "Finish Turn" else "Wait for your turn..."
		inventory.setItem(nextIndex, createBasicItemStack(Material.ARROW, text, NamedTextColor.GREEN))
	}

	override fun onClick(originalEvent: InventoryClickEvent) {
		val player = originalEvent.whoClicked as Player
		val index = originalEvent.rawSlot

		when (index) {
			quitIndex -> {
				if (handleQuitClick(originalEvent, quitIndex)) return
			}
			nextIndex -> game.receiveEvent("next", player)
		}

		val isItemIndex = (index in 0..6) || (index in 18..24) || (index in 36..42)
		if (isItemIndex) {
			val row = index / 18
			val col = index % 9
			if (col in 0..6) {
				val chosenMaterial = board[row][col]
				game.receiveEvent("select", player, chosenMaterial)
				return
			}
		}

		val isCheckboxIndex = (index in 9..15) || (index in 27..33) || (index in 45..51)
		if (isCheckboxIndex) {
			val row = (index - 9) / 18
			val col = (index - 9) % 9
			if (col in 0..6) {
				SoundUtil.playDefaultSelectSound(player)
				itemNoteboxStates[row][col] = !itemNoteboxStates[row][col]
				val material = if (itemNoteboxStates[row][col]) Material.LIME_STAINED_GLASS_PANE else Material.RED_STAINED_GLASS_PANE
				val itemName = formatMaterialName(board[row][col])
				val status = if (itemNoteboxStates[row][col]) "✓ $itemName" else "✗ $itemName"
				inventory.setItem(index, createBasicItemStack(material, status, if (itemNoteboxStates[row][col]) NamedTextColor.GREEN else NamedTextColor.RED))
			}
		}
	}
}
