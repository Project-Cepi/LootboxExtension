package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import world.cepi.itemextension.item.Item
import world.cepi.itemextension.item.itemSerializationModule
import world.cepi.itemextension.item.traits.list.NameTrait
import world.cepi.kstom.command.arguments.annotations.DefaultNumber
import world.cepi.kstom.command.arguments.annotations.MaxAmount
import world.cepi.kstom.command.arguments.annotations.MinAmount
import world.cepi.kstom.item.get
import java.util.concurrent.ThreadLocalRandom

class ItemReward(
    val item: Item,

    @MinAmount(0.0)
    @MaxAmount(1.0)
    @DefaultNumber(1.0)
    val chance: Double,

    @DefaultNumber(1.0)
    val amount: Int
): Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {
        ThreadLocalRandom.current().nextDouble(1.0) < chance
        target.inventory.addItemStack(item.renderItem(amount))

        return generateComponent()

    }

    override fun generateComponent(): Component {
        return Component.text("$amount ${item.get<NameTrait>()?.name ?: "Item"} ($chance%)!")
            .hoverEvent(item.renderItem(amount).asHoverEvent())
    }

    companion object: RewardGenerator<ItemReward> {
        override fun generateReward(sender: CommandSender, args: List<Any>): ItemReward? {

            val player = sender as? Player ?: return null

            val chance = args[0] as? Double ?: return null

            val item = player.itemInMainHand.meta.get<Item>(Item.key, itemSerializationModule) ?: return null

            return ItemReward(item, chance, player.itemInMainHand.amount)
        }
    }

}