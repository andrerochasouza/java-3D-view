package br.com.andre.collision;

import br.com.andre.graphic.Vector3;

/**
 * Representa um objeto de colisão no jogo.
 */
public class CollisionObject {
    private String name; // Nome do objeto para identificação
    private Vector3 min;
    private Vector3 max;

    public CollisionObject(String name, Vector3 min, Vector3 max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public Vector3 getMin() {
        return min;
    }

    public Vector3 getMax() {
        return max;
    }
}