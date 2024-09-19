package br.com.andre.graphic;

import br.com.andre.engine.Camera;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Renderer {
    private World world;
    private Camera camera;
    private int screenWidth = 800;
    private int screenHeight = 600;

    public Renderer(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        List<RenderedPolygon> renderedPolygons = new ArrayList<>();

        for (Polygon polygon : world.getPolygons()) {
            // Culling de faces traseiras
            Vector3 normal = calculatePolygonNormal(polygon);
            if (normal.dot(camera.getDirection()) >= 0) {
                continue; // Ignora o polígono se estiver de costas para a câmera
            }

            // Transforma os vértices
            List<Vector3> transformedVertices = new ArrayList<>();
            for (Vector3 vertex : polygon.getVertices()) {
                Vector3 transformed = transformVertex(vertex);
                transformedVertices.add(transformed);
            }

            // Clipping contra o plano próximo
            transformedVertices = clipPolygonAgainstNearPlane(transformedVertices);

            if (transformedVertices.size() >= 3) {
                // Projeta os vértices
                List<Vector3> projectedVertices = new ArrayList<>();
                double averageDepth = 0;

                for (Vector3 vertex : transformedVertices) {
                    Vector3 projected = projectVertex(vertex);
                    projectedVertices.add(projected);
                    averageDepth += vertex.getZ(); // Usar a profundidade original para ordenação
                }

                averageDepth /= transformedVertices.size();
                RenderedPolygon renderedPolygon = new RenderedPolygon(projectedVertices, polygon.getColor(), averageDepth);
                renderedPolygons.add(renderedPolygon);
            }
        }

        // Ordena os polígonos por profundidade
        renderedPolygons.sort(Comparator.comparingDouble(RenderedPolygon::getDepth).reversed());

        // Renderiza os polígonos
        for (RenderedPolygon rp : renderedPolygons) {
            List<Vector3> projectedVertices = rp.getVertices();

            int[] xPoints = new int[projectedVertices.size()];
            int[] yPoints = new int[projectedVertices.size()];

            for (int i = 0; i < projectedVertices.size(); i++) {
                Vector3 v = projectedVertices.get(i);
                xPoints[i] = (int) v.getX();
                yPoints[i] = (int) v.getY();
            }

            g.setColor(rp.getColor());
            g.fillPolygon(xPoints, yPoints, projectedVertices.size());
            g.setColor(Color.BLACK);
            g.drawPolygon(xPoints, yPoints, projectedVertices.size());
        }
    }

    private Vector3 transformVertex(Vector3 vertex) {
        // Translada o vértice em relação à posição da câmera
        Vector3 translated = vertex.subtract(camera.getPosition());

        // Rotaciona o vértice baseado na orientação da câmera
        Vector3 rotated = new Vector3(
                translated.dot(camera.getRight()),
                translated.dot(camera.getUp()),
                translated.dot(camera.getDirection())
        );

        return rotated;
    }

    private Vector3 projectVertex(Vector3 vertex) {
        // Evita divisão por zero
        double z = vertex.getZ();
        if (z == 0) {
            z = 0.0001;
        }

        // Projeção perspectiva simples
        double fov = Math.toRadians(70);
        double f = screenHeight / (2 * Math.tan(fov / 2));

        double x = (vertex.getX() * f) / z + screenWidth / 2;
        double y = (-vertex.getY() * f) / z + screenHeight / 2;

        return new Vector3(x, y, z);
    }

    private List<Vector3> clipPolygonAgainstNearPlane(List<Vector3> vertices) {
        List<Vector3> outputList = new ArrayList<>(vertices);
        double nearPlaneZ = 0.1;

        List<Vector3> inputList;

        // Clipping contra o plano z = nearPlaneZ
        inputList = new ArrayList<>(outputList);
        outputList.clear();

        if (inputList.isEmpty()) {
            return outputList;
        }

        Vector3 S = inputList.get(inputList.size() - 1);

        for (Vector3 E : inputList) {
            boolean E_inside = E.getZ() >= nearPlaneZ;
            boolean S_inside = S.getZ() >= nearPlaneZ;

            if (E_inside) {
                if (!S_inside) {
                    Vector3 intersection = intersectEdgeWithNearPlane(S, E, nearPlaneZ);
                    outputList.add(intersection);
                }
                outputList.add(E);
            } else if (S_inside) {
                Vector3 intersection = intersectEdgeWithNearPlane(S, E, nearPlaneZ);
                outputList.add(intersection);
            }

            S = E;
        }

        return outputList;
    }

    private Vector3 intersectEdgeWithNearPlane(Vector3 S, Vector3 E, double nearPlaneZ) {
        double t = (nearPlaneZ - S.getZ()) / (E.getZ() - S.getZ());
        double x = S.getX() + t * (E.getX() - S.getX());
        double y = S.getY() + t * (E.getY() - S.getY());
        double z = nearPlaneZ;

        return new Vector3(x, y, z);
    }

    private Vector3 calculatePolygonNormal(Polygon polygon) {
        List<Vector3> vertices = polygon.getVertices();

        Vector3 v0 = vertices.get(0);
        Vector3 v1 = vertices.get(1);
        Vector3 v2 = vertices.get(2);

        Vector3 edge1 = v1.subtract(v0);
        Vector3 edge2 = v2.subtract(v0);

        return edge1.cross(edge2).normalize();
    }

    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    private class RenderedPolygon {
        private List<Vector3> vertices;
        private Color color;
        private double depth;

        public RenderedPolygon(List<Vector3> vertices, Color color, double depth) {
            this.vertices = vertices;
            this.color = color;
            this.depth = depth;
        }

        public List<Vector3> getVertices() {
            return vertices;
        }

        public Color getColor() {
            return color;
        }

        public double getDepth() {
            return depth;
        }
    }
}