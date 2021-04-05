package world.cepi.crates

import kotlinx.serialization.json.Json
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.extensions.Extension;
import net.minestom.server.item.Material
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.listeners.onBlockPlace
import world.cepi.crates.model.LootCrate
import world.cepi.crates.model.LootCrateBlock
import world.cepi.kstom.Manager
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import java.io.File

class LootboxExtension : Extension() {

    val playerInitialization: (Player) -> Unit = {
        it.addEventCallback(PlayerBlockPlaceEvent::class.java, ::onBlockPlace)
    }

    override fun initialize() {

        Manager.connection.addPlayerInitialization(playerInitialization)

        Manager.block.registerCustomBlock(LootCrateBlock)

        LootcrateCommand.register()

        logger.info("[CratesExtension] has been enabled!")
    }

    override fun terminate() {
        saveCrates()

        Manager.connection.removePlayerInitialization(playerInitialization)

        LootcrateCommand.unregister()

        logger.info("[CratesExtension] has been disabled!")
    }

    companion object {
        private val lootboxesFile = File("./lootboxes/")
        val crates: MutableList<LootCrate> = loadCrates()

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