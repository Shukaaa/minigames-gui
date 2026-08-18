package rip.shuka.minigamesGui.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import rip.shuka.minigamesGui.game.GameFactory
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.ItemStacksUtil

class MinigameMenuInventory : InventoryHolder {
	private val games = GameFactory.getAvailableGames()
	private val gameRows = maxOf(1, (games.size + GAMES_PER_ROW - 1) / GAMES_PER_ROW)
	private val rows = gameRows + 2
	private val inventory: Inventory

	init {
		val title = Component.text("1vs1 Minigames", NamedTextColor.BLUE)
			.decoration(TextDecoration.BOLD, true)
		inventory = org.bukkit.Bukkit.createInventory(this, rows * 9, title)
		initialize()
	}

	private fun initialize() {
		for (row in 0 until rows) {
			for (column in 0 until 9) {
				if (row == 0 || row == rows - 1 || column == 0 || column == 8) {
					inventory.setItem(row * 9 + column, ItemStacksUtil.createNamelessItemStack(Material.GRAY_STAINED_GLASS_PANE))
				}
			}
		}

		inventory.setItem(
			4,
			ItemStacksUtil.createBasicItemStack(
				Material.DIAMOND,
				Component.text("1vs1 Minigames", NamedTextColor.BLUE)
					.decoration(TextDecoration.BOLD, true),
				mutableListOf(
					Component.text("Challenge your friends to different minigames.", NamedTextColor.GRAY)
				)
			)
		)
		inventory.setItem(
			(rows - 1) * 9,
			ItemStacksUtil.createBasicItemStack(Material.CHERRY_SIGN, "Exit", NamedTextColor.RED)
		)

		for (slot in 0 until gameRows * GAMES_PER_ROW) {
			val row = slot / GAMES_PER_ROW + 1
			val column = slot % GAMES_PER_ROW + 1
			val game = games.getOrNull(slot)
			inventory.setItem(
				row * 9 + column,
				if (game == null) {
					ItemStacksUtil.createBasicItemStack(
						Material.LIGHT_GRAY_STAINED_GLASS_PANE,
						"More games coming soon",
						NamedTextColor.GRAY
					)
				} else {
					ItemStacksUtil.createBasicItemStack(
						game.icon,
						Component.text(game.name, NamedTextColor.AQUA)
							.decoration(TextDecoration.BOLD, true),
						descriptionLore(game.description)
					)
				}
			)
		}
	}

	override fun getInventory(): Inventory = inventory

	fun onClick(event: InventoryClickEvent) {
		val player = event.whoClicked as? Player ?: return
		val slot = event.rawSlot

		if (slot == (rows - 1) * 9) {
			player.closeInventory()
			return
		}

		val row = slot / 9
		val column = slot % 9
		if (row !in 1..gameRows || column !in 1..7) return

		val game = games.getOrNull((row - 1) * GAMES_PER_ROW + column - 1) ?: return
		player.closeInventory()
		MinigameSelection.begin(player, game.key)
		MessageSender.send(
			"Enter the player name to invite, or type /exit to cancel.",
			player,
			MessageStatus.NEUTRAL
		)
	}

	private fun descriptionLore(description: String): MutableList<Component?> {
		val lines = mutableListOf<Component?>()
		var currentLine = ""

		for (word in description.trim().split(Regex("\\s+"))) {
			if (currentLine.isEmpty()) {
				currentLine = word
			} else if (currentLine.length + word.length + 1 <= DESCRIPTION_LINE_LENGTH) {
				currentLine += " $word"
			} else {
				lines.add(Component.text(currentLine, NamedTextColor.GRAY))
				currentLine = word
			}
		}

		if (currentLine.isNotEmpty()) {
			lines.add(Component.text(currentLine, NamedTextColor.GRAY))
		}
		lines.add(Component.text("Click to select", NamedTextColor.DARK_GRAY))
		return lines
	}

	private companion object {
		const val GAMES_PER_ROW = 7
		const val DESCRIPTION_LINE_LENGTH = 32
	}
}
