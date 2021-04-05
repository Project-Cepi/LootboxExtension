package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import world.cepi.itemextension.item.Item
import world.cepi.itemextension.item.traits.list.NameTrait

class ItemReward(val item: Item, val amount: Byte): Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {
        target.inventory.addItemStack(item.renderItem(amount))

        return Component.text("$amount ${item.getTrait<NameTrait>()?.name ?: "Item"}!")
            .hoverEvent(item.renderItem(amount).asHoverEvent())

    }

    companion object: RewardGenerator<ItemReward> {
        override fun generateReward(sender: CommandSender, args: List<Any>): ItemReward? {

            val player = sender as? Player ?: return null

            val item = player.itemInMainHand.data?.get<Item>(Item.key) ?: return null

            return ItemReward(item, player.itemInMainHand.amount)
        }
    }

}