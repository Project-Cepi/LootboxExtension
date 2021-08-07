package world.cepi.crates.meta

import kotlinx.serialization.Serializable
import net.minestom.server.data.Data
import net.minestom.server.instance.block.Block
import world.cepi.kstom.command.arguments.generation.annotations.DefaultBlock

@Serializable
class BlockTypeMeta(
    @param:DefaultBlock(Block.CHEST)
    val block: Block = Block.CHEST
): LootCrateMeta() {

    override fun apply(data: Data) {
        data.set("block", block.blockId)
    }

}