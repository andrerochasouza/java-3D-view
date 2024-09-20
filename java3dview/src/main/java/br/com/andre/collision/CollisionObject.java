package br.com.andre.collision;

import br.com.andre.graphic.Vector3;
import java.util.List;

public class CollisionObject {
    private List<Vector3> vertices;
    private AABB boundingBox;

    public CollisionObject(List<Vector3> vertices) {
        this.vertices = vertices;
        this.boundingBox = calculateBoundingBox(vertices);
    }

    public List<Vector3> getVertices() {
        return vertices;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    private AABB calculateBoundingBox(List<Vector3> vertices) {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Vector3 vertex : vertices) {
            double x = vertex.getX();
            double y = vertex.getY();
            double z = vertex.getZ();

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }

        return new AABB(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

    public static class AABB {
        private Vector3 min;
        private Vector3 max;

        public AABB(Vector3 min, Vector3 max) {
            this.min = min;
            this.max = max;
        }

        public Vector3 getMin() {
            return min;
        }

        public Vector3 getMax() {
            return max;
        }
    }
}