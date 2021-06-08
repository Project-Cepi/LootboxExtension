package world.cepi.crates.rewards

import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import world.cepi.crates.model.LootCrate
import kotlin.reflect.full.companionObjectInstance

sealed interface Reward {

    fun dispatch(target: Player, lootcrate: LootCrate, instance: Instance, position: BlockPosition): Component

    companion object {

        val rewards =
            Reward::class.sealedSubclasses
                .associateWith { it.companionObjectInstance as RewardGenerator<*> }
    }
}