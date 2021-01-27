package world.cepi.crates

import kotlinx.serialization.json.Json
import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.extensions.Extension;
import net.minestom.server.item.Material
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.listeners.lootCrateListener
import world.cepi.crates.listeners.onBlockPlace
import world.cepi.crates.model.LootCrate
import java.io.File

class LootboxExtension : Extension() {

    override fun initialize() {
        logger.info("[CratesExtension] has been enabled!")
        val eventManager = MinecraftServer.getGlobalEventHandler()

        eventManager.addEventCallback(PlayerUseItemOnBlockEvent::class.java, ::lootCrateListener)
        eventManager.addEventCallback(PlayerBlockPlaceEvent::class.java, ::onBlockPlace)

        MinecraftServer.getCommandManager().register(LootcrateCommand())
    }

    override fun terminate() {
        logger.info("[ExampleExtension] has been disabled!")
        saveCrates()
    }
    companion object {
        private val lootboxesFile = File("./lootboxes/")
        val crates = loadCrates()

        private fun loadCrates(): MutableList<LootCrate> {
            val boxesList = mutableListOf<LootCrate>()
            if (lootboxesFile.listFiles() == null) lootboxesFile.mkdirs()
            lootboxesFile.listFiles()?.forEach { boxesList.add(Json.decodeFromString(LootCrate.serializer(), it.readText())) }

            return boxesList
        }

        private fun saveCrates() = crates.forEach {
            val crateFile = File(lootboxesFile,"${it.name}.json")
            crateFile.writeText(Json.encodeToString(LootCrate.serializer(), it))
        }
    }

}

fun getMaterialFromRegistryName(registryName: String): Material? {
    Material.values().forEach { if (it.name == registryName) return it}
    return null
}