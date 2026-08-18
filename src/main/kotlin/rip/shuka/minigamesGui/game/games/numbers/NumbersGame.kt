package rip.shuka.minigamesGui.game.games.numbers

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.Game
import rip.shuka.minigamesGui.message.MessageSender
import rip.shuka.minigamesGui.message.MessageStatus
import rip.shuka.minigamesGui.utils.SoundUtil

class NumbersGame : Game({ NumbersInventory() }) {
	override val key: String = "numbers"
	override val name: String = "Numbers Game"
	override val title: Component = Component.text("Numbers Game")
	override val description: String =
		"Both players pick a number between 1 and 9. The player who picks the higher number wins. " +
		"But every player can only pick each number once! (USe Voice chat for improved experience)"

	var scores = mutableMapOf<Player, Int>()
	var choicesSubmitted = mutableMapOf<Player, Int>()
	var alreadySelectedNumbers = mutableMapOf<Player, List<Int>>()

	override fun initialize() {
		openPlayerInventories()
		scores[player[0].player] = 0
		scores[player[1].player] = 0
	}

	override fun receiveEvent(eventId: String, vararg data: Any) {
		if (eventId == "choose") {
			val choosingPlayer = data[0] as Player
			val choice = data[1] as Int

			if (choicesSubmitted.containsKey(choosingPlayer)) {
				MessageSender.send("You have already submitted your choice! Wait for the other player.", choosingPlayer, MessageStatus.FAILURE)
				SoundUtil.playErrorSound(choosingPlayer)
				return
			}

			if (alreadySelectedNumbers.getOrDefault(choosingPlayer, emptyList()).contains(choice)) {
				MessageSender.send("You have already selected this number! Choose a different one.", choosingPlayer, MessageStatus.FAILURE)
				SoundUtil.playErrorSound(choosingPlayer)
				return
			}

			alreadySelectedNumbers[choosingPlayer] = alreadySelectedNumbers.getOrDefault(choosingPlayer, emptyList()) + choice
			choicesSubmitted[choosingPlayer] = choice
			MessageSender.send("You have chosen $choice.", choosingPlayer, MessageStatus.SUCCESS)
			SoundUtil.playDefaultSelectSound(choosingPlayer)

			if (choicesSubmitted.size == 2) {
				val p1 = player[0].player
				val p2 = player[1].player
				val c1 = choicesSubmitted[p1]!!
				val c2 = choicesSubmitted[p2]!!

				when {
					c1 > c2 -> {
						scores[p1] = scores.getOrDefault(p1, 0) + 1
						MessageSender.send("You win this round! $c1 beats $c2. Your score: ${scores[p1]}", p1, MessageStatus.SUCCESS)
						MessageSender.send("You lose this round! $c1 beats $c2. Your score: ${scores[p2]}", p2, MessageStatus.FAILURE)
					}
					c2 > c1 -> {
						scores[p2] = scores.getOrDefault(p2, 0) + 1
						MessageSender.send("You win this round! $c2 beats $c1. Your score: ${scores[p2]}", p2, MessageStatus.SUCCESS)
						MessageSender.send("You lose this round! $c2 beats $c1. Your score: ${scores[p1]}", p1, MessageStatus.FAILURE)
					}
					else -> {
						MessageSender.send("It's a draw! Both chose $c1.", p1, MessageStatus.NEUTRAL)
						MessageSender.send("It's a draw! Both chose $c1.", p2, MessageStatus.NEUTRAL)
					}
				}

				choicesSubmitted.clear()

				forEachPlayerInventory<NumbersInventory> { gamePlayer, inventory ->
					inventory.updateAlreadySelectedNumbers(alreadySelectedNumbers[gamePlayer.player] ?: emptyList())
					inventory.updateScores(gamePlayer.player, scores)
				}
			} else {
				MessageSender.send("Waiting for your opponent to choose...", choosingPlayer, MessageStatus.NEUTRAL)
			}

			val opponentPlayer = player.first { it.player != choosingPlayer }.player
			if (alreadySelectedNumbers[choosingPlayer]?.size == 9 && alreadySelectedNumbers[opponentPlayer]?.size == 9) {
				val opponentScore = scores.getOrDefault(opponentPlayer, 0)
				val choosingPlayerScore = scores.getOrDefault(choosingPlayer, 0)

				if (choosingPlayerScore > opponentScore) {
					this.endGameWithWinner(choosingPlayer, "You win the game! Final score: $choosingPlayerScore to $opponentScore", "You lose the game! Final score: $choosingPlayerScore to $opponentScore")
				} else if (opponentScore > choosingPlayerScore) {
					this.endGameWithWinner(opponentPlayer, "You win the game! Final score: $opponentScore to $choosingPlayerScore", "You lose the game! Final score: $opponentScore to $choosingPlayerScore")
				} else {
					this.endGameWithDraw("The game ends in a draw! Final score: $choosingPlayerScore to $opponentScore")
				}
			}
		}
	}
}