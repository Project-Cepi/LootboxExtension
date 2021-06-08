package world.cepi.crates.rewards

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubclassOf

@Serializable
sealed class Reward {

    abstract fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component

    companion object {
        internal val rewards =
            Reward::class.sealedSubclasses
                .onEach { println(it.companionObject) }
                .filter { it.companionObjectInstance is RewardGenerator<*> }
                .associateWith { it.companionObjectInstance as RewardGenerator<*> }
    }
}