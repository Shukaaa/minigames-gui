package rip.shuka.minigamesGui.listener

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatTabCompleteEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import rip.shuka.minigamesGui.menu.MinigameSelection

class MinigameSelectionListener : Listener {
	@EventHandler
	fun onChat(event: AsyncChatEvent) {
		if (!MinigameSelection.isPending(event.player)) return

		event.isCancelled = true
		MinigameSelection.handleChat(
			event.player,
			PlainTextComponentSerializer.plainText().serialize(event.message())
		)
	}

	@EventHandler
	fun onChatTabComplete(event: PlayerChatTabCompleteEvent) {
		if (!MinigameSelection.isPending(event.player)) return

		val prefix = event.lastToken
		event.tabCompletions.clear()
		event.tabCompletions.addAll(
			event.player.server.onlinePlayers
				.asSequence()
				.filter { it != event.player && it.name.startsWith(prefix, ignoreCase = true) }
				.map { it.name }
				.sortedWith(String.CASE_INSENSITIVE_ORDER)
				.toList()
		)
	}

	@EventHandler
	fun onCommand(event: PlayerCommandPreprocessEvent) {
		if (!event.message.trim().equals("/exit", ignoreCase = true)) return
		if (!MinigameSelection.isPending(event.player)) return

		event.isCancelled = true
		MinigameSelection.cancel(event.player)
	}
}
