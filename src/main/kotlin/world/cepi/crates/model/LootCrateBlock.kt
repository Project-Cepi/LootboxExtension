package world.cepi.crates.model

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.utils.time.UpdateOption
import org.apache.logging.log4j.util.Strings
import javax.naming.Name

object LootCrateBlock: CustomBlock(Block.CHEST, LootCrate.lootKey) {

    private val breakingMap: MutableMap<BlockPosition, Object2IntMap<Player>> = mutableMapOf()

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

        player.playSound(Sound.sound(
            SoundEvent.BLOCK_NOTE_BLOCK_PLING,
            Sound.Source.PLAYER,
            1f,
            .5f + (.15f * stage)
        ), position.x.toDouble(), position.y.toDouble(), position.z.toDouble())

        return 20
    }

    override fun onDestroy(instance: Instance, position: BlockPosition, data: Data?) {
        val loot = data?.get<LootCrate>(LootCrate.lootKey) ?: return

        breakingMap[position]?.keys?.forEach {

            it.playSound(Sound.sound(
                SoundEvent.BLOCK_NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                2f
            ), position.x.toDouble(), position.y.toDouble(), position.z.toDouble())

            if (loot.rewards.isNotEmpty())
                it.sendMessage(Component.text("Loot crate opened: ", NamedTextColor.GREEN))
        }

        loot.rewards.forEach { reward ->
            breakingMap[position]?.forEach {
                val message = reward.dispatch(it.key, loot, instance, position)
                if (PlainComponentSerializer.plain().serialize(message) != Strings.EMPTY) {
                    it.key.sendMessage(Component.text("-", NamedTextColor.DARK_GRAY)
                        .append(Component.space())
                        .append(message.color(NamedTextColor.GRAY)))
                }
            }
        }

        breakingMap.remove(position)
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