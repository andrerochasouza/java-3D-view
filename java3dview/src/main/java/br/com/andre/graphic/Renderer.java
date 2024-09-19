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
    private final World world;
    private final Camera camera;
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
        // Define o fundo da tela como preto e preenche todo o espaço disponível
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Lista para armazenar os polígonos que serão renderizados após transformação e projeção
        List<RenderedPolygon> renderedPolygons = new ArrayList<>();

        // Itera sobre cada polígono presente no mundo
        for (Polygon polygon : world.getPolygons()) {
            // Calcula o vetor normal do polígono
            Vector3 normal = calculatePolygonNormal(polygon);
            // Calcula o centro do polígono
            Vector3 polygonCenter = calculatePolygonCenter(polygon);
            // Calcula o vetor de visão (do centro do polígono para a câmera)
            Vector3 viewVector = polygonCenter.subtract(camera.getPosition()).normalize();

            // Aplica o back-face culling se estiver habilitado para este polígono
            if (polygon.isCullBackFace()) {
                if (normal.dot(viewVector) < 0) {
                    continue; // Ignora o polígono se estiver de costas para a câmera
                }
            }

            // Transforma os vértices para o espaço da câmera
            List<Vector3> transformedVertices = new ArrayList<>();
            for (Vector3 vertex : polygon.getVertices()) {
                Vector3 transformed = transformVertex(vertex);
                transformedVertices.add(transformed);
            }

            // Realiza o clipping do polígono contra o plano próximo
            transformedVertices = clipPolygonAgainstNearPlane(transformedVertices);

            // Verifica se o polígono ainda possui pelo menos 3 vértices após o clipping
            if (transformedVertices.size() >= 3) {
                // Projeta os vértices no plano 2D da tela
                List<Vector3> projectedVertices = new ArrayList<>();
                double averageDepth = 0;

                for (Vector3 vertex : transformedVertices) {
                    Vector3 projected = projectVertex(vertex);
                    projectedVertices.add(projected);
                    averageDepth += vertex.getZ(); // Acumula a profundidade Z para ordenação posterior
                }

                // Calcula a profundidade média do polígono
                averageDepth /= transformedVertices.size();

                // Cria um novo objeto RenderedPolygon com os vértices projetados, cor e profundidade média
                RenderedPolygon renderedPolygon = new RenderedPolygon(projectedVertices, polygon.getColor(), averageDepth);
                renderedPolygons.add(renderedPolygon);
            }
        }

        // Ordena os polígonos por profundidade média (Z-buffer), do mais distante para o mais próximo
        renderedPolygons.sort(Comparator.comparingDouble(RenderedPolygon::getDepth).reversed());

        // Itera sobre cada polígono renderizado para desenhá-los na tela
        for (RenderedPolygon rp : renderedPolygons) {
            List<Vector3> projectedVertices = rp.getVertices();

            // Arrays para armazenar as coordenadas X e Y dos vértices projetados
            int[] xPoints = new int[projectedVertices.size()];
            int[] yPoints = new int[projectedVertices.size()];

            // Extrai as coordenadas X e Y de cada vértice projetado
            for (int i = 0; i < projectedVertices.size(); i++) {
                Vector3 v = projectedVertices.get(i);
                xPoints[i] = (int) v.getX();
                yPoints[i] = (int) v.getY();
            }

            // Define a cor do polígono e o preenche na tela
            g.setColor(rp.getColor());
            g.fillPolygon(xPoints, yPoints, projectedVertices.size());

            // Desenha o contorno do polígono com a cor preta
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

        return new Vector3(x, y, nearPlaneZ);
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
     * Calcula o centro de um polígono.
     *
     * @param polygon o polígono para o qual calcular o centro
     * @return o vetor representando o centro do polígono
     */
    private Vector3 calculatePolygonCenter(Polygon polygon) {
        List<Vector3> vertices = polygon.getVertices();
        double x = 0, y = 0, z = 0;
        int numVertices = vertices.size();

        for (Vector3 vertex : vertices) {
            x += vertex.getX();
            y += vertex.getY();
            z += vertex.getZ();
        }

        return new Vector3(x / numVertices, y / numVertices, z / numVertices);
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
}