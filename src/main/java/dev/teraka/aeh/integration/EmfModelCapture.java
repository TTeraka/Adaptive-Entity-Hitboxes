package dev.teraka.aeh.integration;

import dev.teraka.aeh.AdaptiveEntityHitboxes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Records which custom EMF models reach the entity renderer.
 *
 * <p>This is intentionally observational. Hitbox dimensions are not changed until model-space
 * bounds can be calculated and validated independently.</p>
 */
public final class EmfModelCapture {
    private static final Set<ResourceLocation> OBSERVED_ENTITY_TYPES = ConcurrentHashMap.newKeySet();

    private EmfModelCapture() {
    }

    public static void observe(LivingEntity entity, EMFModelPartRoot root) {
        if (!root.containsCustomModel || root.currentModelVariant == 0) {
            return;
        }

        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (OBSERVED_ENTITY_TYPES.add(entityTypeId)) {
            AdaptiveEntityHitboxes.LOGGER.info(
                    "Observed custom EMF model {} for entity type {} (variant {})",
                    root.modelName,
                    entityTypeId,
                    root.currentModelVariant
            );
        }
    }
}
