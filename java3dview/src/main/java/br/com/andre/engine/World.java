package br.com.andre.engine;

import br.com.andre.bsp.BSPNode;
import br.com.andre.bsp.BSPTreeBuilder;
import br.com.andre.collision.CollisionObject;
import br.com.andre.collision.collider_object.AABBCollider;
import br.com.andre.graphic.Material;
import br.com.andre.graphic.OBJLoader;
import br.com.andre.graphic.PolygonGraphic;
import br.com.andre.graphic.Vector3;
import br.com.andre.physic.PhysicsBody;
import br.com.andre.physic.StaticBody;

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

    public List<PolygonGraphic> getPolygonGraphics() {
        return polygonGraphics;
    }

    public void setPolygonGraphics(List<PolygonGraphic> polygonGraphics) {
        this.polygonGraphics = polygonGraphics;
    }

    public Map<String, Material> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<String, Material> materials) {
        this.materials = materials;
    }

    public BSPNode getBspTree() {
        return bspTree;
    }

    public void setBspTree(BSPNode bspTree) {
        this.bspTree = bspTree;
    }

    public void setCollisionObjects(List<CollisionObject> collisionObjects) {
        this.collisionObjects = collisionObjects;
    }

    /**
     * Converte os CollisionObjects em StaticBodies com AABBColliders.
     *
     * @return Uma lista de PhysicsBody estáticos.
     */
    public List<PhysicsBody> getStaticPhysicsBodies() {
        List<PhysicsBody> staticBodies = new ArrayList<>();
        for (CollisionObject collisionObject : collisionObjects) {
            Vector3 min = collisionObject.getMin();
            Vector3 max = collisionObject.getMax();
            AABBCollider aabb = new AABBCollider(min, max);
            StaticBody staticBody = new StaticBody(aabb);
            staticBodies.add(staticBody);
        }
        return staticBodies;
    }
}