package world.cepi.crates.commands

import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import world.cepi.crates.LootboxExtension
import world.cepi.kstom.command.arguments.suggest

val possibleLootCrateName = ArgumentType.Word("name").map { name ->
        val crate = LootboxExtension.crates.firstOrNull { it.name == name }
        if (crate != null) throw ArgumentSyntaxException("Crate exists", name, 1)
        name
    }

val existingLootCrate = ArgumentType.Word("crate").map { name ->
    LootboxExtension.crates.firstOrNull { it.name == name }
        ?: throw ArgumentSyntaxException("Invalid crate", name, 1)
}.suggest {
    LootboxExtension.crates.map { it.name }
}
