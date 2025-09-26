package rip.shuka.minigamesGui.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.GameFactory
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageSender.prefixTextComponent
import rip.shuka.minigamesGui.message.MessageStatus

class MinigameCommandExecutor : CommandExecutor {
	val minigames = GameFactory.getAvailableGames()

	override fun onCommand(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>
	): Boolean {
		if (args.isEmpty()) {
			MessageSender.send("Available minigames:", sender)
			for (game in minigames) {
				MessageSender.send("- ${game.name} (key: ${game.key})", sender)
			}
			return true
		}

		val gameKey = args[0]
		val game = minigames.find { it.key == gameKey }
		if (game == null) {
			MessageSender.send("Minigame with key '$gameKey' not found.", sender, MessageStatus.FAILURE)
			return true
		}

		if (sender !is Player) {
			MessageSender.send("Only players can start minigames.", sender, MessageStatus.FAILURE)
			return true
		}

		if (args.size < 2) {
			MessageSender.send("Type /minigame $gameKey <player> to invite a player to play.", sender, MessageStatus.NEUTRAL)
			MessageSender.send("Description: ${game.description}", sender, MessageStatus.NEUTRAL)
			return true
		}

		val player2 = Bukkit.getPlayer(args[1])
		if (player2 == null || !player2.isOnline) {
			MessageSender.send("Player '${args[1]}' not found or not online.", sender, MessageStatus.FAILURE)
			return true
		}

		val invitationMessage: TextComponent = prefixTextComponent
			.append(Component.text(sender.name + " has invited you to play " + game.name + ". "))
					.color(NamedTextColor.GRAY)
					.append(Component.text("[Click to accept]"))
					.clickEvent(
						ClickEvent.callback(
							{ event ->
								val gameInstance = GameFactory.createGame(gameKey)
								if (gameInstance == null) {
									MessageSender.send("Failed to create game instance for '$gameKey'.", sender, MessageStatus.FAILURE)
								}

								gameInstance?.init(listOf(sender, player2))
								MessageSender.send("Started minigame ${game.name}.", sender, MessageStatus.SUCCESS)
							}
						)
					)

		player2.sendMessage(invitationMessage)
		MessageSender.send("Invitation sent to ${player2.name}.", sender, MessageStatus.SUCCESS)
		return true
	}
}