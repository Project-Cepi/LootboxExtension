package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import net.minestom.server.utils.math.IntRange
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

sealed class RangeReward(
    private val range: IntRange,
    val lambdaProcessor: (target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition, decidedNumber: Int) -> Component
): Reward() {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {
        range.minimum = range.minimum.coerceAtLeast(0)
        range.maximum = range.maximum.coerceAtMost(Int.MAX_VALUE)

        val decidedNumber = ((range.minimum)..(range.maximum)).random()

        return lambdaProcessor(target, lootcrate, instance, position, decidedNumber)
    }

}

sealed class RangeRewardGenerator<R: RangeReward>(val clazz: KClass<R>): RewardGenerator<R> {
    override fun generateReward(sender: CommandSender, args: List<Any>): R? {

        if (args.isEmpty()) return null

        val intRange = args[0] as? IntRange ?: return null

        return clazz.primaryConstructor!!.call(intRange)

    }
}