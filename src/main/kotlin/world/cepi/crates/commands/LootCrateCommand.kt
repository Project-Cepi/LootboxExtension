package world.cepi.crates.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import world.cepi.crates.LootboxExtension
import world.cepi.crates.commands.subcommand.MetaSubcommand
import world.cepi.crates.commands.subcommand.RewardSubcommand
import world.cepi.crates.model.LootCrate
import world.cepi.kepi.command.subcommand.applyHelp
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand

object LootCrateCommand : Kommand({

    val create = "create".literal()
    val get = "get".literal()
    val list = "list".literal()
    val info = "info".literal()

    argumentCallback(possibleLootCrateName) {
        sender.sendFormattedTranslatableMessage("lootcrate", "exists", Component.text(exception.input, NamedTextColor.BLUE))
    }

    argumentCallback(existingLootCrate) {
        sender.sendFormattedTranslatableMessage("lootcrate", "exists.not", Component.text(exception.input, NamedTextColor.BLUE))
    }

    syntax(create, possibleLootCrateName) {
        LootboxExtension.crates.add(LootCrate(!possibleLootCrateName))
        sender.sendFormattedTranslatableMessage("lootcrate", "added", Component.text(!possibleLootCrateName, NamedTextColor.BLUE))
    }

    syntax(get, existingLootCrate) {
        val crate = !existingLootCrate

        val player = sender as Player

        player.inventory.addItemStack(crate.toItem())

    }

    syntax(info, existingLootCrate) {
        val crate = !existingLootCrate

        crate.rewards.forEach { sender.sendMessage(it.formattedComponent) }
    }

    syntax(list) {
        LootboxExtension.crates.forEach {
            sender.sendMessage(
                Component.text("-", NamedTextColor.DARK_GRAY)
                    .append(Component.space())
                    .append(Component.text(it.name, NamedTextColor.GRAY))
                    .hoverEvent(HoverEvent.showText(
                        Component.text("Click to show info", NamedTextColor.GRAY)
                    ))
                    .clickEvent(ClickEvent.runCommand("/lootcrate info ${it.name}"))
            )
        }
    }

    applyHelp {
        """
            First, create a lootcrate by doing:
            <yellow>lootcrate create (id)
            
            You can check of the info of a crate or add a reward
            
            Add a reward by doing:
            <yellow>/lootcrate reward (id) (reward) (...args)
            
            For example, XP has the arguments
            (min)..(max) -- like 1..5
            
            Finally, grab the lootcrate by using
            <yellow>/lootcrate get (id)
            And place it down anywhere you wish.
        """.trimIndent()
    }

    addSubcommands(RewardSubcommand, MetaSubcommand)

}, "lootcrate")