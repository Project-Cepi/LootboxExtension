package world.cepi.crates.commands

import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import world.cepi.crates.LootboxExtension
import world.cepi.crates.model.LootCrate
import world.cepi.crates.rewards.Rewards
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromConstructor
import world.cepi.kstom.command.arguments.asSubcommand
import java.util.*
import kotlin.reflect.full.primaryConstructor

class LootcrateCommand : Command("lootcrate") {

    private val name = ArgumentType.Word("name")
    private val rewardType = ArgumentType.Word("rewardType").from(*rewardNames.toTypedArray())

    private val create = "create".asSubcommand()
    private val get = "get".asSubcommand()
    private val rewardSubcommand = "reward".asSubcommand()

    init {
        addSyntax { sender ->
            sender.sendMessage(ColoredText.of(ChatColor.RED, "Usage: /lootboxes create|add|get <args>"))
        }

        addSyntax(create, name) { sender, args ->
            val crate = LootboxExtension.crates.firstOrNull { it.name == args.get(name) }
            if (crate != null) sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate already exists!"))

            LootboxExtension.crates.add(LootCrate(args.get(name)))
            sender.sendMessage(ColoredText.of(ChatColor.BRIGHT_GREEN, "Added crate!"))
        }

        addSyntax(get, name) { sender, args ->
            val crate = getCrate(sender, args)
            if (sender !is Player) {
                sender.sendMessage(ColoredText.of(ChatColor.RED, "Please only run this command as a player"))
                return@addSyntax
            }

            val barrel = ItemStack(Material.BARREL, 1)
            val barrelData = DataImpl()
            barrelData.set(LootCrate.lootKey, crate, LootCrate::class.java)
            barrel.data = barrelData
            barrel.displayName = ColoredText.of(ChatColor.GOLD, "Loot Crate")

            sender.inventory.addItemStack(barrel)

        }

        Rewards.forEach { reward ->
            val arguments = argumentsFromConstructor(reward.primaryConstructor!!)

            addSyntax(rewardSubcommand, name, rewardType, *arguments.toTypedArray()) { sender, args ->
                val crate = getCrate(sender, args) ?: return@addSyntax
                val constructorArgs: List<Any> = arguments.mapIndexed { index, _ -> args.get(arguments[index]) }

                crate.rewards.add(reward.primaryConstructor!!.call(constructorArgs))
                updateCrate(crate)

            }
        }
    }

    private fun getCrate(sender: CommandSender, args: Arguments): LootCrate? {
        val name = args.get(name)
        val crate = LootboxExtension.crates.firstOrNull { it.name == name }
        if (crate == null) {
            sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate does not exist!"))
            return null
        }
        return crate
    }

    private fun updateCrate(crate: LootCrate) {
        LootboxExtension.crates.removeIf { it.name == crate.name }
        LootboxExtension.crates.add(crate)
    }

    private val rewardNames: List<String>
        get() = Rewards.map { it.simpleName ?: "" }

}