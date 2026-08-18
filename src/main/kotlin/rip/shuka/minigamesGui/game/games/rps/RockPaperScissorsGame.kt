package rip.shuka.minigamesGui.game.games.rps

import net.kyori.adventure.text.Component
import org.bukkit.Material
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
	override val icon = Material.SHEARS

	private val choices = mutableMapOf<Player, String>()

	override fun initialize() {
		openPlayerInventories()
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "choose") {
			val choosingPlayer = data[0] as Player
			val choice = data[1] as String

			if (player.none { it.player == choosingPlayer }) return
			if (choices.containsKey(choosingPlayer)) {
				SoundUtil.playErrorSound(choosingPlayer)
				MessageSender.send("You have already chosen. Wait for your opponent.", choosingPlayer, MessageStatus.FAILURE)
				return
			}

			choices[choosingPlayer] = choice

			if (choices.size == 2) {
				val p1 = player[0].player
				val p2 = player[1].player
				val c1 = choices[p1]!!
				val c2 = choices[p2]!!

				val result = getResult(c1, c2)
				when (result) {
					0 -> {
						this.endGameWithDraw("It's a draw! Both chose $c1.")
					}
					1 -> {
						this.endGameWithWinner(p1, "You win! $c1 beats $c2.", "You lose! $c1 beats $c2.")
					}
					2 -> {
						this.endGameWithWinner(p2, "You win! $c2 beats $c1.", "You lose! $c2 beats $c1.")
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
