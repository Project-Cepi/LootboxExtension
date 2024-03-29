package world.cepi.crates.rewards

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import world.cepi.crates.model.LootCrate
import world.cepi.itemextension.item.Item
import world.cepi.itemextension.item.context.ItemContextParser
import world.cepi.itemextension.item.traits.list.NameTrait
import world.cepi.kstom.command.arguments.generation.annotations.DefaultNumber
import world.cepi.kstom.command.arguments.generation.annotations.MaxAmount
import world.cepi.kstom.command.arguments.generation.annotations.MinAmount
import world.cepi.kstom.command.arguments.generation.annotations.ParameterContext
import java.util.concurrent.ThreadLocalRandom

@Serializable
class ItemReward(
    @ParameterContext(ItemContextParser::class)
    val item: Item,

    @MinAmount(0.0)
    @MaxAmount(1.0)
    @DefaultNumber(1.0)
    val chance: Double,

    @DefaultNumber(1.0)
    private val amount: Int
): Reward() {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: Point): Component {
        ThreadLocalRandom.current().nextDouble(1.0) < chance
        target.inventory.addItemStack(item.renderItem(amount))

        return generateComponent()

    }

    override fun generateComponent(): Component {
        return Component.text("$amount ${item.get<NameTrait>()?.name ?: "Item"} ($chance%)!")
            .hoverEvent(item.renderItem(amount).asHoverEvent())
    }

}