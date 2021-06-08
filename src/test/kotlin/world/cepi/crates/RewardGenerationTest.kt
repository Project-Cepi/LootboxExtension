package world.cepi.crates

import org.junit.jupiter.api.Test
import world.cepi.crates.rewards.Reward

class RewardGenerationTest {

    @Test
    fun `make sure there are one or more generated rewards`() {
        println(Reward::class.sealedSubclasses)
        assert(Reward.rewards.isNotEmpty()) { "Reward map is empty, generation failed. " }
    }

}