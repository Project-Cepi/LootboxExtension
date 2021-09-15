package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import world.cepi.crates.model.LootCrate
import world.cepi.kstom.command.arguments.generation.annotations.ParameterContext
import world.cepi.mob.arguments.MobMainHandContextParser
import world.cepi.mob.mob.Mob

class MobReward(
    @ParameterContext(MobMainHandContextParser::class) val mob: Mob
) : Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: Point): Component {
        val creature = mob.generateMob() ?: return Component.empty()
        creature.setInstance(instance, position)

        return generateComponent()
    }

}