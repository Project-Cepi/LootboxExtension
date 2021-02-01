package world.cepi.crates.model

import net.minestom.server.entity.Player

fun interface Reward {
    fun despatch(target: Player)
}