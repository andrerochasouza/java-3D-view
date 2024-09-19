package br.com.andre.bsp;

import br.com.andre.graphic.Polygon;

import java.util.List;

/**
 * Classe que representa um nó da BSP Tree.
 */
public class BSPNode {
    private Polygon partitionPolygon;
    private BSPNode frontNode;
    private BSPNode backNode;
    private List<Polygon> polygons;

    public BSPNode(List<Polygon> polygons) {
        this.polygons = polygons;
        if (!polygons.isEmpty()) {
            this.partitionPolygon = polygons.get(0);
        }
    }

    public Polygon getPartitionPolygon() {
        return partitionPolygon;
    }

    public void setPartitionPolygon(Polygon partitionPolygon) {
        this.partitionPolygon = partitionPolygon;
    }

    public BSPNode getFrontNode() {
        return frontNode;
    }

    public void setFrontNode(BSPNode frontNode) {
        this.frontNode = frontNode;
    }

    public BSPNode getBackNode() {
        return backNode;
    }

    public void setBackNode(BSPNode backNode) {
        this.backNode = backNode;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }
}