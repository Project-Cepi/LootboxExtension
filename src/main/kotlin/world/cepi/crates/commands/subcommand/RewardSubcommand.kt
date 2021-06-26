package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.rewards.Reward
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromFunction
import world.cepi.kstom.command.arguments.literal
import kotlin.reflect.full.primaryConstructor

internal object RewardSubcommand : Command("reward") {

    init {

        Reward.rewards.forEach { rewardPair ->

            val reward = rewardPair.key
            val generator = rewardPair.value

            val arguments = argumentsFromFunction(reward.primaryConstructor ?: return@forEach)
                .filterNotNull()

            addSyntax(
                LootcrateCommand.existingLootCrate,
                reward.simpleName!!.dropLast("Reward".length).lowercase().literal(),
                *arguments.toTypedArray()
            ) {
                val crate = context[LootcrateCommand.existingLootCrate]
                val constructorArgs: List<Any> = arguments.map { arg -> context[arg] }

                val generatedReward = generator.generateReward(sender, constructorArgs) ?: run {
                    sender.sendFormattedTranslatableMessage("lootcrate", "reward.invalid")
                    return@addSyntax
                }

                crate.rewards.add(generatedReward)

                sender.sendFormattedTranslatableMessage(
                    "lootcrate",
                    "reward.add",
                    Component.text(reward.simpleName!!.dropLast("Reward".length).lowercase(), NamedTextColor.BLUE),
                    Component.text(crate.name, NamedTextColor.YELLOW)
                )
            }

        }
    }

}