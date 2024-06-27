package site.siredvin.peripheralium.xplat

import net.minecraft.world.item.crafting.Ingredient

interface RecipeIngredients {
    companion object {
        private var impl: RecipeIngredients? = null

        fun configure(impl: RecipeIngredients) {
            this.impl = impl
        }

        fun get(): RecipeIngredients {
            if (impl == null) {
                throw IllegalStateException("You should init recipe ingredients first")
            }
            return impl!!
        }
    }

    val redstone: Ingredient
    val glowstoneDust: Ingredient
    val xpBottle: Ingredient
}
