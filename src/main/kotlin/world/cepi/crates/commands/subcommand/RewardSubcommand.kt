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
import world.cepi.crates.rewards.RewardGenerator
import world.cepi.itemextension.item.Item
import world.cepi.kepi.messages.sendFormattedMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromConstructor
import world.cepi.kstom.command.arguments.asSubcommand
import world.cepi.kstom.command.arguments.safeArgumentsFromConstructor
import world.cepi.mobextension.Mob
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

internal object RewardSubcommand : Command("reward") {

    init {

        val rewardSubcommand = "reward".asSubcommand()
        val rewardType = ArgumentType.Word("rewardType").from(*LootcrateCommand.rewardNames.toTypedArray())

        Reward.rewards.forEach { reward ->
            val arguments = safeArgumentsFromConstructor(reward.primaryConstructor!!).let { arr ->
                if (arr.any { it == null }) return@let emptyList()
                else return@let arr.map { it!! }
            }

            addSyntax(rewardSubcommand, LootcrateCommand.name, rewardType, *arguments.toTypedArray()) { sender, args ->
                val crate = LootcrateCommand.getCrate(sender, args) ?: return@addSyntax
                val constructorArgs: List<Any> = arguments.indices.map { index -> args.get(arguments[index]) }

                if (reward.companionObject != RewardGenerator::class) return@addSyntax

                val generatedReward = (reward.companionObjectInstance as RewardGenerator<*>).generateReward(sender, constructorArgs) ?: return@addSyntax

                crate.rewards.add(generatedReward)
            }

        }
    }

}