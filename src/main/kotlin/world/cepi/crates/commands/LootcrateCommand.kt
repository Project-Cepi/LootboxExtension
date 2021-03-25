package world.cepi.crates.commands

import net.kyori.adventure.text.Component
import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import world.cepi.crates.LootboxExtension
import world.cepi.crates.model.LootCrate
import world.cepi.crates.rewards.ItemReward
import world.cepi.crates.rewards.MobReward
import world.cepi.crates.rewards.Reward.Companion.rewards
import world.cepi.itemextension.command.itemcommand.sendFormattedMessage
import world.cepi.itemextension.item.Item
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromConstructor
import world.cepi.kstom.command.arguments.asSubcommand
import world.cepi.mobextension.Mob
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

        rewards.forEach { reward ->

            when(reward) {
                ItemReward::class -> {
                    addSyntax(rewardSubcommand, name, rewardType) { sender, args ->
                        val crate = getCrate(sender, args) ?: return@addSyntax
                        val player = sender as Player
                        val item = player.itemInMainHand.data?.get<Item>(Item.key) ?: return@addSyntax
                        crate.rewards.add(ItemReward(item, player.itemInMainHand.amount))

                        player.sendFormattedMessage(lootcrateRewardAdded, Component.text("Item"))
                    }
                }
                MobReward::class -> {
                    addSyntax(rewardSubcommand, name, rewardType) { sender, args ->
                        val crate = getCrate(sender, args) ?: return@addSyntax
                        val player = sender as Player
                        val mob = player.itemInMainHand.data?.get<Mob>(Mob.mobKey) ?: return@addSyntax
                        crate.rewards.add(MobReward(mob))

                        player.sendFormattedMessage(lootcrateRewardAdded, Component.text("Mob"))
                    }
                }
                else -> {
                    val arguments = argumentsFromConstructor(reward.primaryConstructor!!)

                    addSyntax(rewardSubcommand, name, rewardType, *arguments.toTypedArray()) { sender, args ->
                        val crate = getCrate(sender, args) ?: return@addSyntax
                        val constructorArgs: List<Any> = arguments.indices.map { index -> args.get(arguments[index]) }
                        crate.rewards.add(reward.primaryConstructor!!.call(*constructorArgs.toTypedArray()))
                    }
                }
            }
        }
    }

    private fun getCrate(sender: CommandSender, context: CommandContext): LootCrate? {
        val name = context.get(name)
        val crate = LootboxExtension.crates.firstOrNull { it.name == name }
        if (crate == null) {
            sender.sendFormattedMessage(lootcrateDoesNotexist, Component.empty())
            return null
        }
        return crate
    }

    private val rewardNames: List<String>
        get() = rewards.map { it.simpleName ?: "" }

}