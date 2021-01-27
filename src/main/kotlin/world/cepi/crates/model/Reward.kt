package world.cepi.crates.model

import net.minestom.server.entity.Player

enum class Reward(val reward: (Player) -> Unit) {
    NONE({})
}