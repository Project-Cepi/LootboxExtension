package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import world.cepi.crates.model.LootCrate

sealed interface Reward {

    fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: Point): Component

    fun generateComponent(): Component = Component.empty()

    val formattedComponent: Component
        get() = Component.text("-", NamedTextColor.DARK_GRAY)
        .append(Component.space())
        .append(generateComponent().color(NamedTextColor.GRAY))

    companion object {

        val rewards = Reward::class.sealedSubclasses
    }
}