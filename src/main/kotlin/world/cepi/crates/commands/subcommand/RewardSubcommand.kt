package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.Player
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.commands.lootcrateRewardAdded
import world.cepi.crates.rewards.ItemReward
import world.cepi.crates.rewards.MobReward
import world.cepi.crates.rewards.Reward
import world.cepi.itemextension.item.Item
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromConstructor
import world.cepi.kstom.command.arguments.asSubcommand
import world.cepi.mobextension.Mob
import kotlin.reflect.full.primaryConstructor

internal object RewardSubcommand : Command("reward") {

    init {

        val rewardSubcommand = "reward".asSubcommand()
        val rewardType = ArgumentType.Word("rewardType").from(*LootcrateCommand.rewardNames.toTypedArray())

        Reward.rewards.forEach { reward ->

            when(reward) {
                ItemReward::class -> {
                    addSyntax(rewardSubcommand, LootcrateCommand.name, rewardType) { sender, args ->
                        val crate = LootcrateCommand.getCrate(sender, args) ?: return@addSyntax
                        val player = sender as Player
                        val item = player.itemInMainHand.data?.get<Item>(Item.key) ?: return@addSyntax
                        crate.rewards.add(ItemReward(item, player.itemInMainHand.amount))

                        player.sendFormattedMessage(lootcrateRewardAdded, Component.text("Item"))
                    }
                }
                MobReward::class -> {
                    addSyntax(rewardSubcommand, LootcrateCommand.name, rewardType) { sender, args ->
                        val crate = LootcrateCommand.getCrate(sender, args) ?: return@addSyntax
                        val player = sender as Player
                        val mob = player.itemInMainHand.data?.get<Mob>(Mob.mobKey) ?: return@addSyntax
                        crate.rewards.add(MobReward(mob))

                        player.sendFormattedMessage(lootcrateRewardAdded, Component.text("Mob"))
                    }
                }
                else -> {
                    val arguments = argumentsFromConstructor(reward.primaryConstructor!!)

                    addSyntax(rewardSubcommand, LootcrateCommand.name, rewardType, *arguments.toTypedArray()) { sender, args ->
                        val crate = LootcrateCommand.getCrate(sender, args) ?: return@addSyntax
                        val constructorArgs: List<Any> = arguments.indices.map { index -> args.get(arguments[index]) }
                        crate.rewards.add(reward.primaryConstructor!!.call(*constructorArgs.toTypedArray()))
                    }
                }
            }
        }
    }

}