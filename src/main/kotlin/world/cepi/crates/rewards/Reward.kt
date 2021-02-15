package world.cepi.crates.rewards

import net.minestom.server.entity.Player
import kotlin.reflect.KClass

fun interface Reward {
    fun dispatch(target: Player)

    companion object {
        val Rewards: Array<KClass<out Reward>> = arrayOf(
            XPReward::class
        )
    }
}