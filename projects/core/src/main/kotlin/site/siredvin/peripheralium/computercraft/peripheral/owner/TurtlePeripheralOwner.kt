package site.siredvin.peripheralium.computercraft.peripheral.owner

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.peripheralium.api.storage.ContainerUtils
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.computercraft.peripheral.ability.PeripheralOwnerAbility
import site.siredvin.peripheralium.computercraft.peripheral.ability.TurtleFuelAbility
import site.siredvin.peripheralium.util.DataStorageUtil
import site.siredvin.peripheralium.util.world.FakePlayerProviderTurtle
import site.siredvin.peripheralium.util.world.FakePlayerProxy

class TurtlePeripheralOwner(val turtle: ITurtleAccess, val side: TurtleSide) : BasePeripheralOwner() {

    override val level: Level?
        get() = turtle.level
    override val pos: BlockPos
        get() = turtle.position
    override val facing: Direction
        get() = turtle.direction
    override val targetRepresentation: Any
        get() = turtle
    override val owner: Player?
        get() {
            val owningPlayer = turtle.owningPlayer ?: return null
            return turtle.level.getPlayerByUUID(owningPlayer.id)
        }
    override val dataStorage: CompoundTag
        get() = DataStorageUtil.getDataStorage(turtle, side)

    override val storage: SlottedStorage
        get() = TargetableContainer(turtle.inventory)

    override fun markDataStorageDirty() {
        turtle.updateUpgradeNBTData(side)
    }

    override fun <T> withPlayer(function: (FakePlayerProxy) -> T, overwrittenDirection: Direction?, skipInventory: Boolean): T {
        return FakePlayerProviderTurtle.withPlayer(turtle, function, overwrittenDirection = overwrittenDirection, skipInventory = skipInventory)
    }

    override val toolInMainHand: ItemStack
        get() = turtle.inventory.getItem(turtle.selectedSlot)

    override fun storeItem(stored: ItemStack): ItemStack {
        return ContainerUtils.storeItem(turtle.inventory, stored, turtle.selectedSlot, turtle.selectedSlot)
    }

    override fun destroyUpgrade() {
        turtle.setUpgrade(side, null)
    }

    override fun isMovementPossible(level: Level, pos: BlockPos): Boolean {
        return FakePlayerProviderTurtle.withPlayer(turtle, { _ ->
            if (level.isOutsideBuildHeight(pos)) {
                return@withPlayer false
            }
            if (!level.isInWorldBounds(pos)) {
                return@withPlayer false
            }
            if (!level.isLoaded(pos)) {
                return@withPlayer false
            }
            return@withPlayer level.worldBorder.isWithinBounds(pos)
        })
    }

    override fun move(level: Level, pos: BlockPos): Boolean {
        return turtle.teleportTo(level, pos)
    }

    fun attachFuel(maxFuelConsumptionLevel: Int = 1): TurtlePeripheralOwner {
        attachAbility(PeripheralOwnerAbility.FUEL, TurtleFuelAbility(this, maxFuelConsumptionLevel))
        return this
    }
}
