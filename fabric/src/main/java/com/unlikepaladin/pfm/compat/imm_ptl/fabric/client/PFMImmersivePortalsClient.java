package com.unlikepaladin.pfm.compat.imm_ptl.fabric.client;

import com.unlikepaladin.pfm.client.EntityRenderRegistry;
import com.unlikepaladin.pfm.compat.PFMClientModCompatibility;
import com.unlikepaladin.pfm.compat.PFMModCompatibility;
import com.unlikepaladin.pfm.compat.imm_ptl.fabric.PFMImmersivePortalsImpl;

public class PFMImmersivePortalsClient implements PFMClientModCompatibility {
    private final PFMImmersivePortalsImpl parent;
    public PFMImmersivePortalsClient(PFMImmersivePortalsImpl parent) {
        this.parent = parent;
    }
    @Override
    public PFMModCompatibility getCompatiblity() {
        return parent;
    }

    @Override
    public void registerEntityRenderer() {
        //EntityRenderRegistry.registerEntityRender(PFMImmersivePortalsImpl.MIRROR, PFMMirrorEntityRenderer::new);
    }
}
