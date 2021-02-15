package world.cepi.crates.rewards

import net.minestom.server.entity.Player

fun interface Reward {
    fun dispatch(target: Player)
}