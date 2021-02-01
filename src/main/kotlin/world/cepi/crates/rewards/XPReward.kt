package world.cepi.crates.rewards

import net.minestom.server.entity.Player
import world.cepi.crates.model.Reward

class XPReward(private val minXp: Int, private val maxXp: Int) : Reward {
    override fun despatch(target: Player) {
        target.exp = (minXp..maxXp).random().toFloat()
        return
    }
}