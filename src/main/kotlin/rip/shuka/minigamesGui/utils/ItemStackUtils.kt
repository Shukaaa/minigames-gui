package rip.shuka.minigamesGui.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object ItemStacksUtil {
	fun formatMaterialName(material: Material): String {
		return material.name
			.lowercase()
			.split('_')
			.joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
	}

	fun createNamelessItemStack(material: Material): ItemStack {
		val stack = ItemStack(material)
		val meta = stack.itemMeta

		if (meta != null) {
			meta.displayName(Component.text(" "))
			stack.setItemMeta(meta)
		}

		return stack
	}

	fun createPlayerHead(playerName: String, displayName: String, color: NamedTextColor? = null): ItemStack {
		val skull = ItemStack(Material.PLAYER_HEAD, 1)
		val meta = skull.itemMeta as SkullMeta?

		if (meta != null) {
			val offlinePlayer = Bukkit.getOfflinePlayer(playerName)
			meta.owningPlayer = offlinePlayer
			meta.displayName(
				Component.text(displayName)
					.color(color ?: NamedTextColor.WHITE)
					.decoration(TextDecoration.ITALIC, false)
			)
			skull.setItemMeta(meta)
		}

		return skull
	}

	fun createBasicItemStack(material: Material, displayName: String, color: NamedTextColor?): ItemStack {
		val item = ItemStack(material)
		val meta = item.itemMeta

		if (meta != null) {
			meta.displayName(
				Component.text(displayName)
					.color(color)
					.decoration(TextDecoration.ITALIC, false)
			)
			item.setItemMeta(meta)
		}

		return item
	}

	fun createBasicItemStack(material: Material, title: Component?, description: MutableList<Component?>?): ItemStack {
		val item = ItemStack(material)
		val meta = item.itemMeta

		if (meta != null) {
			meta.displayName(title?.decoration(TextDecoration.ITALIC, false))
			meta.lore(description?.map { it?.decoration(TextDecoration.ITALIC, false) })
			item.setItemMeta(meta)
		}

		return item
	}
}