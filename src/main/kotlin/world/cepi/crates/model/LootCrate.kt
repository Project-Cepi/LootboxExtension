package world.cepi.crates.model

import kotlinx.serialization.Serializable
import world.cepi.crates.rewards.SimpleRewards

@Serializable
data class LootCrate(
    val name: String,
    val entries: MutableList<LootCrateEntry>,
    var minXp: Int = 0,
    var maxXp: Int = 0,
    var reward: Reward = SimpleRewards.NONE.reward
) {
    @Serializable
    data class LootCrateEntry(
        val namespace: String,
        val count: Int,
        val chance: Int
    )
}
