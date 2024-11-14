package org.polyfrost.evergreenhud.utils

import net.minecraft.item.ItemStack

object ItemStackUtils {
    fun ItemStack.getLore(): List<String> {
        val list = ArrayList<String>()
        val theTagCompound = this.tagCompound ?: return list
        val theTagList = theTagCompound.getCompoundTag("display").getTagList("Lore", 8)
        for (i in 0..<theTagList.tagCount()) {
            list.add(theTagList.getStringTagAt(i))
        }
        return list
    }
}