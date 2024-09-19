package br.com.andre.graphic;

import br.com.andre.engine.Camera;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A classe Renderer lida com a renderização do mundo 3D na tela 2D.
 */
public class Renderer {
    private World world;
    private Camera camera;
    private int screenWidth = 800;
    private int screenHeight = 600;

    /**
     * Construtor que cria um Renderer com o mundo e a câmera especificados.
     *
     * @param world  o mundo a ser renderizado
     * @param camera a câmera usada para renderização
     */
    public Renderer(World world, Camera camera) {
        this.world = world;
        this.camera = camera;
    }

    /**
     * Renderiza o mundo no contexto Graphics fornecido.
     *
     * @param g o contexto Graphics no qual desenhar
     */
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
                    averageDepth += vertex.getZ(); // Usa a profundidade original para ordenação
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

    /**
     * Transforma um vértice do espaço do mundo para o espaço da câmera.
     *
     * @param vertex o vértice em coordenadas do mundo
     * @return o vértice transformado em coordenadas da câmera
     */
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

    /**
     * Projeta um ponto 3D na tela 2D.
     *
     * @param vertex o vértice em coordenadas da câmera
     * @return o ponto 2D projetado
     */
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

    /**
     * Recorta um polígono contra o plano próximo para lidar com casos onde vértices estão atrás da câmera.
     *
     * @param vertices a lista de vértices representando o polígono
     * @return a lista de vértices após o recorte
     */
    private List<Vector3> clipPolygonAgainstNearPlane(List<Vector3> vertices) {
        List<Vector3> outputList = new ArrayList<>();
        double nearPlaneZ = 0.1;

        if (vertices.isEmpty()) {
            return outputList;
        }

        Vector3 S = vertices.get(vertices.size() - 1);

        for (Vector3 E : vertices) {
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

    /**
     * Calcula o ponto de interseção de uma aresta com o plano próximo.
     *
     * @param S          o vértice inicial da aresta
     * @param E          o vértice final da aresta
     * @param nearPlaneZ o valor Z do plano próximo
     * @return o ponto de interseção no plano próximo
     */
    private Vector3 intersectEdgeWithNearPlane(Vector3 S, Vector3 E, double nearPlaneZ) {
        double t = (nearPlaneZ - S.getZ()) / (E.getZ() - S.getZ());
        double x = S.getX() + t * (E.getX() - S.getX());
        double y = S.getY() + t * (E.getY() - S.getY());
        double z = nearPlaneZ;

        return new Vector3(x, y, z);
    }

    /**
     * Calcula o vetor normal de um polígono.
     *
     * @param polygon o polígono para o qual calcular a normal
     * @return o vetor normal normalizado do polígono
     */
    private Vector3 calculatePolygonNormal(Polygon polygon) {
        List<Vector3> vertices = polygon.getVertices();

        Vector3 v0 = vertices.get(0);
        Vector3 v1 = vertices.get(1);
        Vector3 v2 = vertices.get(2);

        Vector3 edge1 = v1.subtract(v0);
        Vector3 edge2 = v2.subtract(v0);

        return edge1.cross(edge2).normalize();
    }

    /**
     * Define o tamanho da tela para renderização.
     *
     * @param width  a largura da tela
     * @param height a altura da tela
     */
    public void setScreenSize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    /**
     * Classe interna representando um polígono pronto para ser renderizado, contendo vértices projetados.
     */
    private class RenderedPolygon {
        private List<Vector3> vertices;
        private Color color;
        private double depth;

        /**
         * Construtor que cria um RenderedPolygon com os vértices, cor e profundidade fornecidos.
         *
         * @param vertices a lista de vértices projetados
         * @param color    a cor do polígono
         * @param depth    a profundidade média do polígono
         */
        public RenderedPolygon(List<Vector3> vertices, Color color, double depth) {
            this.vertices = vertices;
            this.color = color;
            this.depth = depth;
        }

        /**
         * Obtém os vértices projetados do polígono.
         *
         * @return a lista de vértices
         */
        public List<Vector3> getVertices() {
            return vertices;
        }

        /**
         * Obtém a cor do polígono.
         *
         * @return a cor
         */
        public Color getColor() {
            return color;
        }

        /**
         * Obtém a profundidade média do polígono.
         *
         * @return a profundidade
         */
        public double getDepth() {
            return depth;
        }
    }
}