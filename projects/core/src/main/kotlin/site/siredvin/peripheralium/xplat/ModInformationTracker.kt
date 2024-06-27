package site.siredvin.peripheralium.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.stats.Stat
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.data.language.ModInformationHolder
import java.util.function.Supplier

class ModInformationTracker : ModInformationHolder {

    val internalItems: MutableList<Supplier<out Item>> = mutableListOf()
    val internalBlocks: MutableList<Supplier<out Block>> = mutableListOf()
    val internalPocketUpgrades: MutableList<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>> = mutableListOf()
    val internalTurtleUpgrades: MutableList<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>> = mutableListOf()
    val internalCustomStats: MutableList<Supplier<Stat<ResourceLocation>>> = mutableListOf()

    override val items: List<Supplier<out Item>>
        get() = internalItems
    override val blocks: List<Supplier<out Block>>
        get() = internalBlocks
    override val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = internalPocketUpgrades
    override val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = internalTurtleUpgrades
    override val customStats: List<Supplier<Stat<ResourceLocation>>>
        get() = internalCustomStats
}
