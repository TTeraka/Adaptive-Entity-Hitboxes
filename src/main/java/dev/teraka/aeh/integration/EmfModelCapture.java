package dev.teraka.aeh.integration;

import dev.teraka.aeh.AdaptiveEntityHitboxes;
import dev.teraka.aeh.bounds.ModelBoundsCalculator;
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
    private static final Set<String> OBSERVED_MODELS = ConcurrentHashMap.newKeySet();

    private EmfModelCapture() {
    }

    public static void observe(LivingEntity entity, EMFModelPartRoot root) {
        if (!root.containsCustomModel || root.currentModelVariant == 0) {
            return;
        }

        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String observationKey = entityTypeId + ":" + root.modelName + ":" + root.currentModelVariant;
        if (!OBSERVED_MODELS.add(observationKey)) {
            return;
        }

        ModelBoundsCalculator.calculate(root, root.currentModelVariant).ifPresentOrElse(
                bounds -> AdaptiveEntityHitboxes.LOGGER.info(
                        "Measured EMF model {} for {} (variant {}): model {}w x {}h x {}d blocks, "
                                + "suggested entity width {}; vanilla entity {}w x {}h",
                        root.modelName,
                        entityTypeId,
                        root.currentModelVariant,
                        format(bounds.width()),
                        format(bounds.height()),
                        format(bounds.depth()),
                        format(bounds.entityWidth()),
                        format(entity.getBbWidth()),
                        format(entity.getBbHeight())
                ),
                () -> AdaptiveEntityHitboxes.LOGGER.warn(
                        "Custom EMF model {} for {} (variant {}) contained no measurable cubes",
                        root.modelName,
                        entityTypeId,
                        root.currentModelVariant
                )
        );
    }

    private static String format(float value) {
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }
}
