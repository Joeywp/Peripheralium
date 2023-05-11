package site.siredvin.peripheralium.computercraft.operations

import com.google.common.math.IntMath
import net.minecraftforge.common.ForgeConfigSpec
import site.siredvin.peripheralium.api.peripheral.IPeripheralOperation
import kotlin.math.floor

enum class SphereOperation(
    private val defaultCooldown: Int,
    private val defaultMaxFreeRadius: Int,
    private val defaultMaxCostRadius: Int,
    private val defaultExtraBlockCost: Double
) : IPeripheralOperation<SphereOperationContext> {
    SCAN_BLOCKS(2000, 8, 16, 0.17),
    SCAN_ENTITIES(2000, 8, 16, 0.17),
    SCAN_ITEMS(2000, 8, 16, 0.17);

    private var cooldown: ForgeConfigSpec.IntValue? = null
    private var max_free_radius: ForgeConfigSpec.IntValue? = null
    private var max_cost_radius: ForgeConfigSpec.IntValue? = null
    private var extra_block_cost: ForgeConfigSpec.DoubleValue? = null
    override fun addToConfig(builder: ForgeConfigSpec.Builder) {
        cooldown = builder.defineInRange(
            settingsName() + "Cooldown", defaultCooldown, 1000, Int.MAX_VALUE
        )
        max_free_radius = builder.defineInRange(
            settingsName() + "MaxFreeRadius", defaultMaxFreeRadius, 1, 64
        )
        max_cost_radius = builder.defineInRange(
            settingsName() + "MaxCostRadius", defaultMaxCostRadius, 1, 64
        )
        extra_block_cost = builder.defineInRange(
            settingsName() + "ExtraBlockCost", defaultExtraBlockCost, 0.1, Double.MAX_VALUE
        )
    }

    override val initialCooldown: Int
        get() = cooldown!!.get()

    override fun getCooldown(context: SphereOperationContext): Int {
        return cooldown!!.get()
    }

    override fun getCost(context: SphereOperationContext): Int {
        if (context.radius <= max_free_radius!!.get()) return 0
        val freeBlockCount = IntMath.pow(2 * max_free_radius!!.get() + 1, 3)
        val allBlockCount = IntMath.pow(2 * context.radius + 1, 3)
        return floor((allBlockCount - freeBlockCount) * extra_block_cost!!.get()).toInt()
    }

    val maxFreeRadius: Int
        get() = max_free_radius!!.get()
    val maxCostRadius: Int
        get() = max_cost_radius!!.get()

    override fun computerDescription(): Map<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["name"] = settingsName()
        data["type"] = javaClass.name
        data["cooldown"] = cooldown!!.get()
        data["maxFreeRadius"] = max_free_radius!!.get()
        data["maxCostRadius"] = max_cost_radius!!.get()
        data["extraBlockCost"] = extra_block_cost!!.get()
        return data
    }

    fun free(): SphereOperationContext {
        return SphereOperationContext(maxFreeRadius)
    }

    fun cost(): SphereOperationContext {
        return SphereOperationContext(maxCostRadius)
    }
}