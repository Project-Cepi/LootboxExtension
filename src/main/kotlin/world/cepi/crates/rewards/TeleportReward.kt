package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.location.RelativeVec
import world.cepi.crates.model.LootCrate

class TeleportReward(val relativeVec: RelativeVec): Reward() {

    override fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component {

        target.teleport(relativeVec.from(position.toPosition()).toPosition())

        return Component.empty()
    }

    companion object: RewardGenerator<TeleportReward> {
        override fun generateReward(sender: CommandSender, args: List<Any>): TeleportReward? {

            if (args.isEmpty()) return null

            val relativeVec = args[0] as? RelativeVec ?: return null

            return TeleportReward(relativeVec)

        }
    }

}