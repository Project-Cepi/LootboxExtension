package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import world.cepi.crates.commands.LootCrateCommand
import world.cepi.crates.rewards.Reward
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.generation.generateSyntaxes
import world.cepi.kstom.command.arguments.literal
import kotlin.reflect.full.primaryConstructor

internal object RewardSubcommand : Command("reward") {

    init {

        Reward.rewards.forEach { reward ->

            val arguments = generateSyntaxes(reward)

            arguments.callback = {
                sender.sendFormattedTranslatableMessage("lootcrate", "reward.invalid")
            }

            arguments.applySyntax(this,
                LootCrateCommand.existingLootCrate,
                reward.simpleName!!.dropLast("Reward".length).lowercase().literal()
            ) { instance ->
                val crate = context[LootCrateCommand.existingLootCrate]

                crate.rewards.add(instance)

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