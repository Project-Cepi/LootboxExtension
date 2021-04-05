package world.cepi.crates.commands

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import world.cepi.crates.LootboxExtension
import world.cepi.crates.commands.subcommand.RewardSubcommand
import world.cepi.crates.model.LootCrate
import world.cepi.crates.rewards.ItemReward
import world.cepi.crates.rewards.MobReward
import world.cepi.crates.rewards.Reward.Companion.rewards
import world.cepi.itemextension.command.itemcommand.sendFormattedMessage
import world.cepi.itemextension.item.Item
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kepi.subcommands.Help
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromConstructor
import world.cepi.kstom.command.arguments.asSubcommand
import world.cepi.mobextension.Mob
import java.util.*
import kotlin.reflect.full.primaryConstructor

object LootcrateCommand : Command("lootcrate") {

    val name = ArgumentType.Word("name")

    private val create = "create".asSubcommand()
    private val get = "get".asSubcommand()
    private val info = "info".asSubcommand()
    private val list = "list".asSubcommand()

    init {
        addSyntax { sender ->
            sender.sendFormattedMessage(lootcrateUsage, Component.empty())
        }

        addSyntax(create, name) { sender, args ->
            val crate = LootboxExtension.crates.firstOrNull { it.name == args.get(name) }
            if (crate != null) sender.sendFormattedMessage(lootcrateAlreadyExists, Component.empty())

            LootboxExtension.crates.add(LootCrate(args.get(name)))
            sender.sendFormattedMessage(lootcrateAdded, Component.empty())
        }

        addSyntax(get, name) { sender, args ->
            val crate = getCrate(sender, args) ?: return@addSyntax

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

    fun getCrate(sender: CommandSender, context: CommandContext): LootCrate? {
        val name = context.get(name)
        val crate = LootboxExtension.crates.firstOrNull { it.name == name }
        if (crate == null) {
            sender.sendFormattedMessage(lootcrateDoesNotexist, Component.empty())
            return null
        }
        return crate
    }

    val rewardNames: List<String>
        get() = rewards.map { it.simpleName ?: "" }

}