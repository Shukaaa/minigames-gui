package rip.shuka.minigamesGui.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.menu.MinigameMenuInventory
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.message.MessageSender

class MinigameCommandExecutor : CommandExecutor {
	override fun onCommand(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>
	): Boolean {
		if (sender !is Player) {
			MessageSender.send("Only players can start minigames.", sender, MessageStatus.FAILURE)
			return true
		}

		sender.openInventory(MinigameMenuInventory().inventory)
		return true
	}
}