package world.cepi.crates.rewards

import net.minestom.server.command.CommandSender

/**
 * Companion object superclass to generate a reward
 */
internal sealed interface RewardGenerator<out R: Reward> {

    /**
     * Generates a reward from a [CommandSender] and a set of [args]
     *
     * @param sender The sender who wants to generate this reward (useful for grabbing items)
     * @param args The args passed for the reward to use.
     *
     * @return The reward generated, null if invalid.
     */
    fun generateReward(sender: CommandSender, args: List<Any>): R?

}