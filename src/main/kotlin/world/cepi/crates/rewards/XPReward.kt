package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.math.IntRange
import world.cepi.crates.model.LootCrate
import world.cepi.level.ExperienceManager

class XPReward(val xp: IntRange) : Reward {

    override fun dispatch(
        target: Player,
        lootcrate: LootCrate,
        instance: Instance,
        position: BlockPosition
    ): Component {
        return dispatchRange(xp, target, lootcrate, instance, position) { player, _, _, _, decidedXP, ->
            ExperienceManager.addExperience(player, decidedXP)
            Component.text(decidedXP, NamedTextColor.BLUE)
                .append(Component.space())
                .append(Component.text("XP", NamedTextColor.GRAY))

        }
    }

    override fun generateComponent(): Component {
        return Component.text("${xp.minimum} - ${xp.maximum}", NamedTextColor.BLUE)
            .append(Component.space())
            .append(Component.text("XP", NamedTextColor.GRAY))
    }
}