package world.cepi.crates.commands

import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentInteger
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import world.cepi.crates.LootboxExtension
import world.cepi.crates.model.LootCrate

class LootcrateCommand : Command("lootcrate") {
    private val name: ArgumentWord = ArgumentType.Word("name")
    private val chance: ArgumentInteger = ArgumentType.Integer("chance")
    private val xp = ArgumentType.IntRange("xp")

    private val create = ArgumentType.Word("create").from("create")
    private val add = ArgumentType.Word("create").from("add")
    private val get = ArgumentType.Word("get").from("get")

    init {

        addSyntax({sender: CommandSender, args: Arguments ->
            val crate = LootboxExtension.crates.firstOrNull { it.name == args.getString("name") }
            if (crate != null) sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate already exists!"))

            LootboxExtension.crates.add(LootCrate(args.getString("name"), mutableListOf()))
            sender.sendMessage(ColoredText.of(ChatColor.BRIGHT_GREEN, "Added crate!"))
        }, create, name)

        addSyntax({sender: CommandSender, args: Arguments ->
            val name = args.getWord("name")
            val crate = LootboxExtension.crates.firstOrNull { it.name == name }
            if (crate == null) {
                sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate does not exist!"))
                return@addSyntax
            }
            if (sender !is Player) {
                sender.sendMessage(ColoredText.of(ChatColor.RED, "Please only run this command as a player"))
                return@addSyntax
            }

            val stack = sender.itemInMainHand
            crate.entries.add(LootCrate.LootCrateEntry(
                namespace = stack.material.name,
                count = stack.amount.toInt(),
                chance = args.getInteger("chance")
            ))
            LootboxExtension.crates.removeIf { it.name == name }
            LootboxExtension.crates.add(crate)

            sender.sendMessage(ColoredText.of(ChatColor.BRIGHT_GREEN, "Added item to crate!"))
        }, add, name, chance)

        addSyntax({sender: CommandSender, args: Arguments ->
            val name = args.getWord("name")
            val crate = LootboxExtension.crates.firstOrNull { it.name == name }
            if (crate == null) {
                sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate does not exist!"))
                return@addSyntax
            }
            if (sender !is Player) {
                sender.sendMessage(ColoredText.of(ChatColor.RED, "Please only run this command as a player"))
                return@addSyntax
            }

            val barrel = ItemStack(Material.BARREL, 1)
            val barrelData = DataImpl()
            barrelData.set("loot", crate, LootCrate::class.java)
            barrel.data = barrelData
            barrel.displayName = ColoredText.of(ChatColor.GOLD, "Loot Crate")

            sender.inventory.addItemStack(barrel)

        }, get, name)

        addSyntax({sender: CommandSender, args: Arguments ->
            val name = args.getWord("name")
            val crate = LootboxExtension.crates.firstOrNull { it.name == name }
            if (crate == null) {
                sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate does not exist!"))
                return@addSyntax
            }

            val xpRange = args.getIntRange("xp")
            crate.minXp = xpRange.minimum
            crate.maxXp = xpRange.maximum


        })
    }
}