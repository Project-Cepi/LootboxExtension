package world.cepi.crates.model

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.instance.block.BlockHandler
import net.minestom.server.particle.Particle
import net.minestom.server.particle.ParticleCreator
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.NamespaceID
import world.cepi.kstom.Manager
import world.cepi.kstom.item.get
import world.cepi.kstom.serializer.UUIDSerializer
import world.cepi.kstom.util.playSound

object LootCrateBlock : BlockHandler {

    val mapSerializer = MapSerializer(UUIDSerializer, Long.serializer())

//    private val breakingMap: MutableMap<BlockPosition, Object2IntMap<Player>> = mutableMapOf()
//
//    override fun enableMultiPlayerBreaking() = true
//
//    override fun enableCustomBreakDelay() = true
//
//    override fun getBreakDelay(
//        player: Player,
//        position: BlockPosition,
//        stage: Byte,
//        breakers: MutableSet<Player>?
//    ): Int {
//        player.playSound(Sound.sound(
//            SoundEvent.BLOCK_NOTE_BLOCK_PLING,
//            Sound.Source.PLAYER,
//            1f,
//            noteBlockPitches[stage.toInt()]
//        ), position.x.toDouble(), position.y.toDouble(), position.z.toDouble())
//
//        val data = player.instance?.getBlockData(position)
//        return if (data?.hasKey("ticks") == true)
//            data.get<Int>("ticks")!!
//        else 20
//    }

    override fun onDestroy(destroy: BlockHandler.Destroy): Unit = with(destroy) {
        val particleX = blockPosition.x() + .5
        val particleY = blockPosition.y() + .5
        val particleZ = blockPosition.z() + .5
        val flashParticle = ParticleCreator.createParticlePacket(Particle.FLASH, particleX, particleY, particleZ, .5f, .5f, .5f, 1)
        val explosionParticle = ParticleCreator.createParticlePacket(Particle.EXPLOSION, particleX, particleY, particleZ, .5f, .5f, .5f, 1)
        val cloudParticle = ParticleCreator.createParticlePacket(Particle.CLOUD, particleX, particleY, particleZ, .5f, .5f, .5f, 10)

        instance.getChunkAt(blockPosition)?.viewers?.forEach {
            it.playerConnection.sendPacket(flashParticle)
            it.playerConnection.sendPacket(explosionParticle)
            it.playerConnection.sendPacket(cloudParticle)
        }

        instance.playSound(Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_BLAST, Sound.Source.MASTER, .8f, 1f), particleX, particleY, particleZ)
        instance.playSound(Sound.sound(SoundEvent.ENTITY_FIREWORK_ROCKET_TWINKLE, Sound.Source.MASTER, 1f, 1f), particleX, particleY, particleZ)
        instance.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.MASTER, .3f, 1f), particleX, particleY, particleZ)


        val loot = block.get<LootCrate>(LootCrate.lootKey) ?: return

        // TODO re-implement block breaking
        // block.get("map", serializer = mapSerializer)?.keys?

        if (destroy !is BlockHandler.PlayerDestroy) return

        arrayOf(destroy.player).forEach { player ->
            player.playSound(Sound.sound(
                SoundEvent.BLOCK_NOTE_BLOCK_PLING,
                Sound.Source.PLAYER,
                1f,
                1.41f // g"
            ), blockPosition)

            if (loot.rewards.isNotEmpty())
                player.sendMessage(
                    Component.text("Loot crate opened: ", NamedTextColor.GREEN)
                )

            loot.rewards.forEach { reward ->
                val message = try {
                    reward.dispatch(
                        player,
                        loot, instance, blockPosition
                    )
                } catch (exception: Exception) {
                    Manager.exception.handleException(exception)
                    Component.text("An internal error occurred", NamedTextColor.RED)
                }

                if (message != Component.empty()) {
                    player.sendMessage(Component.text("-", NamedTextColor.DARK_GRAY)
                            .append(Component.space())
                            .append(message.color(NamedTextColor.GRAY)))
                }
            }
        }
    }

    override fun tick(tick: BlockHandler.Tick) = with(tick) {

        val particle = ParticleCreator.createParticlePacket(
            Particle.CRIT,
            blockPosition.x() + .5, blockPosition.y() + .5, blockPosition.z() + .5,
            .5f, .5f, .5f, 10
        )
        instance.players.forEach {
            if (blockPosition.distanceSquared(it.position) <= 100)
                it.playerConnection.sendPacket(particle)
        }

//        block.get("map", serializer = mapSerializer)?.toMutableMap()?.mapNotNull {
//            if (it.value - 1 <= 0) {
//                return@mapNotNull null
//            }
//
//            it.key to it.value - 1
//        }.let { block.withTag("map", serializer = mapSerializer) }
//
//        getBreakers(instance, blockPosition)?.forEach { player ->
//            internalMap[player] = data?.get<Int>("ticks") ?: 20
//        }
    }

    override fun getNamespaceId(): NamespaceID = NamespaceID.from("cepi:loot_crate")

    // Notes g' -> e"
    // 0.5*2^(<Note Block clicks>/12)
    // source: https://www.reddit.com/r/Minecraft/comments/1hazq2/table_and_formula_to_convert_note_block_clicks/
    private val noteBlockPitches = floatArrayOf(0.71f, 0.75f, 0.79f, 0.84f, 0.89f, 0.94f, 1.0f, 1.06f, 1.12f, 1.19f)
}