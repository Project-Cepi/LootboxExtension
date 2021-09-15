package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.location.RelativeVec
import world.cepi.crates.model.LootCrate
import world.cepi.kstom.util.asVec

class TeleportReward(val relativeVec: RelativeVec): Reward {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: Point): Component {
        target.teleport(relativeVec.from(position.asVec().asPosition()).asPosition())

        return generateComponent()
    }

}