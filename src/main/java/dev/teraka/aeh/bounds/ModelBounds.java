package dev.teraka.aeh.bounds;

public record ModelBounds(
        float minX,
        float minY,
        float minZ,
        float maxX,
        float maxY,
        float maxZ
) {
    public float width() {
        return maxX - minX;
    }

    /**
     * Minecraft centers an entity bounding box on its position, so an asymmetric model needs
     * enough width to reach the furthest measured point on either side of that origin.
     */
    public float centeredWidth() {
        return 2.0F * Math.max(Math.abs(minX), Math.abs(maxX));
    }

    public float height() {
        return maxY - minY;
    }

    public float depth() {
        return maxZ - minZ;
    }

}
