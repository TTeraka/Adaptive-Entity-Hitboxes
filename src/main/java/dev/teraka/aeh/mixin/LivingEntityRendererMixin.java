package dev.teraka.aeh.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.teraka.aeh.integration.EmfModelCapture;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.joml.Vector3f;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    protected M model;

    @Unique
    private final Vector3f adaptiveHitboxes$baseScale = new Vector3f(1.0F);

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD")
    )
    private void adaptiveHitboxes$captureBaseScale(
            T entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CallbackInfo callbackInfo
    ) {
        poseStack.last().pose().getScale(adaptiveHitboxes$baseScale);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"
            )
    )
    private void adaptiveHitboxes$captureEmfModel(
            T entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            CallbackInfo callbackInfo
    ) {
        if (!(model instanceof IEMFModel emfModel) || !emfModel.emf$isEMFModel()) {
            EmfModelCapture.clear(entity);
            return;
        }

        EMFModelPartRoot root = emfModel.emf$getEMFRootModel();
        if (root != null) {
            Vector3f renderedScale = poseStack.last().pose().getScale(new Vector3f());
            EmfModelCapture.observe(
                    entity,
                    root,
                    relativeScale(renderedScale.x, adaptiveHitboxes$baseScale.x),
                    relativeScale(renderedScale.y, adaptiveHitboxes$baseScale.y),
                    relativeScale(renderedScale.z, adaptiveHitboxes$baseScale.z)
            );
        } else {
            EmfModelCapture.clear(entity);
        }
    }

    @Unique
    private static float relativeScale(float rendered, float base) {
        return Float.isFinite(rendered) && Float.isFinite(base) && base > 0.0F
                ? Math.abs(rendered / base)
                : 1.0F;
    }
}
