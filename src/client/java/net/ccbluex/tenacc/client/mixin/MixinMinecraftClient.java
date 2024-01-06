package net.ccbluex.tenacc.client.mixin;

import net.ccbluex.tenacc.ClientTestManager;
import net.ccbluex.tenacc.impl.common.TickEvent;
import net.ccbluex.tenacc.input.InputManager;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.PresetsScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FlatLevelGeneratorPresetTags;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.structure.StructureSet;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
	private static final String TENACC_WORLD_NAME = "tenacc_test_world";
	private static final String WORLD_GENERATION_PRESET = "minecraft:air;minecraft:the_void";

	@Shadow public abstract IntegratedServerLoader createIntegratedServerLoader();

	@Shadow public abstract void setScreen(@Nullable Screen screen);

	@Shadow public abstract LevelStorage getLevelStorage();

	@Inject(at = @At("HEAD"), method = "run")
	private void run(CallbackInfo info) {
		ClientTestManager.INSTANCE.init();
	}

	@Inject(at = @At("HEAD"), method = "tick")
	private void tick(CallbackInfo info) {
		InputManager.INSTANCE.tickInput();

		ClientTestManager.INSTANCE.getSequenceManager().onEvent(new TickEvent());
		ClientTestManager.INSTANCE.tick();
	}

	@Inject(at = @At("HEAD"), method="onInitFinished", cancellable = true)
	private void injectStartupWorldLoading(MinecraftClient.LoadingContext loadingContext, CallbackInfoReturnable<Runnable> cir) {
		if (!ClientTestManager.INSTANCE.getTestProvider().getHeadlessMode())
			return;

		if (!this.getLevelStorage().levelExists(TENACC_WORLD_NAME)) {
			// Generate the world. This is goofy af, but it works.
			CreateWorldScreen.create(MinecraftClient.getInstance(), null);

			try {
				var gameRules = new GameRules();

				gameRules.get(GameRules.DO_MOB_SPAWNING).set(false, null);
				gameRules.get(GameRules.DO_WEATHER_CYCLE).set(false, null);
				gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
				gameRules.get(GameRules.ANNOUNCE_ADVANCEMENTS).set(false, null);
				gameRules.get(GameRules.DO_IMMEDIATE_RESPAWN).set(true, null);
				gameRules.get(GameRules.SPAWN_RADIUS).set(0, null);

				var createWorldScreen = ((CreateWorldScreen) MinecraftClient.getInstance().currentScreen);

				var worldCreator = createWorldScreen.getWorldCreator();
//
				worldCreator.setWorldName(TENACC_WORLD_NAME);
				worldCreator.setDifficulty(Difficulty.NORMAL);
				worldCreator.setGameMode(WorldCreator.Mode.CREATIVE);
				worldCreator.setCheatsEnabled(true);
//			worldCreator.setWorldType(new WorldCreator.WorldType(world));
				worldCreator.setBonusChestEnabled(false);
				worldCreator.setGenerateStructures(false);
				worldCreator.setSeed("tenacc");
				worldCreator.setGameRules(gameRules);

				var flatWorldType = worldCreator.getNormalWorldTypes().stream().filter(x -> x.preset().getKey().orElseThrow().getValue().getPath().equals("flat")).findFirst().orElseThrow();

				DynamicRegistryManager.Immutable dynamicRegistryManager = worldCreator.getGeneratorOptionsHolder().getCombinedRegistryManager();

				FeatureSet featureSet = worldCreator.getGeneratorOptionsHolder().dataConfiguration().enabledFeatures();
				RegistryWrapper.Impl registryEntryLookup = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.BIOME);
				RegistryWrapper.Impl registryEntryLookup2 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.STRUCTURE_SET);
				RegistryWrapper.Impl registryEntryLookup3 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE);
				RegistryWrapper registryEntryLookup4 = dynamicRegistryManager.getWrapperOrThrow(RegistryKeys.BLOCK).withFeatureFilter(featureSet);

				FlatChunkGenerator chunkGenerator = (FlatChunkGenerator) flatWorldType.preset().value().getOverworld().orElseThrow().chunkGenerator();


				FlatChunkGeneratorConfig flatChunkGeneratorConfig = PresetsScreen.parsePresetString((RegistryEntryLookup<Block>)((RegistryEntryLookup)registryEntryLookup4), (RegistryEntryLookup<Biome>)((RegistryEntryLookup)registryEntryLookup), (RegistryEntryLookup<StructureSet>)((RegistryEntryLookup)registryEntryLookup2), (RegistryEntryLookup<PlacedFeature>)((RegistryEntryLookup)registryEntryLookup3), WORLD_GENERATION_PRESET, chunkGenerator.getConfig());

				chunkGenerator.config = flatChunkGeneratorConfig;

				worldCreator.setWorldType(flatWorldType);

				createWorldScreen.createLevel();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			cir.cancel();
			return;
		}

		this.createIntegratedServerLoader().start(TENACC_WORLD_NAME, () -> {
			throw new IllegalStateException("Failed to load tenacc world");
		});


		cir.cancel();

		//this.createIntegratedServerLoader().start("tenacc_test_world", () -> this.setScreen(new TitleScreen()));
	}
}