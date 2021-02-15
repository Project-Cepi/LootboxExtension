package world.cepi.crates.rewards

import net.minestom.server.entity.Player
import world.cepi.level.ExperienceManager

class XPReward(private val xp: net.minestom.server.utils.math.IntRange) : Reward {
    override fun dispatch(target: Player) {
        ExperienceManager.addExperience(target, ((xp.minimum)..(xp.maximum)).random())
        return
    }
}