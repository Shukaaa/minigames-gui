package rip.shuka.minigamesGui.message

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MessageSender {
	private const val PREFIX = "§9§lMINIGAMES §7| "

	fun send(message: String, player: Player, status: MessageStatus = MessageStatus.NEUTRAL) {
		player.sendMessage(getMessages(message, status))
	}

	fun send(message: String, sender: CommandSender, status: MessageStatus = MessageStatus.NEUTRAL) {
		sender.sendMessage(getMessages(message, status))
	}

	val prefixTextComponent: TextComponent
		get() = Component.text(PREFIX)

	private fun getMessages(message: String, status: MessageStatus): String {
		var message = message
		if (status === MessageStatus.SUCCESS) {
			message = "§a$message"
		} else if (status === MessageStatus.FAILURE) {
			message = "§c$message"
		} else {
			message = "§7$message"
		}

		return PREFIX + message
	}
}