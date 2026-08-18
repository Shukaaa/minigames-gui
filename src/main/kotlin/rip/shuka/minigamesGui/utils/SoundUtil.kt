package rip.shuka.minigamesGui.utils

import org.bukkit.entity.Player

object SoundUtil {
	fun playSound(soundName: String, vararg player: Player) {
		for (p in player) {
			p.playSound(p.location, soundName, 1.0f, 1.0f)
		}
	}

	fun playDefaultSelectSound(vararg player: Player) {
		playSound("minecraft:ui.button.click", *player)
	}

	fun playQuitSound(vararg player: Player) {
		playSound("minecraft:entity.ender_dragon.flap", *player)
	}

	fun playSuccessSound(vararg player: Player) {
		playSound("minecraft:entity.experience_orb.pickup", *player)
	}

	fun playErrorSound(vararg player: Player) {
		playSound("minecraft:block.note_block.bass", *player)
	}

	fun playFailureSound(vararg player: Player) {
		playSound("minecraft:block.anvil.land", *player)
	}

	fun playWinningSound(vararg player: Player) {
		playSound("minecraft:entity.player.levelup", *player)
	}
}