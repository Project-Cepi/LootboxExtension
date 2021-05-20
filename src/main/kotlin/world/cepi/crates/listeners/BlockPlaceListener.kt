package world.cepi.crates.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import world.cepi.crates.LootboxExtension
import world.cepi.crates.model.LootCrate
import world.cepi.kepi.messages.sendFormattedTranslatableMessage
import world.cepi.kstom.data.data

fun onBlockPlace(event: PlayerBlockPlaceEvent) {

    val item = event.player.getItemInHand(event.hand)

    val lootcrateName = item.meta.getTag(Tag.String(LootCrate.lootKey)) ?: return

    if (item.material == Material.CHEST) {
        event.setCustomBlock(LootCrate.lootKey)

        val data = data {
            this[LootCrate.lootKey] = LootboxExtension.crates.firstOrNull { it.name == lootcrateName } ?: return@data
        }

        event.blockData = data

        event.player.sendFormattedTranslatableMessage("lootcrate", "place", Component.text(data.get<LootCrate>(LootCrate.lootKey)!!.name, NamedTextColor.BLUE))
    }
}