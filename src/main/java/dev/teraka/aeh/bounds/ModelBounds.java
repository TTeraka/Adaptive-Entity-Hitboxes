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

    public float height() {
        return maxY - minY;
    }

    public float depth() {
        return maxZ - minZ;
    }

    public float entityWidth() {
        return Math.max(width(), depth());
    }
}
