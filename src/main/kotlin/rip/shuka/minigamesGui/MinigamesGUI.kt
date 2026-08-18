package rip.shuka.minigamesGui

import org.bukkit.plugin.java.JavaPlugin
import rip.shuka.minigamesGui.command.MinigameCommandExecutor
import rip.shuka.minigamesGui.command.MinigameCommandTabCompleter
import rip.shuka.minigamesGui.listener.InventoryListener
import rip.shuka.minigamesGui.listener.MinigameSelectionListener

class MinigamesGUI : JavaPlugin() {
	companion object {
		lateinit var instance: MinigamesGUI
			private set
	}

	override fun onEnable() {
		getCommand("1vs1")?.setExecutor(MinigameCommandExecutor())
		getCommand("1vs1")?.tabCompleter = MinigameCommandTabCompleter()
		server.pluginManager.registerEvents(InventoryListener(), this)
		server.pluginManager.registerEvents(MinigameSelectionListener(), this)

		instance = this
	}

	override fun onDisable() {
		// Plugin shutdown logic
	}
}
