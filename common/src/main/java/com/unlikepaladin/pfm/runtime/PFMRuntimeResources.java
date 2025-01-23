package com.unlikepaladin.pfm.runtime;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import com.unlikepaladin.pfm.utilities.PFMFileUtil;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PFMRuntimeResources {
    public static final String base64Icon = "/9j/4AAQSkZJRgABAQAASABIAAD/2wBDABwcHBwcHDAcHDBEMDAwRFxEREREXHRcXFxcXHSMdHR0dHR0jIyMjIyMjIyoqKioqKjExMTExNzc3Nzc3Nzc3Nz/2wBDASIkJDg0OGA0NGDmnICc5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ubm5ub/wAARCAEAAQADASIAAhEBAxEB/8QAGQABAAMBAQAAAAAAAAAAAAAAAAEDBAUC/8QAKBABAQABAwQBBAIDAQAAAAAAAAECAxExBCEycVESIjNBYYETkaGx/8QAGAEBAAMBAAAAAAAAAAAAAAAAAAEDBAL/xAAcEQEBAAMBAQEBAAAAAAAAAAAAAQIDETEhUUH/2gAMAwEAAhEDEQA/ANADG2gAAAAAAAAAAAAAAt/w6n0/V9NVFliJZfAASAAAAAAAAAAAAAAAAAAAAAAAADRp9Nnn3y+2Nuno4afE7/LvHXary2SMWn02effL7Y26ejhp8Tv8rkWxdjhIpyztSo1NLTz5ndbvUOrHMci9rshN5qGRrABIAAAAAAAAAAAAAAAAAANGn02efe/bG3DRw0+J3+XeOu1XlskYsOmzz737Y26ejhp8Tv8AK5FsXY4SKcs7UotkRah05TbUAkAEJci81CbzUMjUACQAAAAAAAAAAAAAAABM5iEzmEQ6szs/l7mUsUveLWy2PdqASgAAAAAQlyLzUJvNQyNQAJAAAAAAAAAAAAAAAAEzmITOYRDpPeLw94tbNXoBLkAAAAAQlyLzUJvNQyNQAJAAAAAAAAAAAAAAAAEzmITOYRDpPeLw94tbNXoBLkAAAAAQlyLzUJvNQyNQAJAAAAAAAAAAAAAAAAEzmITOYRDpPeLw94tbNXoBLkAAAAAQlyLzULM8MsLfqn9q2SzjVKACQAAAAAAAAAAAAAAABM5iEzmEQ6T3i8PeLWzV6AS5AAASCEiQRtLNqzanTY3vh2v/ABqEWS+pmVnjkZ6eeF+6f28O1tLNqy6nS45d8O3/AIpy1/i3Hb+uePeennp37p/bwqs4tl6ACQAAAAAAAAAAABM5iEzmEQ6T3i8PeLWzV6BKXKEiQQkABIAAAACLJZtWXU6XHLvh2/j9NYi4y+pmVnjj56eendsps8O1ZLNqwdRo4YT6se3dRlr59i7HZ35WQBWtAAAAAAAAAAEzmIBDpnDLhr2ds/8AbTLvN40zKVRZxZMvl7m14Upls4dOeLh4mfy9zvwlykAAAAAQAi0SlFrzvuIDfdm6n8f9tFsnesfUauOWP049+7nO/HeE+sgDM0gAAAAAAAAAAADo4+M9Oc6OPjPS3Ur2Pcm6LNk48rFynqpMtnCvU1MdPKS8WPUsym8u6OxPFsz+VnLOmWzhLmxePEz+XrdKOJRa827iA33EW7cvE1ccrZj32Op4sFdtr3OA48av48vTlupq/jy9OWp2rtYAqWgAAAAAAAAAAADo4+M9Oc6OPjPS3Ur2PePKxXjysXKKw9V5z0zTK43eXZp6rznplZsvWjDxqw1/1n/tollm87ua0dP5X07wzvjnLD+taxWsnC5VUs2fUY49se9/4vy8b6clXsys8d4Yy+rM9TPPyv8AS3p/2zNPT/tXhe5fVmU5GpZOFaycNCivGr+PL05bqav48vTlqdq3WAKloAAAAAAAAAAAA6OPjPTnNenrY2THLss12Sq851px5WK8eU56mOE3yq/qmxk6rznplW6up/ky3k22VM2V7WjGcg0dP530ztHT+d9GHpl41rJwrWThpZ6jLxvpyXWy8b6clTtWahp6f9szR09ktlvLjD1Zl41rJwrWThpZ68av48vTlunrWTTu95jmKNvq3X4AK1oAAAAAAAAAAAAAD3jnnj43Z5ttu97oDqOAAkaOn876Z2jp/O+nWHrnLxrWThWsnDSz1GXjfTkutl4305Knas1ACpctw1sse17xbl1N22wm381lHUzrm4yptuV3t3qAcpABIAAAAAAAAAAAAAAAAAAu0LJld7tvFImXl6izs46aycOZjqZYcXt8Lr1N22xm1XTZFNwrVqZTHG73bs5ablcrvld0Ks8urMMeADl2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA//2Q==";

    public static final DirectoryResourcePack ASSETS_PACK = new DirectoryResourcePack(getAssetPackDirectory().toFile());
    public static final DirectoryResourcePack DATA_PACK = new DirectoryResourcePack(getDataPackDirectory().toFile());

    public static volatile List<ResourcePack> RESOURCE_PACK_LIST;
    public static Map<Identifier, PFMBakedModelContainer> modelCacheMap = new ConcurrentHashMap<>();
    private static boolean isAnyGeneratorRunning = false;

    public static byte[] getImageData() {
        return Base64.getDecoder().decode(base64Icon);
    }

    public static Path getDataPackDirectory() {
        return createDirIfNeeded(getPFMDirectory().resolve("cache/pfm-datapack"));
    }

    public static Path getAssetPackDirectory() {
        return createDirIfNeeded(getPFMDirectory().resolve("cache/pfm-assetpack"));
    }

    public static Path getPFMDirectory() {
        Path p = PFMFileUtil.getGamePath().resolve(PaladinFurnitureMod.MOD_ID);
        return createDirIfNeeded(p);
    }

    public static Path createDirIfNeeded(Path path) {
        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException ignored) {
        } catch (Exception e) {
            PaladinFurnitureMod.GENERAL_LOGGER.error("Failed to create directory {}", e.getMessage());
        }
        return path;
    }

    private static CompletableFuture<Void> future;
    public static CompletableFuture<Void> prepareAsyncDataGen(boolean logOrDebug) {
        PFMDataGenerator dataGen = new PFMDataGenerator(PFMRuntimeResources.getDataPackDirectory(), logOrDebug);
        return future = CompletableFuture.runAsync(() -> {
            isAnyGeneratorRunning = true;
            try {
                dataGen.run();
            } catch (IOException e) {
                dataGen.getLogger().error("Failed to run data generation {}", e.getMessage());
                throw new RuntimeException(e);
            }
            isAnyGeneratorRunning = false;
        });
    }

    public static CompletableFuture<Void> prepareAsyncAssetGen(boolean logOrDebug) {
        PFMAssetGenerator dataGen = new PFMAssetGenerator(PFMRuntimeResources.getAssetPackDirectory(), logOrDebug);
        return future = CompletableFuture.runAsync(() -> {
            isAnyGeneratorRunning = true;
            try {
                dataGen.run();
            } catch (IOException e) {
                dataGen.getLogger().error("Failed to run asset generation {}", e.getMessage());
                throw new RuntimeException(e);
            }
            isAnyGeneratorRunning = false;
        });
    }

    public static boolean ready = false;
    public static void prepareAndRunDataGen(boolean logOrDebug) {
        isAnyGeneratorRunning = true;
        PFMDataGenerator dataGen = new PFMDataGenerator(PFMRuntimeResources.getDataPackDirectory(), logOrDebug);
        try {
            dataGen.run();
        } catch (IOException e) {
            dataGen.getLogger().error("Failed to run data generation {}", e.getMessage());
            throw new RuntimeException(e);
        }
        isAnyGeneratorRunning = false;
    }

    public static void prepareAndRunAssetGen(boolean logOrDebug) {
        isAnyGeneratorRunning = true;
        PFMAssetGenerator dataGen = new PFMAssetGenerator(PFMRuntimeResources.getAssetPackDirectory(), logOrDebug);
        try {
            dataGen.run();
        } catch (IOException e) {
            dataGen.getLogger().error("Failed to run asset generation {}", e.getMessage());
            throw new RuntimeException(e);
        }
        isAnyGeneratorRunning = false;
    }

    public static void runAsyncResourceGen() {
        if (future != null && !future.isDone())
            future.join();
    }

    public static boolean isAnyGeneratorRunning() {
        return isAnyGeneratorRunning;
    }
}
