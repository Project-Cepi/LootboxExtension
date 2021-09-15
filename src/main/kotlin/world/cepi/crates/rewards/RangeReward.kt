package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import world.cepi.crates.model.LootCrate
import net.minestom.server.utils.math.IntRange

fun dispatchRange(
    range: IntRange,
    target: Player, lootcrate: LootCrate, instance: Instance, position: Point,
    lambdaProcessor: (target: Player, lootcrate: LootCrate, instance: Instance, position: Point, decidedNumber: Int) -> Component
): Component {
    val decidedNumber = ((range.minimum.coerceAtLeast(0))..(range.maximum.coerceAtMost(Int.MAX_VALUE))).random()

    return lambdaProcessor(target, lootcrate, instance, position, decidedNumber)
}