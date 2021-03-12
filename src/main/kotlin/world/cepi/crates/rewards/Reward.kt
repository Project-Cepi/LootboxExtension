package world.cepi.crates.rewards

import net.minestom.server.entity.Player
import kotlin.reflect.KClass

interface Reward {
    fun dispatch(target: Player): String

    companion object {
        val rewards: Array<KClass<out Reward>> = arrayOf(
            XPReward::class,
            ItemReward::class
        )
    }
}