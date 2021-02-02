package world.cepi.crates.commands

import net.minestom.server.chat.ChatColor
import net.minestom.server.chat.ColoredText
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Arguments
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.arguments.ArgumentWord
import net.minestom.server.command.builder.arguments.number.ArgumentInteger
import net.minestom.server.data.DataImpl
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import world.cepi.crates.LootboxExtension
import world.cepi.crates.model.LootCrate
import world.cepi.crates.model.Reward
import world.cepi.crates.rewards.Rewards
import world.cepi.crates.rewards.SimpleRewards
import world.cepi.kstom.arguments.argumentFromClass
import world.cepi.kstom.command.addSyntax
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType
import kotlin.reflect.jvm.internal.impl.resolve.calls.inference.CapturedType

class LootcrateCommand : Command("lootcrate") {
    private val name: ArgumentWord = ArgumentType.Word("name")
    private val chance: ArgumentInteger = ArgumentType.Integer("chance")
    private val xp = ArgumentType.IntRange("xp")
    private val rewardType = ArgumentType.Word("rewardType").from(*rewardNames)

    private val create = ArgumentType.Word("create").from("create")
    private val add = ArgumentType.Word("create").from("add")
    private val get = ArgumentType.Word("get").from("get")
    private val rewardCommand = ArgumentType.Word("rewardCommand").from("reward")

    init {
        setDefaultExecutor { sender, args ->
            sender.sendMessage(ColoredText.of(ChatColor.RED, "Usage: /lootboxes create|add|get <args>"))
        }

        addSyntax({sender: CommandSender, args: Arguments ->
            val crate = LootboxExtension.crates.firstOrNull { it.name == args.getString("name") }
            if (crate != null) sender.sendMessage(ColoredText.of(ChatColor.RED, "That crate already exists!"))

            LootboxExtension.crates.add(LootCrate(args.getString("name"), mutableListOf()))
            sender.sendMessage(ColoredText.of(ChatColor.BRIGHT_GREEN, "Added crate!"))
        }, create, name)

        addSyntax({sender: CommandSender, args: Arguments ->
            val crate = getCrate(sender, args) ?: return@addSyntax
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
            updateCrate(crate)

            sender.sendMessage(ColoredText.of(ChatColor.BRIGHT_GREEN, "Added item to crate!"))
        }, add, name, chance)

        addSyntax({sender: CommandSender, args: Arguments ->
            val crate = getCrate(sender, args)
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
            val crate = getCrate(sender, args) ?: return@addSyntax

            val xpRange = args.getIntRange("xp")
            crate.minXp = xpRange.minimum
            crate.maxXp = xpRange.maximum

            updateCrate(crate)
        }, name, xp)

        Rewards.forEach { reward ->
            val arguments = mutableListOf<Argument<*>>()
            reward.primaryConstructor!!.parameters.forEach {
                arguments.add(argumentFromClass(it.type as KClass<*>) ?: throw InvalidPropertiesFormatException("Arguments for a Reward must be simple datatypes"))
            }

            addSyntax({ sender: CommandSender, args: Arguments ->
                val crate = getCrate(sender, args) ?: return@addSyntax
                val constructorArgs = mutableListOf<Any>()

                reward.primaryConstructor!!.parameters.forEach {
                    val argAsType = it.type as KClass<*>
                    constructorArgs.add(args.getObject(argAsType.simpleName!!))
                }

                crate.reward = reward.primaryConstructor!!.call(constructorArgs)
                updateCrate(crate)

            }, rewardCommand, name, rewardType, *arguments.toTypedArray())
        }
    }

    private fun getCrate(sender: CommandSender, args: Arguments): LootCrate? {
        val name = args.getWord("name")
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

    private val rewardNames: Array<String>
    get() {
        val names = mutableListOf<String>()
        names.addAll(SimpleRewards.values().map { it.name })
        names.addAll(Rewards.map { it.simpleName ?: "" })
        return names.toTypedArray()
    }
}