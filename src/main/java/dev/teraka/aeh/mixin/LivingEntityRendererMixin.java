package dev.teraka.aeh.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.teraka.aeh.integration.EmfModelCapture;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.IEMFModel;
import traben.entity_model_features.models.parts.EMFModelPartRoot;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    protected M model;

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("TAIL")
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
            return;
        }

        EMFModelPartRoot root = emfModel.emf$getEMFRootModel();
        if (root != null) {
            EmfModelCapture.observe(entity, root);
        }
    }
}
