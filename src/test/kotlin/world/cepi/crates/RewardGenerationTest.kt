package world.cepi.crates

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import world.cepi.crates.rewards.Reward

class RewardGenerationTest {

    @Test
    fun `make sure there are one or more generated rewards`() {
        assertTrue(Reward.rewards.isNotEmpty(), "Reward map is empty, generation failed.")
    }

}