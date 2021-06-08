package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.math.IntRange
import world.cepi.crates.model.LootCrate
import world.cepi.economy.EconomyHandler
import world.cepi.level.ExperienceManager

class MoneyReward(val money: IntRange) : Reward {

    override fun dispatch(
        target: Player,
        lootcrate: LootCrate,
        instance: Instance,
        position: BlockPosition
    ): Component {
        return dispatchRange(money, target, lootcrate, instance, position) { player, _, _, _, decidedMoney, ->
            EconomyHandler[player] += decidedMoney.toLong()
            Component.empty()
                .append(Component.text(decidedMoney, NamedTextColor.BLUE)
                    .append(Component.space()))
                .append(Component.text("Coins", NamedTextColor.GRAY)) // TODO move to currency name

        }
    }

    companion object: RewardGenerator<MoneyReward> {
        override fun generateReward(sender: CommandSender, args: List<Any>): MoneyReward? {

            if (args.isEmpty()) return null

            val intRange = args[0] as? IntRange ?: return null

            return MoneyReward(intRange)

        }
    }
}