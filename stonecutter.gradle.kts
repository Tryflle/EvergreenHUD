plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.11"

stonecutter parameters {
    replacements.string {
        direction = eval(current.version, "< 1.21.11")
        from = "net.minecraft.util.Util"
        to = "net.minecraft.Util"
    }
}