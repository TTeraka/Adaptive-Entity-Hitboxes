package dev.teraka.aeh;

import dev.teraka.aeh.integration.EmfModelCapture;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entry point for Adaptive Entity Hitboxes.
 *
 * <p>The project intentionally starts without hitbox-changing behavior. EMF model discovery,
 * bounds calculation, and entity dimension updates will be added after the integration points
 * have been verified against the target EMF version.</p>
 */
public final class AdaptiveEntityHitboxes implements ClientModInitializer {
    public static final String MOD_ID = "adaptive_entity_hitboxes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return ResourceLocation.fromNamespaceAndPath(MOD_ID, "model_bounds");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager resourceManager) {
                        EmfModelCapture.clearCache();
                        LOGGER.info("Cleared cached EMF model bounds after resource reload");
                    }
                }
        );
        LOGGER.info("Adaptive Entity Hitboxes loaded");
    }
}
