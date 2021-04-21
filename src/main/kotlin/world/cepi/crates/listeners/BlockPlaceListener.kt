package world.cepi.crates.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.data.DataImpl
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.item.ItemTag
import net.minestom.server.item.Material
import world.cepi.crates.LootboxExtension
import world.cepi.crates.commands.LootcrateCommand
import world.cepi.crates.model.LootCrate
import world.cepi.kepi.messages.sendFormattedTranslatableMessage

fun onBlockPlace(event: PlayerBlockPlaceEvent) {

    val item = event.player.getItemInHand(event.hand)

    val lootcrateName = item.meta.get(ItemTag.String(LootCrate.lootKey)) ?: return

    if (item.material == Material.CHEST) {
        event.setCustomBlock(LootCrate.lootKey)

        val data = DataImpl()

        data.set(LootCrate.lootKey, LootboxExtension.crates.firstOrNull { it.name == lootcrateName } ?: return)

        event.blockData = data

        event.player.sendFormattedTranslatableMessage("lootcrate", "place", Component.text(data.get<LootCrate>(LootCrate.lootKey)!!.name, NamedTextColor.BLUE))
    }
}