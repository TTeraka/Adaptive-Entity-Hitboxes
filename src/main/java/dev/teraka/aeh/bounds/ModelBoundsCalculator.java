package dev.teraka.aeh.bounds;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.teraka.aeh.mixin.accessor.EmfModelPartVanillaAccessor;
import dev.teraka.aeh.mixin.accessor.ModelPartAccessor;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;

import java.util.List;
import java.util.Optional;

public final class ModelBoundsCalculator {
    private static final float MODEL_UNITS_PER_BLOCK = 16.0F;

    private ModelBoundsCalculator() {
    }

    public static Optional<ModelBounds> calculate(
            ModelPart root,
            int variant,
            float rendererScaleX,
            float rendererScaleY,
            float rendererScaleZ
    ) {
        List<ModelPart> parts = root.getAllParts().toList();
        List<PartPose> renderedPoses = parts.stream().map(ModelPart::storePose).toList();

        try {
            MutableBounds bounds = new MutableBounds();

            // Keep a stable neutral baseline, but also include the pose EMF prepared for this
            // render. Some JEMs use animations to position otherwise separate pieces such as a
            // humanoid head, so measuring only reset poses can omit visible geometry.
            parts.forEach(ModelPart::resetPose);
            measure(root, variant, rendererScaleX, rendererScaleY, rendererScaleZ, bounds);

            for (int index = 0; index < parts.size(); index++) {
                parts.get(index).loadPose(renderedPoses.get(index));
            }
            measure(root, variant, rendererScaleX, rendererScaleY, rendererScaleZ, bounds);

            return bounds.toImmutable();
        } finally {
            for (int index = 0; index < parts.size(); index++) {
                parts.get(index).loadPose(renderedPoses.get(index));
            }
        }
    }

    private static void measure(
            ModelPart root,
            int variant,
            float rendererScaleX,
            float rendererScaleY,
            float rendererScaleZ,
            MutableBounds bounds
    ) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(rendererScaleX, rendererScaleY, rendererScaleZ);
        visit(root, variant, poseStack, bounds);
    }

    private static void visit(ModelPart part, int variant, PoseStack poseStack, MutableBounds bounds) {
        if (!part.visible || isHiddenForVariant(part, variant)) {
            return;
        }

        poseStack.pushPose();
        part.translateAndRotate(poseStack);

        ModelPartAccessor accessor = (ModelPartAccessor) (Object) part;
        if (!part.skipDraw) {
            Matrix4f transform = poseStack.last().pose();
            for (ModelPart.Cube cube : accessor.adaptiveHitboxes$getCubes()) {
                includeCube(cube, transform, bounds);
            }
        }

        for (ModelPart child : accessor.adaptiveHitboxes$getChildren().values()) {
            visit(child, variant, poseStack, bounds);
        }

        poseStack.popPose();
    }

    private static boolean isHiddenForVariant(ModelPart part, int variant) {
        return part instanceof EMFModelPartVanilla
                && ((EmfModelPartVanillaAccessor) (Object) part)
                .adaptiveHitboxes$getHiddenVariants()
                .contains(variant);
    }

    private static void includeCube(ModelPart.Cube cube, Matrix4f transform, MutableBounds bounds) {
        float[] xValues = {cube.minX, cube.maxX};
        float[] yValues = {cube.minY, cube.maxY};
        float[] zValues = {cube.minZ, cube.maxZ};

        for (float x : xValues) {
            for (float y : yValues) {
                for (float z : zValues) {
                    Vector3f point = new Vector3f(
                            x / MODEL_UNITS_PER_BLOCK,
                            y / MODEL_UNITS_PER_BLOCK,
                            z / MODEL_UNITS_PER_BLOCK
                    );
                    transform.transformPosition(point);
                    bounds.include(point.x, point.y, point.z);
                }
            }
        }
    }

    private static final class MutableBounds {
        private float minX = Float.POSITIVE_INFINITY;
        private float minY = Float.POSITIVE_INFINITY;
        private float minZ = Float.POSITIVE_INFINITY;
        private float maxX = Float.NEGATIVE_INFINITY;
        private float maxY = Float.NEGATIVE_INFINITY;
        private float maxZ = Float.NEGATIVE_INFINITY;

        private void include(float x, float y, float z) {
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        private Optional<ModelBounds> toImmutable() {
            if (!Float.isFinite(minX)) {
                return Optional.empty();
            }
            return Optional.of(new ModelBounds(minX, minY, minZ, maxX, maxY, maxZ));
        }
    }
}
