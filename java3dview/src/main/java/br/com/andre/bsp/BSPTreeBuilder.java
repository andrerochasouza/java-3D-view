package br.com.andre.bsp;

import br.com.andre.graphic.PolygonGraphic;
import br.com.andre.graphic.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static br.com.andre.util.CalcPolygon.calculatePolygonCenter;
import static br.com.andre.util.CalcPolygon.calculatePolygonNormal;

/**
 * BSPTreeBuilder constrói a árvore BSP a partir dos polígonos.
 */
public class BSPTreeBuilder {

    public static BSPNode buildBSPTree(List<PolygonGraphic> polygonGraphicList) {
        if (polygonGraphicList.isEmpty()) {
            return null;
        }

        PolygonGraphic partitionPolygonGraphic = polygonGraphicList.get(0);
        List<PolygonGraphic> frontList = new ArrayList<>();
        List<PolygonGraphic> backList = new ArrayList<>();

        for (int i = 1; i < polygonGraphicList.size(); i++) {
            PolygonGraphic poly = polygonGraphicList.get(i);
            classifyPolygon(partitionPolygonGraphic, poly, frontList, backList);
        }

        BSPNode node = new BSPNode(Collections.singletonList(partitionPolygonGraphic));
        node.setFrontNode(buildBSPTree(frontList));
        node.setBackNode(buildBSPTree(backList));

        return node;
    }

    private static void classifyPolygon(PolygonGraphic partitionPolygonGraphic, PolygonGraphic poly, List<PolygonGraphic> frontList, List<PolygonGraphic> backList) {
        Vector3 normal = calculatePolygonNormal(partitionPolygonGraphic);
        Vector3 center = calculatePolygonCenter(poly);

        Vector3 partitionCenter = calculatePolygonCenter(partitionPolygonGraphic);
        Vector3 toPoly = center.subtract(partitionCenter);

        if (normal.dot(toPoly) >= 0) {
            frontList.add(poly);
        } else {
            backList.add(poly);
        }
    }
}