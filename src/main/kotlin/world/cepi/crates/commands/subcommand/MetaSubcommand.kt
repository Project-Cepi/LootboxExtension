package world.cepi.crates.commands.subcommand

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.command.builder.Command
import world.cepi.crates.commands.LootCrateCommand
import world.cepi.crates.meta.LootCrateMeta
import world.cepi.kepi.command.subcommand.KepiMetaSubcommand
import world.cepi.kepi.messages.sendFormattedTranslatableMessage

internal object MetaSubcommand: KepiMetaSubcommand<LootCrateMeta>(
    LootCrateMeta::class,
    "meta",
    "meta",
    { instance, name ->
        val crate = context[LootCrateCommand.existingLootCrate]

        crate.putMeta(instance)

        sender.sendFormattedTranslatableMessage(
            "lootcrate", "meta.set",
            Component.text(name, NamedTextColor.BLUE)
        )
    },

    { clazz, name ->
        val crate = context[LootCrateCommand.existingLootCrate]

        // TODO remove meta
    }
)