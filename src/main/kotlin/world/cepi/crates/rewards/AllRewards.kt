package world.cepi.crates.rewards

import world.cepi.crates.model.Reward
import kotlin.reflect.KClass

internal val Rewards: Array<KClass<out Reward>> = arrayOf(
    XPReward::class
)