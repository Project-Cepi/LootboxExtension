package world.cepi.lootbox.model

import kotlinx.serialization.Serializable
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.*

@Serializable
data class LootCrate(
    val name: String,
    val entries: MutableList<LootCrateEntry>,
    var minXp: Int = 0,
    var maxXp: Int = 0
) {
    @Serializable
    data class LootCrateEntry(
        val namespace: String,
        val count: Int,
        val chance: Int
    )
}
