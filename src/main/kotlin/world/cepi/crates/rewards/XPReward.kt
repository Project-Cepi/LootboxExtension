package world.cepi.crates.rewards

import net.minestom.server.entity.Player
import net.minestom.server.utils.math.IntRange
import world.cepi.level.ExperienceManager

class XPReward(private val xp: IntRange) : Reward {
    override fun dispatch(target: Player) {
        if (xp.minimum == Int.MIN_VALUE) xp.minimum = 0
        if (xp.maximum == Int.MAX_VALUE) xp.maximum = xp.minimum
        ExperienceManager.addExperience(target, ((xp.minimum)..(xp.maximum)).random())
        return
    }
}