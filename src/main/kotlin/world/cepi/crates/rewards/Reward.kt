package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

interface Reward {

    fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component

    companion object {
        internal val rewards: Array<Pair<KClass<out Reward>, RewardGenerator<*>>> = arrayOf(
            XPReward::class to XPReward::class.companionObjectInstance as RewardGenerator<*>,
            ItemReward::class to ItemReward::class.companionObjectInstance as RewardGenerator<*>,
            MobReward::class to MobReward::class.companionObjectInstance as RewardGenerator<*>,
            TeleportReward::class to TeleportReward::class.companionObjectInstance as RewardGenerator<*>
        )
    }
}