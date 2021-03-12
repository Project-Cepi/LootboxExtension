package world.cepi.crates.rewards

import net.minestom.server.entity.Player
import world.cepi.itemextension.item.Item
import world.cepi.itemextension.item.traits.list.NameTrait

class ItemReward(val item: Item, val amount: Byte): Reward {

    override fun dispatch(target: Player): String {
        target.inventory.addItemStack(item.renderItem(amount))

        return "$amount ${item.getTrait<NameTrait>()?.name ?: "Item"}!"

    }

}