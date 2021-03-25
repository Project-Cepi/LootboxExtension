package world.cepi.crates.model

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.data.DataImpl
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import world.cepi.crates.rewards.Reward
import world.cepi.kstom.adventure.asMini

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
        barrel.displayName = "<gradient:yellow:gold>Loot Crate".asMini().decoration(TextDecoration.ITALIC, false)
        barrel.lore = arrayListOf<Component>(
            Component.space(),
            Component.text("ID: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                .append(Component.text(name, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
        )
        return barrel
    }

    companion object {
        const val lootKey = "cepi-lootcrate"
        const val blockID = 18372.toShort()
    }
}