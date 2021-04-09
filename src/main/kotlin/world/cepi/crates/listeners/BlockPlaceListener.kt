package world.cepi.crates.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.item.Material
import world.cepi.crates.model.LootCrate
import world.cepi.kepi.messages.sendFormattedTranslatableMessage

fun onBlockPlace(event: PlayerBlockPlaceEvent) {

    val item = event.player.getItemInHand(event.hand)

    val data = item.data ?: return

    if (item.material == Material.CHEST && data.hasKey(LootCrate.lootKey)) {
        event.setCustomBlock(LootCrate.lootKey)
        event.blockData = data

        event.player.sendFormattedTranslatableMessage("lootcrate", "place", Component.text(data.get<LootCrate>(LootCrate.lootKey)!!.name, NamedTextColor.BLUE))
    }
}