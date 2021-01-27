package world.cepi.lootbox.listeners

import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.block.Block

fun onBlockPlace(event: PlayerBlockPlaceEvent) {
    val block = event.player.instance?.getBlock(event.blockPosition)
    val data = event.player.getItemInHand(event.hand).data ?: DataImpl()

    if (block == Block.BARREL && data.hasKey("loot")) event.blockData = data
}