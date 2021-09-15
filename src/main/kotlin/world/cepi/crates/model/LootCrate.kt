package world.cepi.crates.model

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.data.Data
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import world.cepi.crates.meta.LootCrateMeta
import world.cepi.crates.rewards.Reward
import world.cepi.kstom.adventure.asMini

@Serializable
data class LootCrate(
    val name: String,
    val rewards: MutableList<Reward> = mutableListOf(),
    private val lootCrateMeta: MutableList<LootCrateMeta> = mutableListOf()
) {
    /**
     * returns if the meta already existed the old one
     */
    @Suppress("unchecked_cast")
    fun <T : LootCrateMeta> putMeta(meta: T): T? {
        val previous = lootCrateMeta.firstOrNull { it::class == meta::class }
        if (previous == null) {
            lootCrateMeta.add(meta)
            return null
        }
        lootCrateMeta[lootCrateMeta.indexOf(previous)] = meta
        return previous as T
    }

    fun applyMeta(block: Block): Block = lootCrateMeta.fold(block) { foldedBlock, meta -> meta.apply(foldedBlock) }

    fun toItem(): ItemStack = ItemStack.builder(Material.CHEST)
        .meta { meta ->
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
        .build()


    companion object {
        const val lootKey = "cepi-lootcrate"
        const val blockID = 1837.toShort()
    }
}