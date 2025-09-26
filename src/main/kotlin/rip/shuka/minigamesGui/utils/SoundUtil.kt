package rip.shuka.minigamesGui.utils

import org.bukkit.entity.Player
import rip.shuka.minigamesGui.game.GamePlayer

object SoundUtil {
	fun playSound(soundName: String, vararg player: Player) {
		for (p in player) {
			p.playSound(p.location, soundName, 1.0f, 1.0f)
		}
	}

	fun playDefaultSelectSound(vararg player: GamePlayer) {
		playSound("minecraft:ui.button.click", *(player.map { it.player }).toTypedArray())
	}

	fun playDefaultSelectSound(vararg player: Player) {
		playSound("minecraft:ui.button.click", *player)
	}

	fun playQuitSound(vararg player: GamePlayer) {
		playSound("minecraft:entity.ender_dragon.flap", *(player.map { it.player }).toTypedArray())
	}

	fun playQuitSound(vararg player: Player) {
		playSound("minecraft:entity.ender_dragon.flap", *player)
	}

	fun playSuccessSound(vararg player: GamePlayer) {
		playSound("minecraft:entity.experience_orb.pickup", *(player.map { it.player }).toTypedArray())
	}

	fun playSuccessSound(vararg player: Player) {
		playSound("minecraft:entity.experience_orb.pickup", *player)
	}

	fun playErrorSound(vararg player: GamePlayer) {
		playSound("minecraft:block.note_block.bass", *(player.map { it.player }).toTypedArray())
	}

	fun playErrorSound(vararg player: Player) {
		playSound("minecraft:block.note_block.bass", *player)
	}

	fun playFailureSound(vararg player: GamePlayer) {
		playSound("minecraft:block.anvil.land", *(player.map { it.player }).toTypedArray())
	}

	fun playFailureSound(vararg player: Player) {
		playSound("minecraft:block.anvil.land", *player)
	}

	fun playWinningSound(vararg player: GamePlayer) {
		playSound("minecraft:entity.player.levelup", *(player.map { it.player }).toTypedArray())
	}

	fun playWinningSound(vararg player: Player) {
		playSound("minecraft:entity.player.levelup", *player)
	}
}