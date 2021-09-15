package world.cepi.crates.meta

import kotlinx.serialization.Serializable
import net.minestom.server.instance.block.Block

@Serializable
sealed class LootCrateMeta {

    abstract fun apply(block: Block): Block

}