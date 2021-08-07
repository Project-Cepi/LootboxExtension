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
import net.minestom.server.particle.ParticleCreator
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.utils.time.UpdateOption
import org.apache.logging.log4j.util.Strings
import world.cepi.kstom.Manager
import world.cepi.kstom.util.component1
import world.cepi.kstom.util.component2
import world.cepi.kstom.util.component3
import java.time.Duration

object LootCrateBlock : CustomBlock(Block.CHEST, LootCrate.lootKey) {
    private val breakingMap: MutableMap<BlockPosition, Object2IntMap<Player>> = mutableMapOf()

    override fun onPlace(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        if (data == null) return
        if (data.hasKey("block")) instance.refreshBlockStateId(blockPosition, data.get<Short>("block")!!)
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
            noteBlockPitches[stage.toInt()]
        ), position.x.toDouble(), position.y.toDouble(), position.z.toDouble())

        val data = player.instance?.getBlockData(position)
        return if (data?.hasKey("ticks") == true)
            data.get<Int>("ticks")!!
        else 20
    }

    override fun onDestroy(instance: Instance, position: BlockPosition, data: Data?) {
        val particleX = position.x + .5
        val particleY = position.y + .5
        val particleZ = position.z + .5
        val flashParticle = ParticleCreator.createParticlePacket(Particle.FLASH, particleX, particleY, particleZ, .5f, .5f, .5f, 1)
        val explosionParticle = ParticleCreator.createParticlePacket(Particle.EXPLOSION, particleX, particleY, particleZ, .5f, .5f, .5f, 1)
        val cloudParticle = ParticleCreator.createParticlePacket(Particle.CLOUD, particleX, particleY, particleZ, .5f, .5f, .5f, 10)

        instance.players.forEach {
            if (position.getDistanceSquared(it.position.toBlockPosition()) <= 100) {
                it.playerConnection.sendPacket(flashParticle)
                it.playerConnection.sendPacket(explosionParticle)
                it.playerConnection.sendPacket(cloudParticle)
            }
        }

        instance.playSound(Sound.sound(SoundEvent.FIREWORK_ROCKET_BLAST, Sound.Source.MASTER, .8f, 1f), particleX, particleY, particleZ)
        instance.playSound(Sound.sound(SoundEvent.FIREWORK_ROCKET_TWINKLE, Sound.Source.MASTER, 1f, 1f), particleX, particleY, particleZ)
        instance.playSound(Sound.sound(SoundEvent.GENERIC_EXPLODE, Sound.Source.MASTER, .3f, 1f), particleX, particleY, particleZ)


        val loot = data?.get<LootCrate>(LootCrate.lootKey) ?: return
        println(breakingMap)

        breakingMap[position]?.keys?.forEach {
            val (x, y, z) = position

            it.playSound(Sound.sound(
                SoundEvent.NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                1.41f // g"
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

    override fun getUpdateFrequency(): Duration? {
        return Duration.of(1, TimeUnit.SERVER_TICK)
    }

    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {

        // TODO cool particle at top
//        if (data!!.get<Int>("anim") ?: 0 == 0) {
//            data.set("anim", 10)
//        } else
//            data.set("anim", data.get<Int>("anim")!! - 0)
        val particle = ParticleCreator.createParticlePacket(Particle.CRIT, blockPosition.x + .5, blockPosition.y + .5, blockPosition.z + .5, .5f, .5f, .5f, 10)
        instance.players.forEach {
            if (blockPosition.getDistanceSquared(it.position.toBlockPosition()) <= 100)
                it.playerConnection.sendPacket(particle)
        }

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
            internalMap[player] = data?.get<Int>("ticks") ?: 20
        }
    }

    // Notes g' -> e"
    // 0.5*2^(<Note Block clicks>/12)
    // source: https://www.reddit.com/r/Minecraft/comments/1hazq2/table_and_formula_to_convert_note_block_clicks/
    private val noteBlockPitches = floatArrayOf(0.71f, 0.75f, 0.79f, 0.84f, 0.89f, 0.94f, 1.0f, 1.06f, 1.12f, 1.19f)
}