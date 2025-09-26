package rip.shuka.minigamesGui.game.games.rps

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.SoundUtil

class RockPaperScissorsGame : Game({ RockPaperScissorsInventory() }) {
	override val key = "rockpaperscissors"
	override val name = "Rock Paper Scissors"
	override val title = Component.text("Rock Paper Scissors")
	override val description = "Choose Rock, Paper or Scissors. The winner is decided after both players have chosen."

	private val choices = mutableMapOf<Player, String>()

	override fun initialize() {
		player.forEach { p ->
			p.player.openInventory(p.inventoryHolder.inventory)
		}
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "quit") {
			val quittingPlayer = data[0] as Player
			SoundUtil.playQuitSound(*player.toTypedArray())
			player.forEach { p ->
				p.player.closeInventory()
				if (p.player != quittingPlayer) {
					MessageSender.send("${quittingPlayer.name} has quit the game.", p.player, MessageStatus.FAILURE)
				} else {
					MessageSender.send("You have quit the game.", p.player, MessageStatus.NEUTRAL)
				}
			}
			return
		}

		if (eventId == "choose") {
			val choosingPlayer = data[0] as Player
			val choice = data[1] as String
			choices[choosingPlayer] = choice

			if (choices.size == 2) {
				val p1 = player[0].player
				val p2 = player[1].player
				val c1 = choices[p1]!!
				val c2 = choices[p2]!!

				val result = getResult(c1, c2)
				player.forEach { p ->
					p.player.closeInventory()
				}
				when (result) {
					0 -> {
						SoundUtil.playQuitSound(*player.toTypedArray())
						MessageSender.send("It's a draw! Both chose $c1.", p1, MessageStatus.NEUTRAL)
						MessageSender.send("It's a draw! Both chose $c2.", p2, MessageStatus.NEUTRAL)
					}
					1 -> {
						SoundUtil.playWinningSound(player[0])
						SoundUtil.playFailureSound(player[1])
						MessageSender.send("You win! $c1 beats $c2.", p1, MessageStatus.SUCCESS)
						MessageSender.send("You lose! $c1 beats $c2.", p2, MessageStatus.FAILURE)
					}
					2 -> {
						SoundUtil.playWinningSound(player[1])
						SoundUtil.playFailureSound(player[0])
						MessageSender.send("You lose! $c2 beats $c1.", p1, MessageStatus.FAILURE)
						MessageSender.send("You win! $c2 beats $c1.", p2, MessageStatus.SUCCESS)
					}
				}
			} else {
				MessageSender.send("Waiting for your opponent to choose...", choosingPlayer, MessageStatus.NEUTRAL)
			}
		}
	}

	private fun getResult(c1: String, c2: String): Int {
		if (c1 == c2) return 0
		return when (c1) {
			"Rock" -> if (c2 == "Scissors") 1 else 2
			"Paper" -> if (c2 == "Rock") 1 else 2
			"Scissors" -> if (c2 == "Paper") 1 else 2
			else -> 0
		}
	}
}
