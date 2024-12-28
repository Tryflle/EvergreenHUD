package org.polyfrost.evergreenhud.utils

import net.minecraft.item.ItemStack

object ItemStackUtils {
    @Suppress("UNNECESSARY_SAFE_CALL")
    inline fun ItemStack.forEachLore(consumer: (String) -> Unit) {
        val tags = this.tagCompound?.getCompoundTag("display")?.getTagList("Lore", 8) ?: return
        for (i in 0..<tags.tagCount()) {
            consumer(tags.getStringTagAt(i))
        }
    }
}