package world.cepi.crates.rewards

import kotlin.reflect.KClass

internal val Rewards: Array<KClass<out Reward>> = arrayOf(
    XPReward::class
)