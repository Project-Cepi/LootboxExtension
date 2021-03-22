package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import world.cepi.mobextension.Mob

class MobReward(val mob: Mob) : Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {
        val creature = mob.generateMob()!!
        creature.setInstance(instance, position.clone().add(0, 1, 0).toPosition())

        return Component.empty()

    }

}