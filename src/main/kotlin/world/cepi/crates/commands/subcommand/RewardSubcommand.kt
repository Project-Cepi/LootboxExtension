package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import world.cepi.crates.commands.existingLootCrate
import world.cepi.crates.rewards.Reward
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.arguments.generation.generateSyntaxes
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand

internal object RewardSubcommand : Kommand({
    Reward.rewards.forEach { reward ->

        val arguments = generateSyntaxes(reward)

        arguments.callback = {
            sender.sendFormattedTranslatableMessage("lootcrate", "reward.invalid")
        }

        arguments.applySyntax(this,
            existingLootCrate,
            reward.simpleName!!.dropLast("Reward".length).lowercase().literal()
        ) { instance ->
            val crate = !existingLootCrate

            crate.rewards.add(instance)

            sender.sendFormattedTranslatableMessage(
                "lootcrate",
                "reward.add",
                Component.text(reward.simpleName!!.dropLast("Reward".length).lowercase(), NamedTextColor.BLUE),
                Component.text(crate.name, NamedTextColor.YELLOW)
            )
        }

    }
}, "reward")
