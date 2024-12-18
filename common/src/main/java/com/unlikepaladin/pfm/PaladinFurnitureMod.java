package com.unlikepaladin.pfm;

import com.google.common.collect.ImmutableSet;
import com.unlikepaladin.pfm.blocks.SimpleBedBlock;
import com.unlikepaladin.pfm.blocks.behavior.BathtubBehavior;
import com.unlikepaladin.pfm.blocks.behavior.SinkBehavior;
import com.unlikepaladin.pfm.compat.PFMModCompatibility;
import com.unlikepaladin.pfm.compat.cookingforblockheads.PFMCookingForBlockheads;
import com.unlikepaladin.pfm.compat.farmersdelight.PFMFarmersDelight;
import com.unlikepaladin.pfm.compat.imm_ptl.PFMImmersivePortals;
import com.unlikepaladin.pfm.config.PaladinFurnitureModConfig;

import com.unlikepaladin.pfm.data.materials.DynamicBlockRegistry;
import com.unlikepaladin.pfm.data.materials.StoneVariantRegistry;
import com.unlikepaladin.pfm.data.materials.WoodVariantRegistry;
import com.unlikepaladin.pfm.items.PFMComponents;
import com.unlikepaladin.pfm.mixin.PFMPointOfInterestTypesAccessor;
import com.unlikepaladin.pfm.registry.dynamic.FurnitureEntry;
import com.unlikepaladin.pfm.mixin.PFMPointOfInterestTypeAccessor;
import com.unlikepaladin.pfm.registry.PaladinFurnitureModBlocksItems;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class PaladinFurnitureMod {

	public static final String MOD_ID = "pfm";
	public static final Identifier FURNITURE_DYED_ID = Identifier.of("pfm:furniture_dyed");
	public static final HashMap<Class<? extends Block>, FurnitureEntry<?>> furnitureEntryMap = new LinkedHashMap<>();
	public static SoundEvent FURNITURE_DYED_EVENT = SoundEvent.of(FURNITURE_DYED_ID);

	public static final Logger GENERAL_LOGGER = LogManager.getLogger();
	public static Pair<String, ItemGroup> FURNITURE_GROUP = new Pair<>("furniture", null);
	public static Pair<String, ItemGroup> DYE_KITS = new Pair<>("dye_kits", null);;
	private static PaladinFurnitureModUpdateChecker updateChecker;
	public static boolean isClient = false;
	public static List<PFMModCompatibility> pfmModCompatibilities = new ArrayList<>();
	public void commonInit() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		SinkBehavior.registerBehavior();
		BathtubBehavior.registerBehavior();
		updateChecker = new PaladinFurnitureModUpdateChecker();
		updateChecker.checkForUpdates(getPFMConfig());
		DynamicBlockRegistry.addBlockSetContainer(WoodVariantRegistry.INSTANCE.getType(), WoodVariantRegistry.INSTANCE);
		DynamicBlockRegistry.addBlockSetContainer(StoneVariantRegistry.INSTANCE.getType(), StoneVariantRegistry.INSTANCE);

		if (getModList().contains("cookingforblockheads"))
			pfmModCompatibilities.add(PFMCookingForBlockheads.getInstance());
		if (getModList().contains("farmersdelight"))
			pfmModCompatibilities.add(PFMFarmersDelight.getInstance());
		if (getModList().contains("immersive_portals"))
			pfmModCompatibilities.add(PFMImmersivePortals.getInstance());
		}

	public static void replaceHomePOIStates() {
		PointOfInterestType homePOI = Registries.POINT_OF_INTEREST_TYPE.get(PointOfInterestTypes.HOME);
		Set<BlockState> originalBedStates = ((PFMPointOfInterestTypeAccessor)(Object)homePOI).getBlockStates();
		Set<BlockState> addedBedStates = Arrays.stream(PaladinFurnitureModBlocksItems.getBeds()).flatMap(block -> block.getStateManager().getStates().stream().filter(state -> state.get(SimpleBedBlock.PART) == BedPart.HEAD)).collect(ImmutableSet.toImmutableSet());
		Set<BlockState> newBedStates = new HashSet<>();
		newBedStates.addAll(originalBedStates);
		newBedStates.addAll(addedBedStates);
		((PFMPointOfInterestTypeAccessor) (Object)homePOI).setBlockStates(ImmutableSet.copyOf(newBedStates));
		addedBedStates.forEach(state -> PFMPointOfInterestTypesAccessor.getBlockStateToPointOfInterestType().put(state, Registries.POINT_OF_INTEREST_TYPE.getOrThrow(PointOfInterestTypes.HOME)));
	}

	@ExpectPlatform
    public static PaladinFurnitureModConfig getPFMConfig() {
		throw new AssertionError();
    }

	public static PaladinFurnitureModUpdateChecker getUpdateChecker() {
		return updateChecker;
	}

	@ExpectPlatform
	public static List<String> getModList() {throw new AssertionError();}

	@ExpectPlatform
	public static Map<String, String> getVersionMap() {throw new AssertionError();}

	@ExpectPlatform
	public static Loader getLoader() {
		throw new AssertionError();
	}

	public static boolean isOptifineLoaded() {
		try {
			Class.forName("net.optifine.shaders.Shaders");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	public enum Loader implements StringIdentifiable {
		FORGE("forge"),
		FABRIC_LIKE("fabric_like");
		final String name;
		Loader(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}
	}
}
