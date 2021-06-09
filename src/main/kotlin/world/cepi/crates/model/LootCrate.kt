package world.cepi.crates.model

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import world.cepi.crates.rewards.Reward
import world.cepi.kstom.adventure.asMini

@Serializable
data class LootCrate(
    val name: String,
    val rewards: MutableList<Reward> = mutableListOf()
) {

    fun toItem(): ItemStack = ItemStack.of(Material.CHEST).with {

        it.meta { meta ->

            meta.displayName("<gradient:yellow:gold>Loot Crate".asMini().decoration(TextDecoration.ITALIC, false))

            meta.lore(
                arrayListOf<Component>(
                    Component.space(),
                    Component.text("ID: ", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(name, NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false))
                )
            )

            meta.set(Tag.String(lootKey), name)
        }
    }


    companion object {
        const val lootKey = "cepi-lootcrate"
        const val blockID = 1837.toShort()
    }
}