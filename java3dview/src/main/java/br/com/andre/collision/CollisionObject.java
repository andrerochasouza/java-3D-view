package br.com.andre.collision;

import br.com.andre.graphic.Vector3;
import java.util.List;

/**
 * Representa um objeto de colisão no jogo.
 */
public class CollisionObject {
    private String name; // Nome do objeto para identificação
    private AABB boundingBox;

    public CollisionObject(String name, Vector3 min, Vector3 max) {
        this.name = name;
        this.boundingBox = new AABB(min, max);
    }

    // Construtor adicional opcional
    public CollisionObject(String name, List<Vector3> vertices) {
        this.name = name;
        Vector3 min = new Vector3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vector3 max = new Vector3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

        for (Vector3 vertex : vertices) {
            min = new Vector3(
                    Math.min(min.getX(), vertex.getX()),
                    Math.min(min.getY(), vertex.getY()),
                    Math.min(min.getZ(), vertex.getZ())
            );
            max = new Vector3(
                    Math.max(max.getX(), vertex.getX()),
                    Math.max(max.getY(), vertex.getY()),
                    Math.max(max.getZ(), vertex.getZ())
            );
        }

        this.boundingBox = new AABB(min, max);
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public String getName() {
        return name;
    }

    /**
     * Representa uma Axis-Aligned Bounding Box.
     */
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