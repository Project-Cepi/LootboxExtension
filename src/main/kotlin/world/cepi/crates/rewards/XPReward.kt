package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.math.IntRange
import world.cepi.crates.model.LootCrate
import world.cepi.level.ExperienceManager

class XPReward(private val xp: IntRange) : Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {
        if (xp.minimum == Int.MIN_VALUE) xp.minimum = 0
        if (xp.maximum == Int.MAX_VALUE) xp.maximum = xp.minimum

        val decidedXP = ((xp.minimum)..(xp.maximum)).random()

        ExperienceManager.addExperience(target, decidedXP)
        return Component.empty()
            .append(Component.text(decidedXP, NamedTextColor.BLUE)
            .append(Component.space()))
            .append(Component.text("XP", NamedTextColor.GRAY))
    }
}