package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import world.cepi.kstom.command.arguments.generation.annotations.ParameterContext
import world.cepi.mob.arguments.MobMainHandContextParser
import world.cepi.mob.mob.Mob
import world.cepi.mob.mob.mobEgg

class MobReward(
    @ParameterContext(MobMainHandContextParser::class) val mob: Mob
) : Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {
        val creature = mob.generateMob() ?: return Component.empty()
        creature.setInstance(instance, position.toPosition())

        return generateComponent()
    }

}