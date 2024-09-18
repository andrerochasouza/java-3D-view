package br.com.andre.engine;

import br.com.andre.graphic.Polygon;
import br.com.andre.graphic.Vector3;

import java.awt.*;
import java.util.ArrayList;
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

        for (Polygon polygon : world.getPolygons()) {
            List<Vector3> transformedVertices = new ArrayList<>();
            for (Vector3 vertex : polygon.getVertices()) {
                Vector3 transformed = transformVertex(vertex);
                transformedVertices.add(transformed);
            }

            if (transformedVertices.size() >= 3) {
                int[] xPoints = new int[transformedVertices.size()];
                int[] yPoints = new int[transformedVertices.size()];

                for (int i = 0; i < transformedVertices.size(); i++) {
                    Vector3 v = transformedVertices.get(i);
                    xPoints[i] = (int) (v.getX() * screenWidth / 2 + screenWidth / 2);
                    yPoints[i] = (int) (-v.getY() * screenHeight / 2 + screenHeight / 2);
                }

                g.setColor(polygon.getColor());
                g.fillPolygon(xPoints, yPoints, transformedVertices.size());
                g.setColor(Color.BLACK);
                g.drawPolygon(xPoints, yPoints, transformedVertices.size());
            }
        }
    }

    private Vector3 transformVertex(Vector3 vertex) {
        // Transladar o vértice em relação à posição da câmera
        Vector3 translated = vertex.subtract(camera.getPosition());

        // Rotacionar o vértice baseado na orientação da câmera
        Vector3 rotated = new Vector3(
                translated.dot(camera.getRight()),
                translated.dot(camera.getUp()),
                translated.dot(camera.getDirection().multiply(-1))
        );

        // Aplicar uma projeção perspectiva simples
        double fov = Math.toRadians(90);
        double aspect = (double) screenWidth / screenHeight;
        double near = 0.1;
        double far = 100;

        double f = 1 / Math.tan(fov / 2);
        double rangeInv = 1 / (near - far);

        double x = rotated.getX() * (f / aspect);
        double y = rotated.getY() * f;
        double z = (rotated.getZ() * ((near + far) * rangeInv)) + (2 * near * far * rangeInv);

        // Normalizar as coordenadas
        if (z != 0) {
            x /= -z;
            y /= -z;
        }

        return new Vector3(x, y, z);
    }

    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }
}