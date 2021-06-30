package world.cepi.crates.meta

import kotlinx.serialization.Serializable
import net.minestom.server.data.Data

@Serializable
sealed class LootCrateMeta {

    abstract fun apply(data: Data)

}