package rip.shuka.minigamesGui.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.MinigamesGUI
import rip.shuka.minigamesGui.game.GameFactory
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageSender.prefixTextComponent
import rip.shuka.minigamesGui.message.MessageStatus

object MinigameSelection {
	private val pendingGames = ConcurrentHashMap<UUID, String>()
	private val pendingInvitations = ConcurrentHashMap<UUID, Invitation>()

	fun begin(player: Player, gameKey: String) {
		pendingGames[player.uniqueId] = gameKey
	}

	fun isPending(player: Player): Boolean = pendingGames.containsKey(player.uniqueId)

	fun handleChat(player: Player, input: String) {
		val gameKey = pendingGames[player.uniqueId] ?: return
		val targetName = input.trim()

		Bukkit.getScheduler().runTask(MinigamesGUI.instance, Runnable {
			if (targetName.equals("/exit", ignoreCase = true)) {
				pendingGames.remove(player.uniqueId)
				MessageSender.send("Game selection cancelled.", player, MessageStatus.NEUTRAL)
				return@Runnable
			}

			val target = Bukkit.getPlayerExact(targetName)
			if (target == null || !target.isOnline) {
				MessageSender.send("Player '$targetName' not found or not online. Try again or type /exit.", player, MessageStatus.FAILURE)
				return@Runnable
			}
			if (target == player) {
				MessageSender.send("You cannot invite yourself. Try another player or type /exit.", player, MessageStatus.FAILURE)
				return@Runnable
			}

			val game = GameFactory.getAvailableGames().find { it.key == gameKey }
			if (game == null) {
				pendingGames.remove(player.uniqueId)
				MessageSender.send("This game is no longer available.", player, MessageStatus.FAILURE)
				return@Runnable
			}

			pendingGames.remove(player.uniqueId)
			sendInvitation(player, target, game.key, game.name)
		})
	}

	fun cancel(player: Player) {
		if (pendingGames.remove(player.uniqueId) != null) {
			MessageSender.send("Game selection cancelled.", player, MessageStatus.NEUTRAL)
		}
	}

	private fun sendInvitation(inviter: Player, target: Player, gameKey: String, gameName: String) {
		pendingInvitations[target.uniqueId] = Invitation(inviter, gameKey)

		val invitation = prefixTextComponent
			.append(
				Component.text(
					"${inviter.name} invited you to play $gameName. ",
					NamedTextColor.GRAY
				)
			)
			.append(
				Component.text("[Click here to accept]", NamedTextColor.GREEN)
					.decoration(TextDecoration.BOLD, true)
					.clickEvent(ClickEvent.callback { acceptInvitation(target) })
			)

		target.sendMessage(invitation)
		MessageSender.send("Invitation sent to ${target.name}.", inviter, MessageStatus.SUCCESS)
	}

	private fun acceptInvitation(target: Player) {
		val invitation = pendingInvitations.remove(target.uniqueId) ?: run {
			MessageSender.send("This invitation is no longer available.", target, MessageStatus.FAILURE)
			return
		}

		if (!invitation.inviter.isOnline) {
			MessageSender.send("The inviter is no longer online.", target, MessageStatus.FAILURE)
			return
		}

		val game = GameFactory.createGame(invitation.gameKey)
		if (game == null) {
			MessageSender.send("This game is no longer available.", target, MessageStatus.FAILURE)
			return
		}

		game.init(listOf(invitation.inviter, target))
		MessageSender.send("Started minigame ${game.name}.", invitation.inviter, MessageStatus.SUCCESS)
		MessageSender.send("You accepted the invitation to play ${game.name}.", target, MessageStatus.SUCCESS)
	}

	private data class Invitation(
		val inviter: Player,
		val gameKey: String
	)
}
