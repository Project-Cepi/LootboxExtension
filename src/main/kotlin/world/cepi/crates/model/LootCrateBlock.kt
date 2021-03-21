package world.cepi.crates.model

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import net.minestom.server.sound.Sound
import net.minestom.server.sound.SoundCategory
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
    ): Int {

        player.playSound(
            Sound.BLOCK_NOTE_BLOCK_PLING,
            SoundCategory.PLAYERS,
            player.position.toBlockPosition().x,
            player.position.toBlockPosition().y,
            player.position.toBlockPosition().z,
            1f,
            .5f + (.15f * stage)
        )

        return 20
    }

    override fun onDestroy(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        val loot = data?.get<LootCrate>(LootCrate.lootKey) ?: return

        breakingMap[blockPosition]?.keys?.forEach {
            it.playSound(
                Sound.BLOCK_NOTE_BLOCK_PLING,
                SoundCategory.PLAYERS,
                it.position.toBlockPosition().x,
                it.position.toBlockPosition().y,
                it.position.toBlockPosition().z,
                1f,
                2f
            )
            it.sendMessage(Component.text("Loot crate opened: "))
        }

        loot.rewards.forEach { reward ->
            breakingMap[blockPosition]?.forEach {
                val message = reward.dispatch(it.key, loot, instance, blockPosition)
                if (PlainComponentSerializer.plain().serialize(message) != "") it.key.sendMessage(message)
            }
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