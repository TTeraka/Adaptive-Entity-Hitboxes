package dev.teraka.aeh.integration;

import dev.teraka.aeh.AdaptiveEntityHitboxes;
import dev.teraka.aeh.bounds.ModelBounds;
import dev.teraka.aeh.bounds.ModelBoundsCalculator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Measures custom EMF models and applies their cached dimensions to matching rendered entities.
 */
public final class EmfModelCapture {
    private static final float MIN_DIMENSION = 0.1F;
    private static final float MAX_DIMENSION = 16.0F;
    private static final Map<String, ModelBounds> MEASURED_MODELS = new ConcurrentHashMap<>();
    private static final Set<String> EMPTY_MODELS = ConcurrentHashMap.newKeySet();

    private EmfModelCapture() {
    }

    public static void clearCache() {
        MEASURED_MODELS.clear();
        EMPTY_MODELS.clear();
    }

    public static void clear(LivingEntity entity) {
        clearDimensions(entity);
    }

    public static void observe(
            LivingEntity entity,
            EMFModelPartRoot root,
            float rendererScaleX,
            float rendererScaleY,
            float rendererScaleZ
    ) {
        if (!root.containsCustomModel
                || root.currentModelVariant == 0
                || entity instanceof Player
                || entity.isBaby()) {
            clearDimensions(entity);
            return;
        }

        ResourceLocation entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        String observationKey = entityTypeId + ":" + root.modelName + ":" + root.currentModelVariant
                + ":" + Float.floatToIntBits(rendererScaleX)
                + ":" + Float.floatToIntBits(rendererScaleY)
                + ":" + Float.floatToIntBits(rendererScaleZ);
        ModelBounds bounds = MEASURED_MODELS.get(observationKey);

        if (bounds == null) {
            Optional<ModelBounds> measured = ModelBoundsCalculator.calculate(
                    root,
                    root.currentModelVariant,
                    rendererScaleX,
                    rendererScaleY,
                    rendererScaleZ
            );
            if (measured.isEmpty()) {
                if (EMPTY_MODELS.add(observationKey)) {
                    AdaptiveEntityHitboxes.LOGGER.warn(
                            "Custom EMF model {} for {} (variant {}) contained no measurable cubes",
                            root.modelName,
                            entityTypeId,
                            root.currentModelVariant
                    );
                }
                clearDimensions(entity);
                return;
            }

            bounds = measured.get();
            ModelBounds existing = MEASURED_MODELS.putIfAbsent(observationKey, bounds);
            if (existing != null) {
                bounds = existing;
            } else {
                logMeasurement(
                        entity,
                        root,
                        entityTypeId,
                        bounds,
                        rendererScaleX,
                        rendererScaleY,
                        rendererScaleZ
                );
            }
        }

        ((AdaptiveDimensionsHolder) entity).adaptiveHitboxes$setDimensions(
                clamp(bounds.centeredWidth()),
                clamp(bounds.height())
        );
    }

    private static void clearDimensions(LivingEntity entity) {
        ((AdaptiveDimensionsHolder) entity).adaptiveHitboxes$clearDimensions();
    }

    private static void logMeasurement(
            LivingEntity entity,
            EMFModelPartRoot root,
            ResourceLocation entityTypeId,
            ModelBounds bounds,
            float rendererScaleX,
            float rendererScaleY,
            float rendererScaleZ
    ) {
        AdaptiveDimensionsHolder holder = (AdaptiveDimensionsHolder) entity;
        float vanillaWidth = holder.adaptiveHitboxes$getVanillaWidth();
        float vanillaHeight = holder.adaptiveHitboxes$getVanillaHeight();
        float measuredWidth = clamp(bounds.centeredWidth());
        float measuredHeight = clamp(bounds.height());
        float adaptiveWidth = Float.isFinite(vanillaWidth)
                ? Math.max(vanillaWidth, measuredWidth)
                : measuredWidth;
        float adaptiveHeight = Float.isFinite(vanillaHeight)
                ? Math.max(vanillaHeight, measuredHeight)
                : measuredHeight;
        AdaptiveEntityHitboxes.LOGGER.info(
                "Applying EMF model {} to {} (variant {}, renderer scale {}x{}x{}): "
                        + "model {}w x {}h x {}d blocks, centered width {}, "
                        + "adaptive entity {}w x {}h; vanilla entity {}w x {}h",
                root.modelName,
                entityTypeId,
                root.currentModelVariant,
                format(rendererScaleX),
                format(rendererScaleY),
                format(rendererScaleZ),
                format(bounds.width()),
                format(bounds.height()),
                format(bounds.depth()),
                format(measuredWidth),
                format(adaptiveWidth),
                format(adaptiveHeight),
                format(vanillaWidth),
                format(vanillaHeight)
        );
    }

    private static float clamp(float value) {
        return Math.clamp(value, MIN_DIMENSION, MAX_DIMENSION);
    }

    private static String format(float value) {
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }
}
