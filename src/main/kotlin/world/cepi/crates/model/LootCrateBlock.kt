package world.cepi.crates.model

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.utils.time.UpdateOption

class LootCrateBlock: CustomBlock(Block.CHEST, LootCrate.lootKey) {

    val breakingMap: MutableMap<BlockPosition, Object2IntMap<Player>> = mutableMapOf()

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
            breakingMap[blockPosition]?.forEach { reward.dispatch(it.key) }
        }

        breakingMap.remove(blockPosition)
    }

    override fun onInteract(player: Player, hand: Player.Hand, blockPosition: BlockPosition, data: Data?): Boolean {
        return false
    }

    override fun getCustomBlockId(): Short {
        return LootCrate.blockID
    }

    override fun getUpdateOption(): UpdateOption {
        return UpdateOption(1, TimeUnit.TICK)
    }

    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        if (breakingMap[blockPosition] == null) breakingMap[blockPosition] = Object2IntOpenHashMap()
        val internalMap = breakingMap[blockPosition]!!

        breakingMap[blockPosition]!!.forEach {
            if (it.value - 1 <= 0) {
                breakingMap[blockPosition]?.removeInt(it.key)
                return@forEach
            }

            breakingMap[blockPosition]?.set(it.key, it.value - 1)
        }

        getBreakers(instance, blockPosition)?.forEach { player ->
            internalMap[player] = 20
        }
    }
}