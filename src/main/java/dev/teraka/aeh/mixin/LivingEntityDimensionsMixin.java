package dev.teraka.aeh.mixin;

import dev.teraka.aeh.integration.AdaptiveDimensionsHolder;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDimensionsMixin implements AdaptiveDimensionsHolder {
    private static final float DIMENSION_EPSILON = 0.001F;

    @Unique
    private float adaptiveHitboxes$width = Float.NaN;

    @Unique
    private float adaptiveHitboxes$height = Float.NaN;

    @Unique
    private float adaptiveHitboxes$vanillaWidth = Float.NaN;

    @Unique
    private float adaptiveHitboxes$vanillaHeight = Float.NaN;

    @Override
    public void adaptiveHitboxes$setDimensions(float width, float height) {
        if (approximatelyEqual(adaptiveHitboxes$width, width)
                && approximatelyEqual(adaptiveHitboxes$height, height)) {
            return;
        }

        adaptiveHitboxes$width = width;
        adaptiveHitboxes$height = height;
        ((LivingEntity) (Object) this).refreshDimensions();
    }

    @Override
    public void adaptiveHitboxes$clearDimensions() {
        if (Float.isNaN(adaptiveHitboxes$width)) {
            return;
        }

        adaptiveHitboxes$width = Float.NaN;
        adaptiveHitboxes$height = Float.NaN;
        ((LivingEntity) (Object) this).refreshDimensions();
    }

    @Override
    public float adaptiveHitboxes$getVanillaWidth() {
        return adaptiveHitboxes$vanillaWidth;
    }

    @Override
    public float adaptiveHitboxes$getVanillaHeight() {
        return adaptiveHitboxes$vanillaHeight;
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void adaptiveHitboxes$useMeasuredDimensions(
            Pose pose,
            CallbackInfoReturnable<EntityDimensions> callbackInfo
    ) {
        EntityDimensions original = callbackInfo.getReturnValue();
        adaptiveHitboxes$vanillaWidth = original.width();
        adaptiveHitboxes$vanillaHeight = original.height();

        if (Float.isNaN(adaptiveHitboxes$width)) {
            return;
        }

        float width = Math.max(original.width(), adaptiveHitboxes$width);
        float height = Math.max(original.height(), adaptiveHitboxes$height);
        float eyeHeight = Math.min(original.eyeHeight(), height);
        callbackInfo.setReturnValue(new EntityDimensions(
                width,
                height,
                eyeHeight,
                original.attachments(),
                original.fixed()
        ));
    }

    @Unique
    private static boolean approximatelyEqual(float first, float second) {
        return Math.abs(first - second) <= DIMENSION_EPSILON;
    }
}
