package world.cepi.crates.model

import kotlinx.serialization.Serializable
import world.cepi.crates.rewards.Reward

@Serializable
data class LootCrate(
    val name: String,
    val rewards: MutableList<Reward> = mutableListOf()
) {
    companion object {
        const val lootKey = "cepi-lootcrate"
    }
}