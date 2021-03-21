package world.cepi.crates.model

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.data.DataImpl
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import world.cepi.crates.rewards.Reward

@Serializable
data class LootCrate(
    val name: String,
    val rewards: MutableList<Reward> = mutableListOf()
) {

    fun toItem(): ItemStack {
        val barrel = ItemStack(Material.CHEST, 1)
        val barrelData = DataImpl()
        barrelData.set(lootKey, this)
        barrel.data = barrelData
        barrel.displayName = Component.text("Loot Crate", NamedTextColor.GOLD)
        return barrel
    }

    companion object {
        const val lootKey = "cepi-lootcrate"
        const val blockID = 18372.toShort()
    }
}