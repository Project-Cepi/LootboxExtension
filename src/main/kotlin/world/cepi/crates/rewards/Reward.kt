package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.isSubclassOf

sealed interface Reward {

    fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component

    companion object {
        internal val rewards: Map<KClass<out Reward>, RewardGenerator<*>> =
            Reward::class.sealedSubclasses.filter { it.isFinal }
                .filter { it.objectInstance as? RewardGenerator<*> != null }
                .associateWith { it.objectInstance as RewardGenerator<*> }
    }
}