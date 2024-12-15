package gay.blackfur.survivalisland;

import com.mojang.logging.LogUtils;
import gay.blackfur.survivalisland.config.ConfigData;
import gay.blackfur.survivalisland.world.density.IslandContinentalNoiseFunction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SurvivalIsland.MODID)
public class SurvivalIsland {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "survivalisland";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ConfigData CONFIG;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SurvivalIsland(IEventBus modEventBus, ModContainer modContainer) {
        CONFIG = gay.blackfur.survivalisland.config.ConfigSerializer.init();
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
    private static class EventSubscriber {
        @SubscribeEvent
        public static void register(RegisterEvent event) {
            System.out.println("BOOM!");
            event.register(
                    // This is the registry key of the registry.
                    // Get these from BuiltInRegistries for vanilla registries,
                    // or from NeoForgeRegistries.Keys for NeoForge registries.
                    BuiltInRegistries.DENSITY_FUNCTION_TYPE.key(),
                    ResourceLocation.fromNamespaceAndPath("survivalisland", "islandcont"),
                    () -> IslandContinentalNoiseFunction.UCODEC
            );
        }
    }
}
