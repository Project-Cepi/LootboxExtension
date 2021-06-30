package world.cepi.crates.meta

import kotlinx.serialization.Serializable
import net.minestom.server.data.Data

@Serializable
class BreakTimeMeta(val ticks: Int): LootCrateMeta() {

    override fun apply(data: Data) {
        data.set("ticks", ticks)
    }
}