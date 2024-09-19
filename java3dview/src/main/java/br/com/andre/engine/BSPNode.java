package br.com.andre.engine;

import br.com.andre.graphic.PolygonGraphic;

import java.util.List;

/**
 * Classe que representa um nó da BSP Tree.
 */
public class BSPNode {
    private PolygonGraphic partitionPolygonGraphic;
    private BSPNode frontNode;
    private BSPNode backNode;
    private List<PolygonGraphic> polygonGraphics;

    public BSPNode(List<PolygonGraphic> polygonGraphics) {
        this.polygonGraphics = polygonGraphics;
        if (!polygonGraphics.isEmpty()) {
            this.partitionPolygonGraphic = polygonGraphics.get(0);
        }
    }

    public PolygonGraphic getPartitionPolygon() {
        return partitionPolygonGraphic;
    }

    public void setPartitionPolygon(PolygonGraphic partitionPolygonGraphic) {
        this.partitionPolygonGraphic = partitionPolygonGraphic;
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

    public List<PolygonGraphic> getPolygons() {
        return polygonGraphics;
    }
}