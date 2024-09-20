package br.com.andre.engine;

import br.com.andre.bsp.BSPNode;
import br.com.andre.bsp.BSPTreeBuilder;
import br.com.andre.graphic.Material;
import br.com.andre.graphic.OBJLoader;
import br.com.andre.graphic.PolygonGraphic;
import br.com.andre.collision.CollisionObject;

import java.util.*;

/**
 * A classe World representa o ambiente 3D contendo todos os polígonos a serem renderizados.
 */
public class World {

    private List<PolygonGraphic> polygonGraphics;
    private Map<String, Material> materials;
    private BSPNode bspTree;
    private List<CollisionObject> collisionObjects;

    public World(String path) {
        polygonGraphics = new ArrayList<>();
        materials = new HashMap<>();
        collisionObjects = new ArrayList<>();

        if (Objects.isNull(path) || path.isEmpty()) {
            throw new IllegalArgumentException("O caminho do recurso não pode ser nulo ou vazio.");
        }

        OBJLoader.loadOBJ(path, polygonGraphics, materials, collisionObjects);
        bspTree = BSPTreeBuilder.buildBSPTree(polygonGraphics);
    }

    public BSPNode getBSPTree() {
        return bspTree;
    }

    public List<CollisionObject> getCollisionObjects() {
        return collisionObjects;
    }
}