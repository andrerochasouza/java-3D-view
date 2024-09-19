package br.com.andre.engine;

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
     * Renderiza o mundo no contexto Graphics fornecido.
     *
     * @param g o contexto Graphics no qual desenhar
     */
    public void render(Graphics g) {
        // Limpa a tela com a cor preta
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, screenWidth, screenHeight);

        // Renderiza a cena usando a BSP Tree
        renderBSPNode(g, world.getBSPTree(), camera.getPosition());
    }

    private void renderBSPNode(Graphics g, BSPNode node, Vector3 cameraPosition) {
        if (node == null) {
            return;
        }

        PolygonGraphic partitionPolygonGraphic = node.getPartitionPolygon();
        if (partitionPolygonGraphic == null) {
            return;
        }

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
            // Aplica o back-face culling se estiver habilitado para este polígono
            Vector3 normal = calculatePolygonNormal(polygonGraphic);
            Vector3 polygonCenter = calculatePolygonCenter(polygonGraphic);
            Vector3 viewVector = polygonCenter.subtract(camera.getPosition()).normalize();

            if (polygonGraphic.isCullBackFace()) {
                if (normal.dot(viewVector) < 0) {
                    continue; // Ignora o polígono se estiver de costas para a câmera
                }
            }

            // Transforma os vértices para o espaço da câmera
            List<Vector3> transformedVertices = new ArrayList<>();
            for (Vector3 vertex : polygonGraphic.getVertices()) {
                Vector3 transformed = transformVertex(vertex);
                transformedVertices.add(transformed);
            }

            // Realiza o clipping do polígono contra o plano próximo
            transformedVertices = clipPolygonAgainstNearPlane(transformedVertices);

            // Verifica se o polígono ainda possui pelo menos 3 vértices após o clipping
            if (transformedVertices.size() >= 3) {
                // Projeta os vértices no plano 2D da tela
                List<Vector3> projectedVertices = new ArrayList<>();

                for (Vector3 vertex : transformedVertices) {
                    Vector3 projected = projectVertex(vertex);
                    projectedVertices.add(projected);
                }

                // Desenha o polígono
                int[] xPoints = new int[projectedVertices.size()];
                int[] yPoints = new int[projectedVertices.size()];

                for (int i = 0; i < projectedVertices.size(); i++) {
                    Vector3 v = projectedVertices.get(i);
                    xPoints[i] = (int) v.getX();
                    yPoints[i] = (int) v.getY();
                }

                g.setColor(polygonGraphic.getColor());
                g.fillPolygon(xPoints, yPoints, projectedVertices.size());

                // Desenha o contorno do polígono com a cor preta
                g.setColor(Color.BLACK);
                g.drawPolygon(xPoints, yPoints, projectedVertices.size());
            }
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
}