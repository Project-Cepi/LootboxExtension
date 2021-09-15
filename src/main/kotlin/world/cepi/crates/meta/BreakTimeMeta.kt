package world.cepi.crates.meta

import kotlinx.serialization.Serializable
import net.minestom.server.instance.block.Block
import world.cepi.kstom.item.with

@Serializable
class BreakTimeMeta(val ticks: Int): LootCrateMeta() {

    override fun apply(block: Block) = block.with("ticks", item = ticks)

}