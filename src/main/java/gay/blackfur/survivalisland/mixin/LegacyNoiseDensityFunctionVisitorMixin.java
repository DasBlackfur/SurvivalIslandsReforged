package gay.blackfur.survivalisland.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import gay.blackfur.survivalisland.world.util.SeedStealer;

@Mixin(targets = "public net.minecraft.world.level.levelgen.RandomState$1NoiseWiringHelper")
public class LegacyNoiseDensityFunctionVisitorMixin implements SeedStealer {
    @Unique
    private long seed;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void captureCtorForIsland(NoiseGeneratorSettings chunkGeneratorSettings, HolderGetter noiseParametersLookup, long seed, CallbackInfo ci) {
        this.seed = seed;
    }

    @Override
    public long steal() {
        return this.seed;
    }
}
