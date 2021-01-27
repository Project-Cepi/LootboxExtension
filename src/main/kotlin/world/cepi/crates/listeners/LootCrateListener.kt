package world.cepi.crates.listeners

import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import world.cepi.crates.getMaterialFromRegistryName
import world.cepi.crates.model.LootCrate

fun lootCrateListener(event: PlayerUseItemOnBlockEvent) {
    val instance = event.player.instance
    val block = instance?.getBlock(event.position)
    val blockData = instance?.getBlockData(event.position) ?: DataImpl()

    if (block != Block.BARREL || !blockData.hasKey("loot")) return

    val loot = blockData.get<LootCrate>("loot") ?: return

    loot.entries.forEach {
        val dropChance = (0..100).random()

        if (dropChance in (0..it.chance)) event.player.inventory.addItemStack(ItemStack(
            getMaterialFromRegistryName(it.namespace)!!,
            it.count.toByte()
        ))
    }

    val playerXp = event.player.exp + (loot.minXp..loot.maxXp).random()
    event.player.exp = playerXp
}