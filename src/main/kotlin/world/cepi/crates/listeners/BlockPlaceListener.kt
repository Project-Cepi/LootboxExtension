package world.cepi.crates.listeners

import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block
import world.cepi.crates.model.LootCrate

fun onBlockPlace(event: PlayerBlockPlaceEvent) {
    val block = event.player.instance?.getBlock(event.blockPosition)
    val data = event.player.getItemInHand(event.hand).data ?: DataImpl()

    if (block == Block.BARREL && data.hasKey(LootCrate.lootKey)) {
        event.blockData = data
        event.player.sendMessage("Copied data over to barrel")
    }
}