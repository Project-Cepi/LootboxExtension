package world.cepi.crates.listeners

import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import world.cepi.crates.LootboxExtension
import world.cepi.crates.getMaterialFromRegistryName
import world.cepi.crates.model.LootCrate

fun lootCrateListener(event: PlayerUseItemOnBlockEvent) {
    event.player.sendMessage("Called PlayerUseBlockItemEvent")

    val instance = event.player.instance
    val blockData = instance?.getBlockData(event.position) ?: DataImpl()

    val loot = blockData.get<LootCrate>(LootCrate.lootKey) ?: return

    loot.rewards.forEach {
        it.dispatch(event.player)
    }
}