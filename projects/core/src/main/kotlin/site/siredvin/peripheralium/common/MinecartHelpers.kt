package site.siredvin.peripheralium.common

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.vehicle.AbstractMinecart
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import site.siredvin.peripheralium.api.storage.SlottedStorage
import site.siredvin.peripheralium.api.storage.TargetableContainer
import site.siredvin.peripheralium.util.MergedContainer

object MinecartHelpers {
    private const val SEARCH_MARGIN = 0.2

    fun getSearchShape(pos: BlockPos): AABB {
        return AABB(
            pos.x.toDouble() + SEARCH_MARGIN, pos.y.toDouble() , pos.z.toDouble() + SEARCH_MARGIN,
            (pos.x + 1).toDouble() - SEARCH_MARGIN, (pos.y + 1).toDouble() - SEARCH_MARGIN, (pos.z + 1).toDouble() - SEARCH_MARGIN
        )
    }

    fun getMinecarts(level: Level, pos: BlockPos): List<AbstractMinecart> {
        return level.getEntitiesOfClass(AbstractMinecart::class.java, getSearchShape(pos))
    }

    fun getContainerMinecarts(level: Level, pos: BlockPos): List<AbstractMinecartContainer> {
        return level.getEntitiesOfClass(AbstractMinecartContainer::class.java, getSearchShape(pos))
    }

    fun minecartExtractor(level: Level, obj: Any?): SlottedStorage? {
        if (obj !is BlockPos)
            return null
        val state = level.getBlockState(obj)
        if (!state.`is`(Blocks.POWERED_RAIL))
            return null
        val containers = getContainerMinecarts(level, obj)
        if(containers.isEmpty())
            return null
        return TargetableContainer(MergedContainer(containers))
    }
}