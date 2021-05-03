package world.cepi.crates.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.entity.Player
import world.cepi.crates.LootboxExtension
import world.cepi.crates.commands.subcommand.RewardSubcommand
import world.cepi.crates.model.LootCrate
import world.cepi.crates.rewards.Reward.Companion.rewards
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kepi.subcommands.Help
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.setArgumentCallback

object LootcrateCommand : Command("lootcrate") {

    val name = ArgumentType.Word("name").map { name ->
        val crate = LootboxExtension.crates.firstOrNull { it.name == name }
        if (crate != null) throw ArgumentSyntaxException("Crate exists", name, 1)
        name
    }

    val existingLootCrate = ArgumentType.Word("crate").map { name ->
        LootboxExtension.crates.firstOrNull { it.name == name }
            ?: throw ArgumentSyntaxException("Invalid crate", name, 1)
    }

    private val create = "create".literal()
    private val get = "get".literal()
    private val list = "list".literal()

    init {

        setArgumentCallback(name) { sender, exception ->
            sender.sendFormattedTranslatableMessage("lootcrate", "exists", Component.text(exception.input, NamedTextColor.BLUE))
        }

        setArgumentCallback(existingLootCrate) { sender, exception ->
            sender.sendFormattedTranslatableMessage("lootcrate", "exists.not", Component.text(exception.input, NamedTextColor.BLUE))
        }

        addSyntax { sender ->
            sender.sendFormattedTranslatableMessage("common", "usage", Component.text("/lootboxes create|get|list|info|reward <args>"))
        }

        addSyntax(create, name) { sender, args ->
            LootboxExtension.crates.add(LootCrate(args.get(name)))
            sender.sendFormattedTranslatableMessage("lootcrate", "added", Component.text(args.get(name), NamedTextColor.BLUE))
        }

        addSyntax(get, existingLootCrate) { sender, args ->
            val crate = args.get(existingLootCrate)

            val player = sender as Player

            player.inventory.addItemStack(crate.toItem())

        }

        addSyntax(list) { sender ->
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

        addSubcommand(Help(
            Component.text("First, create a lootcrate by doing:"),
            Component.empty()
                .append(Component.text("/${getName()} create <id>", NamedTextColor.YELLOW)),
            Component.space(),
            Component.text("You can check the info of a crate or add a reward."),
            Component.space(),
            Component.text("Add a reward by doing:"),
            Component.empty()
                .append(Component.text("/${getName()} reward <id> <reward> <args>", NamedTextColor.YELLOW)),
            Component.space(),
            Component.text("For example, XP has the arguments ")
                .append(Component.text("<min>..<max>", NamedTextColor.BLUE)),
            Component.text("Finally, grab the lootcrate by using"),
            Component.empty()
                .append(Component.text("/${getName()} get <id>", NamedTextColor.YELLOW)),
        ))

        addSubcommand(RewardSubcommand)
    }

    val rewardNames: List<String>
        get() = rewards.map { it.first.simpleName ?: "" }

}