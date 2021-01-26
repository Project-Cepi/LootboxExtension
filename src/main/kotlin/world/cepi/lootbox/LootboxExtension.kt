package world.cepi.lootbox

import kotlinx.serialization.json.Json
import net.minestom.server.extensions.Extension;
import world.cepi.lootbox.model.LootCrate
import java.io.File

class LootboxExtension : Extension() {

    override fun initialize() {
        logger.info("[ExampleExtension] has been enabled!")
    }

    override fun terminate() {
        logger.info("[ExampleExtension] has been disabled!")
    }
    companion object {
        private val lootboxesFile = File("./lootboxes/")

        val crates: Array<LootCrate>
        get() {
            val boxesList = mutableListOf<LootCrate>()
            if (lootboxesFile.listFiles() == null) lootboxesFile.mkdirs()
            lootboxesFile.listFiles()?.forEach { boxesList.add(Json.decodeFromString(LootCrate.serializer(), it.readText())) }

            return boxesList.toTypedArray()
        }
    }

}