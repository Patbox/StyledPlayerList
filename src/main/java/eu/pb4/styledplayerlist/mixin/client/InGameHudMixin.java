package eu.pb4.styledplayerlist.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @ModifyConstant(method = "render", constant = @Constant(intValue = 1, ordinal = 0), require = 0)
    private int spl_replaceWithZero(int constant) {
        return -1;
    }
}
