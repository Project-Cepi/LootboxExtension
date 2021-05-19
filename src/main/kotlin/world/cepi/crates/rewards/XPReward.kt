package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.utils.math.IntRange
import world.cepi.level.ExperienceManager

class XPReward(xp: IntRange) : RangeReward(xp, { player, _, _, _, decidedXP ->
    ExperienceManager.addExperience(player, decidedXP)
    Component.empty()
        .append(Component.text(decidedXP, NamedTextColor.BLUE)
            .append(Component.space()))
        .append(Component.text("XP", NamedTextColor.GRAY))
}) {

    companion object: RangeRewardGenerator<XPReward>(XPReward::class)
}