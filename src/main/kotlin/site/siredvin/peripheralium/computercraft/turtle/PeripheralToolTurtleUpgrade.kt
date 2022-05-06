package site.siredvin.peripheralium.computercraft.turtle

import site.siredvin.peripheralium.api.peripheral.IBasePeripheral
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import dan200.computercraft.api.turtle.TurtleUpgradeType
import site.siredvin.peripheralium.util.turtleAdjective

abstract class PeripheralToolTurtleUpgrade<T : IBasePeripheral<*>> : BaseTurtleUpgrade<T> {
    constructor(id: ResourceLocation, adjective: String, item: ItemStack) : super(
        id,
        TurtleUpgradeType.BOTH,
        adjective,
        item
    )

    constructor(id: ResourceLocation, item: ItemStack) : super(
        id,
        TurtleUpgradeType.BOTH,
        turtleAdjective(id),
        item
    )
}