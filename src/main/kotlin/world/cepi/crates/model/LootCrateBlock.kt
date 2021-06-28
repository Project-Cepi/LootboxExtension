package world.cepi.crates.model

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.CustomBlock
import net.minestom.server.particle.Particle
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.utils.time.UpdateOption
import org.apache.logging.log4j.util.Strings
import world.cepi.kstom.Manager
import world.cepi.kstom.util.component1
import world.cepi.kstom.util.component2
import world.cepi.kstom.util.component3

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
            SoundEvent.NOTE_BLOCK_PLING,
            Sound.Source.PLAYER,
            1f,
            .5f + (.15f * stage)
        ), position.x.toDouble(), position.y.toDouble(), position.z.toDouble())

        return 20
    }

    override fun onDestroy(instance: Instance, position: BlockPosition, data: Data?) {
        val loot = data?.get<LootCrate>(LootCrate.lootKey) ?: return

        breakingMap[position]?.keys?.forEach {

            val (x, y, z) = position

            it.playSound(Sound.sound(
                SoundEvent.NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                2f
            ), x.toDouble(), y.toDouble(), z.toDouble())

            if (loot.rewards.isNotEmpty())
                it.sendMessage(Component.text("Loot crate opened: ", NamedTextColor.GREEN))
        }

        loot.rewards.forEach { reward ->
            breakingMap[position]?.forEach {
                val message = try {
                    reward.dispatch(it.key, loot, instance, position)
                } catch (exception: Exception) {
                    Manager.exception.handleException(exception)
                    Component.text("An internal error occured", NamedTextColor.RED)
                }

                if (message != Component.empty()) {
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

        // TODO cool particle at top
//        if (data!!.get<Int>("anim") ?: 0 == 0) {
//            data.set("anim", 10)
//        } else
//            data.set("anim", data.get<Int>("anim")!! - 0)

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