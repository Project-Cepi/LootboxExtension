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

class MoneyReward(val money: IntRange) : Reward {

    override fun dispatch(
        target: Player,
        lootcrate: LootCrate,
        instance: Instance,
        position: BlockPosition
    ): Component {
        return dispatchRange(money, target, lootcrate, instance, position) { player, _, _, _, decidedMoney, ->
            EconomyHandler[player] += decidedMoney.toLong()
            Component.text(decidedMoney, NamedTextColor.BLUE)
                .append(Component.space())
                .append(Component.text("Coins", NamedTextColor.GRAY)) // TODO move to currency name

        }
    }

    override fun generateComponent(): Component {
        return Component.text("${money.minimum} - ${money.maximum}", NamedTextColor.BLUE)
            .append(Component.space())
            .append(Component.text("Coins", NamedTextColor.GRAY))
    }
}