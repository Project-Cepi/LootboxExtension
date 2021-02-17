package world.cepi.crates.listeners

import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.item.Material
import world.cepi.crates.model.LootCrate

fun onBlockPlace(event: PlayerBlockPlaceEvent) {

    val item = event.player.getItemInHand(event.hand)

    val data = item.data ?: DataImpl()

    if (item.material == Material.CHEST && data.hasKey(LootCrate.lootKey)) {
        event.setCustomBlock(LootCrate.lootKey)
        event.blockData = data

        event.player.sendMessage("Loot crate placed!")
    }
}