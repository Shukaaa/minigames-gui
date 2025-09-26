package rip.shuka.minigamesGui.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import rip.shuka.minigamesGui.game.GameFactory

class MinigameCommandTabCompleter : TabCompleter {
	val minigames = GameFactory.getAvailableGames()

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>
	): List<String?> {
		if (args.size == 1) {
			val partial = args[0].lowercase()
			return minigames.map { it.key }.filter { it.startsWith(partial) }
		}

		if (args.size == 2) {
			val partial = args[1].lowercase()
			return sender.server.onlinePlayers.map { it.name }.filter { it.lowercase().startsWith(partial) && it != sender.name }
		}

		return emptyList()
	}
}