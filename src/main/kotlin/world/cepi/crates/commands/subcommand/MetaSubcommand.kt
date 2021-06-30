package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import world.cepi.crates.commands.LootCrateCommand
import world.cepi.crates.meta.LootCrateMeta
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.command.addSyntax
import world.cepi.kstom.command.arguments.argumentsFromClass
import world.cepi.kstom.command.arguments.literal

internal object MetaSubcommand: Command("meta") {

    init {

        val set = "set".literal()

        LootCrateMeta::class.sealedSubclasses.forEach {
            val arguments = argumentsFromClass(it)

            val readableName = it.simpleName!!.dropLast("Meta".length)

            addSyntax(
                set,
                readableName.lowercase().literal(),
                LootCrateCommand.existingLootCrate,
                *arguments.args
            ) {
                val crate = context[LootCrateCommand.existingLootCrate]
                val instance = arguments.createInstance(context, sender)

                crate.putMeta(instance)

                sender.sendFormattedTranslatableMessage(
                    "lootcrate", "meta.set",
                    Component.text(readableName, NamedTextColor.BLUE)
                )
            }
        }
    }

}