package world.cepi.crates.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import net.minestom.server.entity.Player
import world.cepi.crates.LootboxExtension
import world.cepi.crates.commands.subcommand.MetaSubcommand
import world.cepi.crates.commands.subcommand.RewardSubcommand
import world.cepi.crates.model.LootCrate
import world.cepi.crates.rewards.Reward.Companion.rewards
import world.cepi.kepi.command.subcommand.applyHelp
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.addSubcommands
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.arguments.suggest
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.setArgumentCallback

object LootCrateCommand : Command("lootcrate") {

    val name = ArgumentType.Word("name").map { name ->
        val crate = LootboxExtension.crates.firstOrNull { it.name == name }
        if (crate != null) throw ArgumentSyntaxException("Crate exists", name, 1)
        name
    }

    val existingLootCrate = ArgumentType.Word("crate").map { name ->
        LootboxExtension.crates.firstOrNull { it.name == name }
            ?: throw ArgumentSyntaxException("Invalid crate", name, 1)
    }.suggest {
        LootboxExtension.crates.map { it.name }
    }

    private val create = "create".literal()
    private val get = "get".literal()
    private val list = "list".literal()
    private val info = "info".literal()

    init {

        setArgumentCallback(name) {
            sender.sendFormattedTranslatableMessage("lootcrate", "exists", Component.text(exception.input, NamedTextColor.BLUE))
        }

        setArgumentCallback(existingLootCrate) {
            sender.sendFormattedTranslatableMessage("lootcrate", "exists.not", Component.text(exception.input, NamedTextColor.BLUE))
        }

        addSyntax(create, name) {
            LootboxExtension.crates.add(LootCrate(context[name]))
            sender.sendFormattedTranslatableMessage("lootcrate", "added", Component.text(context[name], NamedTextColor.BLUE))
        }

        addSyntax(get, existingLootCrate) {
            val crate = context[existingLootCrate]

            val player = sender as Player

            player.inventory.addItemStack(crate.toItem())

        }

        addSyntax(info, existingLootCrate) {
            val crate = context[existingLootCrate]

            crate.rewards.forEach { sender.sendMessage(it.formattedComponent) }
        }

        addSyntax(list) {
            LootboxExtension.crates.forEach {
                sender.sendMessage(
                    Component.text("-", NamedTextColor.DARK_GRAY)
                        .append(Component.space())
                        .append(Component.text(it.name, NamedTextColor.GRAY))
                        .hoverEvent(HoverEvent.showText(
                            Component.text("Click to show info", NamedTextColor.GRAY)
                        ))
                        .clickEvent(ClickEvent.runCommand("/${getName()} info ${it.name}"))
                )
            }
        }

        applyHelp {
            """
                First, create a lootcrate by doing:
                <yellow>${getName()} create (id)
                
                You can check of the info of a crate or add a reward
                
                Add a reward by doing:
                <yellow>/${getName()} reward (id) (reward) (...args)
                
                For example, XP has the arguments
                (min)..(max) -- like 1..5
                
                Finally, grab the lootcrate by using
                <yellow>/${getName()} get (id)
                And place it down anywhere you wish.
            """.trimIndent()
        }

        addSubcommands(RewardSubcommand, MetaSubcommand)
    }

}