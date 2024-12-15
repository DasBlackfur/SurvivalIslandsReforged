package gay.blackfur.survivalisland.world.density;

import com.mojang.serialization.MapCodec;
import gay.blackfur.survivalisland.SurvivalIsland;
import gay.blackfur.survivalisland.noise.IslandContinentalNoise;
import gay.blackfur.survivalisland.noise.OctaveNoise;
import gay.blackfur.survivalisland.util.ConcurrentLinkedHashCache;
import gay.blackfur.survivalisland.world.util.SeedStealer;

import java.util.Objects;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class IslandContinentalNoiseFunction implements DensityFunction {
    public static final MapCodec<IslandContinentalNoiseFunction> UCODEC = MapCodec.unit(IslandContinentalNoiseFunction::new);
    public static final KeyDispatchDataCodec<IslandContinentalNoiseFunction> CODEC = KeyDispatchDataCodec.of(UCODEC);

    private static final ConcurrentLinkedHashCache<IslandContinentalNoise, IslandContinentalNoise> ISLAND_CONTINENTAL_NOISE_INSTANCE_CACHE =
            new ConcurrentLinkedHashCache<>(1, Integer.MAX_VALUE, 512);

    private final IslandContinentalNoise islandContinentalNoise;

    public IslandContinentalNoiseFunction() {
        this(0);
    }

    public IslandContinentalNoiseFunction(long seed) {
        RandomSource random = new XoroshiroRandomSource(seed);
        PositionalRandomFactory positionalRandomFactory = random.forkPositional();
        OctaveNoise domainWarpNoise = SurvivalIsland.CONFIG.domainWarpNoise.makeLive(positionalRandomFactory.fromHashOf("domain_warp_noise"));
        OctaveNoise rangeVariationNoise = SurvivalIsland.CONFIG.rangeVariationNoise.makeLive(positionalRandomFactory.fromHashOf("range_variation_noise"));
        IslandContinentalNoise islandContinentalNoise = new IslandContinentalNoise(seed,
                SurvivalIsland.CONFIG.islandSize, SurvivalIsland.CONFIG.islandSeperation,
                SurvivalIsland.CONFIG.continentalTargetRangeA.min(), SurvivalIsland.CONFIG.continentalTargetRangeA.max(),
                SurvivalIsland.CONFIG.continentalTargetRangeB.min(), SurvivalIsland.CONFIG.continentalTargetRangeB.max(),
                SurvivalIsland.CONFIG.islandUnderwaterFalloffDistanceMultiplier,
                domainWarpNoise, rangeVariationNoise
        );
        this.islandContinentalNoise = ISLAND_CONTINENTAL_NOISE_INSTANCE_CACHE.computeIfAbsent(islandContinentalNoise, (k) -> islandContinentalNoise);
    }

    @Override
    public double compute(FunctionContext ctx) {
        return islandContinentalNoise.compute(ctx.blockX(), ctx.blockZ());
    }

    @Override
    public void fillArray(double[] ds, ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(ds, this);
    }

    private static IslandContinentalNoiseFunction fork(long seed) {
        return new IslandContinentalNoiseFunction(seed);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        if (visitor instanceof SeedStealer seed) {
            return fork(seed.steal());
        }
        return this;
    }

    @Override
    public double minValue() {
        return islandContinentalNoise.minValue();
    }

    @Override
    public double maxValue() {
        return islandContinentalNoise.maxValue();
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IslandContinentalNoiseFunction that = (IslandContinentalNoiseFunction) o;
        return islandContinentalNoise.equals(that.islandContinentalNoise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(islandContinentalNoise);
    }
}
