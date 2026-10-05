package dev.teraka.aeh;

import net.fabricmc.api.ClientModInitializer;
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
        LOGGER.info("Adaptive Entity Hitboxes loaded; EMF integration is not active yet.");
    }
}
