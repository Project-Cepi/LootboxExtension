package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.utils.math.IntRange
import world.cepi.economy.EconomyHandler
import world.cepi.level.ExperienceManager

class MoneyReward(money: IntRange) : RangeReward(money, { player, _, _, _, decidedMoney ->
    EconomyHandler[player] += decidedMoney.toLong()
    Component.empty()
        .append(Component.text(decidedMoney, NamedTextColor.BLUE)
            .append(Component.space()))
        .append(Component.text("Coins", NamedTextColor.GRAY)) // TODO move to currency name
}) {

    companion object: RangeRewardGenerator<MoneyReward>(MoneyReward::class)
}