package com.unlikepaladin.pfm.client.neoforge;

import com.unlikepaladin.pfm.PaladinFurnitureMod;
import net.irisshaders.iris.api.v0.IrisApi;

import java.lang.reflect.Field;

public class PaladinFurnitureModClientImpl {
    public static boolean areShadersOn() {
        if (PaladinFurnitureMod.getModList().contains("oculus") || PaladinFurnitureMod.getModList().contains("iris"))
            return IrisApi.getInstance().isShaderPackInUse();
        try
            {
                Field shaderPackLoadedField = Class.forName("net.optifine.shaders.Shaders").getField("shaderPackLoaded");
                Class<?> shaderClass = shaderPackLoadedField.getType();
                if (shaderClass == boolean.class)
                    return shaderPackLoadedField.getBoolean(null);
            }
            catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
            {
                if (e instanceof IllegalAccessException)
                    PaladinFurnitureMod.GENERAL_LOGGER.info("Couldn't access shaders field: " + e);
                return false;
            }
        return false;
    }
}
