package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.rewards.Reward
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.asSubcommand
import world.cepi.kstom.command.arguments.safeArgumentsFromConstructor
import kotlin.reflect.full.primaryConstructor

internal object RewardSubcommand : Command("reward") {

    init {

        Reward.rewards.forEach { rewardPair ->

            val reward = rewardPair.first
            val generator = rewardPair.second

            val arguments = safeArgumentsFromConstructor(reward.primaryConstructor!!).let { arr ->
                if (arr.any { it == null }) return@let emptyList()
                else return@let arr.map { it!! }
            }

            addSyntax(
                LootcrateCommand.existingLootCrate,
                reward.simpleName!!.dropLast("Reward".length).toLowerCase().asSubcommand(),
                *arguments.toTypedArray()
            ) { sender, args ->
                val crate = args.get(LootcrateCommand.existingLootCrate)
                val constructorArgs: List<Any> = arguments.indices.map { index -> args.get(arguments[index]) }

                val generatedReward = generator.generateReward(sender, constructorArgs) ?: let {
                    sender.sendFormattedTranslatableMessage("lootcrate", "reward.invalid")
                    return@addSyntax
                }

                crate.rewards.add(generatedReward)

                sender.sendFormattedTranslatableMessage(
                    "lootcrate",
                    "reward.add",
                    Component.text(reward.simpleName!!.dropLast("Reward".length).toLowerCase(), NamedTextColor.BLUE),
                    Component.text(crate.name, NamedTextColor.YELLOW)
                )
            }

        }
    }

}