package dev.teraka.aeh.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import traben.entity_model_features.models.parts.EMFModelPartVanilla;

import java.util.Set;

@Mixin(value = EMFModelPartVanilla.class, remap = false)
public interface EmfModelPartVanillaAccessor {
    @Accessor("hideInTheseStates")
    Set<Integer> adaptiveHitboxes$getHiddenVariants();
}
