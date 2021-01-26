package world.cepi.lootbox

import net.minestom.server.extensions.Extension;

class LootboxExtension : Extension() {

    override fun initialize() {
        logger.info("[ExampleExtension] has been enabled!")
    }

    override fun terminate() {
        logger.info("[ExampleExtension] has been disabled!")
    }

}