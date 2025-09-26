package rip.shuka.minigamesGui

import org.bukkit.plugin.java.JavaPlugin
import rip.shuka.minigamesGui.command.MinigameCommandExecutor
import rip.shuka.minigamesGui.command.MinigameCommandTabCompleter
import rip.shuka.minigamesGui.listener.InventoryListener

class MinigamesGUI : JavaPlugin() {
	companion object {
		lateinit var instance: MinigamesGUI
			private set
	}

	override fun onEnable() {
		getCommand("minigame")?.setExecutor(MinigameCommandExecutor())
		getCommand("minigame")?.tabCompleter = MinigameCommandTabCompleter()
		server.pluginManager.registerEvents(InventoryListener(), this)

		instance = this
	}

	override fun onDisable() {
		// Plugin shutdown logic
	}
}
