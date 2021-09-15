package world.cepi.crates.meta

import kotlinx.serialization.Serializable
import net.minestom.server.data.Data
import net.minestom.server.instance.block.Block
import world.cepi.kstom.command.arguments.generation.annotations.DefaultBlock
import world.cepi.kstom.item.with

@Serializable
class BlockTypeMeta(
    @param:DefaultBlock("minecraft:chest")
    val block: Block = Block.CHEST
): LootCrateMeta() {

    override fun apply(block: Block) = block.with("block", item = block)

}