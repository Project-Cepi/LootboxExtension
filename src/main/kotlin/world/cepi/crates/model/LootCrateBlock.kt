package world.cepi.crates.model

import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.utils.time.UpdateOption

class LootCrateBlock: CustomBlock(Block.BARREL, LootCrate.lootKey) {
    override fun onPlace(instance: Instance, blockPosition: BlockPosition, data: Data?) {

    }

    override fun enableMultiPlayerBreaking() = true

    override fun enableCustomBreakDelay() = true

    override fun getBreakDelay(
        player: Player,
        position: BlockPosition,
        stage: Byte,
        breakers: MutableSet<Player>?
    ) = 20

    override fun onDestroy(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        val loot = data?.get<LootCrate>(LootCrate.lootKey) ?: return

        loot.rewards.forEach { reward ->
            getBreakers(instance, blockPosition)?.forEach { reward.dispatch(it) }
        }
    }

    override fun onInteract(player: Player, hand: Player.Hand, blockPosition: BlockPosition, data: Data?): Boolean {
        return false
    }

    override fun getCustomBlockId(): Short {
        return LootCrate.blockID
    }

    override fun getUpdateOption(): UpdateOption {
        return UpdateOption(1, TimeUnit.SECOND)
    }

    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        // TODO particle above lootcrate
    }
}