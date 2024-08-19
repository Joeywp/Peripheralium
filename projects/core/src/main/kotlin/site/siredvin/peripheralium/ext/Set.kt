package site.siredvin.peripheralium.ext

infix fun <T> Set<T>.xor(that: Set<T>): Set<T> = (this - that) + (that - this)
