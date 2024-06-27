package site.siredvin.peripheralium.xplat

object LibPlatform : BasePlatform {
    private var impl: BaseInnerPlatform? = null
    private val informationTracker = ModInformationTracker()

    fun configure(impl: BaseInnerPlatform) {
        this.impl = impl
    }

    override val baseInnerPlatform: BaseInnerPlatform
        get() {
            if (impl == null) {
                throw IllegalStateException("You should configure peripheralium LibPlatform first")
            }
            return impl!!
        }

    override val modInformationTracker: ModInformationTracker
        get() = informationTracker
}
