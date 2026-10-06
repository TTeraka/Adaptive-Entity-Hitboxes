package dev.teraka.aeh.integration;

public interface AdaptiveDimensionsHolder {
    void adaptiveHitboxes$setDimensions(float width, float height);

    void adaptiveHitboxes$clearDimensions();

    float adaptiveHitboxes$getVanillaWidth();

    float adaptiveHitboxes$getVanillaHeight();
}
