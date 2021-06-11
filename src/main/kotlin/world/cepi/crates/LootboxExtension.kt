package world.cepi.crates

import net.minestom.server.entity.Player
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.extensions.Extension
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.listeners.onBlockPlace
import world.cepi.crates.model.LootCrate
import world.cepi.crates.model.LootCrateBlock
import world.cepi.kstom.Manager
import world.cepi.kstom.command.register
import world.cepi.kstom.command.unregister
import world.cepi.kstom.event.listenOnly
import java.io.File

class LootboxExtension : Extension() {

    override fun initialize() {

        eventNode.listenOnly(::onBlockPlace)

        Manager.block.registerCustomBlock(LootCrateBlock)

        LootcrateCommand.register()

        logger.info("[CratesExtension] has been enabled!")
    }

    override fun terminate() {

        LootcrateCommand.unregister()

        logger.info("[CratesExtension] has been disabled!")
    }

    companion object {
        val crates: MutableList<LootCrate> = mutableListOf()
    }

}