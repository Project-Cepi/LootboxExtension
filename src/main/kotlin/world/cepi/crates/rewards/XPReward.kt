package world.cepi.crates.rewards

import net.minestom.server.entity.Player

class XPReward(private val xp: IntRange) : Reward {
    override fun dispatch(target: Player) {
        target.exp = (xp).random().toFloat()
        return
    }
}