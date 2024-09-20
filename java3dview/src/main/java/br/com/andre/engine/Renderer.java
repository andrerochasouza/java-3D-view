package br.com.andre.engine;

import br.com.andre.bsp.BSPNode;
import br.com.andre.graphic.PolygonGraphic;
import br.com.andre.graphic.Vector3;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static br.com.andre.util.CalcPolygon.calculatePolygonCenter;
import static br.com.andre.util.CalcPolygon.calculatePolygonNormal;

/**
 * A classe Renderer lida com a renderização do mundo 3D na tela 2D.
 */
public class Renderer {

    private final World world;
    private final Camera camera;
    private int screenWidth = 800;
    private int screenHeight = 600;

    public Renderer(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        renderBSPNode(g, world.getBSPTree(), camera.getPosition());
    }

    private void renderBSPNode(Graphics g, BSPNode node, Vector3 cameraPosition) {
        if (node == null) return;

        PolygonGraphic partitionPolygonGraphic = node.getPartitionPolygon();
        if (partitionPolygonGraphic == null) return;

        Vector3 normal = calculatePolygonNormal(partitionPolygonGraphic);
        Vector3 partitionCenter = calculatePolygonCenter(partitionPolygonGraphic);
        Vector3 toCamera = cameraPosition.subtract(partitionCenter);

        boolean inFront = normal.dot(toCamera) >= 0;

        if (inFront) {
            renderBSPNode(g, node.getBackNode(), cameraPosition);
            renderPolygons(g, node.getPolygons());
            renderBSPNode(g, node.getFrontNode(), cameraPosition);
        } else {
            renderBSPNode(g, node.getFrontNode(), cameraPosition);
            renderPolygons(g, node.getPolygons());
            renderBSPNode(g, node.getBackNode(), cameraPosition);
        }
    }

    private void renderPolygons(Graphics g, List<PolygonGraphic> polygonsToRender) {
        for (PolygonGraphic polygonGraphic : polygonsToRender) {
            if (!isPolygonVisible(polygonGraphic)) continue;

            List<Vector3> transformedVertices = transformVertices(polygonGraphic);
            List<Vector3> clippedVertices = clipPolygonAgainstNearPlane(transformedVertices);

            if (clippedVertices.size() >= 3) {
                List<Vector3> projectedVertices = projectVertices(clippedVertices);
                drawPolygon(g, projectedVertices, polygonGraphic.getColor());
            }
        }
    }

    private boolean isPolygonVisible(PolygonGraphic polygonGraphic) {
        Vector3 normal = calculatePolygonNormal(polygonGraphic);
        Vector3 polygonCenter = calculatePolygonCenter(polygonGraphic);
        Vector3 viewVector = polygonCenter.subtract(camera.getPosition()).normalize();

        return !polygonGraphic.isCullBackFace() || normal.dot(viewVector) >= 0;
    }

    private List<Vector3> transformVertices(PolygonGraphic polygonGraphic) {
        List<Vector3> transformedVertices = new ArrayList<>();
        for (Vector3 vertex : polygonGraphic.getVertices()) {
            transformedVertices.add(transformVertex(vertex));
        }
        return transformedVertices;
    }

    private Vector3 transformVertex(Vector3 vertex) {
        Vector3 translated = vertex.subtract(camera.getPosition());
        return new Vector3(
                translated.dot(camera.getRight()),
                translated.dot(camera.getUp()),
                translated.dot(camera.getDirection())
        );
    }

    private List<Vector3> projectVertices(List<Vector3> vertices) {
        List<Vector3> projectedVertices = new ArrayList<>();
        for (Vector3 vertex : vertices) {
            projectedVertices.add(projectVertex(vertex));
        }
        return projectedVertices;
    }

    private Vector3 projectVertex(Vector3 vertex) {
        double z = vertex.getZ() == 0 ? 0.0001 : vertex.getZ();
        double fov = Math.toRadians(70);
        double f = screenHeight / (2 * Math.tan(fov / 2));

        double x = (vertex.getX() * f) / z + screenWidth / 2;
        double y = (-vertex.getY() * f) / z + screenHeight / 2;

        return new Vector3(x, y, z);
    }

    private void drawPolygon(Graphics g, List<Vector3> projectedVertices, Color color) {
        int[] xPoints = new int[projectedVertices.size()];
        int[] yPoints = new int[projectedVertices.size()];

        for (int i = 0; i < projectedVertices.size(); i++) {
            Vector3 v = projectedVertices.get(i);
            xPoints[i] = (int) v.getX();
            yPoints[i] = (int) v.getY();
        }

        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, projectedVertices.size());
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoints, yPoints, projectedVertices.size());
    }

    private List<Vector3> clipPolygonAgainstNearPlane(List<Vector3> vertices) {
        List<Vector3> outputList = new ArrayList<>();
        double nearPlaneZ = 0.1;

        if (vertices.isEmpty()) return outputList;

        Vector3 S = vertices.get(vertices.size() - 1);

        for (Vector3 E : vertices) {
            boolean E_inside = E.getZ() >= nearPlaneZ;
            boolean S_inside = S.getZ() >= nearPlaneZ;

            if (E_inside) {
                if (!S_inside) {
                    outputList.add(intersectEdgeWithNearPlane(S, E, nearPlaneZ));
                }
                outputList.add(E);
            } else if (S_inside) {
                outputList.add(intersectEdgeWithNearPlane(S, E, nearPlaneZ));
            }
            S = E;
        }

        return outputList;
    }

    private Vector3 intersectEdgeWithNearPlane(Vector3 S, Vector3 E, double nearPlaneZ) {
        double t = (nearPlaneZ - S.getZ()) / (E.getZ() - S.getZ());
        double x = S.getX() + t * (E.getX() - S.getX());
        double y = S.getY() + t * (E.getY() - S.getY());

        return new Vector3(x, y, nearPlaneZ);
    }
}