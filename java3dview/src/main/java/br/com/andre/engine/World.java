package br.com.andre.engine;

import br.com.andre.graphic.Material;
import br.com.andre.graphic.PolygonGraphic;
import br.com.andre.graphic.Vector3;
import br.com.andre.object.CollisionObject;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import static br.com.andre.util.CalcPolygon.calculatePolygonCenter;
import static br.com.andre.util.CalcPolygon.calculatePolygonNormal;

/**
 * A classe World representa o ambiente 3D contendo todos os polígonos a serem renderizados.
 */
public class World {
    private List<PolygonGraphic> polygonGraphics;
    private Map<String, Material> materials;
    private BSPNode bspTree;
    private List<CollisionObject> collisionObjects;

    /**
     * Construtor que cria um novo mundo, carregando polígonos de um arquivo ou inicializando polígonos padrão.
     *
     * @param path o caminho para o arquivo OBJ a ser carregado
     */
    public World(String path) throws IllegalArgumentException {
        polygonGraphics = new ArrayList<>();
        materials = new HashMap<>();
        collisionObjects = new ArrayList<>();

        if (Objects.isNull(path) || path.isEmpty()) {
            throw new IllegalArgumentException("O caminho do recurso não pode ser nulo ou vazio.");
        }

        loadFromOBJ(path); // Carrega o arquivo OBJ
        buildBSPTree(); // Constrói a BSP Tree após carregar os polígonos
    }

    /**
     * Obtém a raiz da BSP Tree construída.
     *
     * @return o nó raiz da BSP Tree
     */
    public BSPNode getBSPTree() {
        return bspTree;
    }

    /**
     * Constrói a BSP Tree a partir dos polígonos carregados.
     */
    public void buildBSPTree() {
        bspTree = buildBSPNode(polygonGraphics);
    }

    public List<CollisionObject> getCollisionObjects() {
        return collisionObjects;
    }

    private BSPNode buildBSPNode(List<PolygonGraphic> polygonGraphicList) {
        if (polygonGraphicList.isEmpty()) {
            return null;
        }

        // Escolhe um polígono como partição (aqui escolhemos o primeiro)
        PolygonGraphic partitionPolygonGraphic = polygonGraphicList.get(0);
        List<PolygonGraphic> frontList = new ArrayList<>();
        List<PolygonGraphic> backList = new ArrayList<>();

        for (int i = 1; i < polygonGraphicList.size(); i++) {
            PolygonGraphic poly = polygonGraphicList.get(i);
            // Classifica o polígono em frente, atrás ou dividindo o plano
            classifyPolygon(partitionPolygonGraphic, poly, frontList, backList);
        }

        BSPNode node = new BSPNode(Collections.singletonList(partitionPolygonGraphic));
        node.setFrontNode(buildBSPNode(frontList));
        node.setBackNode(buildBSPNode(backList));

        return node;
    }

    /**
     * Carrega polígonos de um arquivo OBJ.
     *
     * @param path o caminho para a pasta resource
     */
    private void loadFromOBJ(String path) {
        InputStream objStream = getClass().getResourceAsStream("/" + path);

        if (objStream == null) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + path);
        }

        List<Vector3> vertices = new ArrayList<>();
        String currentMaterialName = null;
        String currentGroupName = null; // Para rastrear o grupo atual

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Remove comentários inline
                if (line.contains("#")) {
                    line = line.split("#")[0].trim();
                }
                if (line.isEmpty()) {
                    continue; // Ignora linhas vazias após remover comentários
                }

                if (line.startsWith("mtllib ")) {
                    String mtlFileName = line.substring(7).trim();
                    String basePath = path.contains("/") ? path.substring(0, path.lastIndexOf('/') + 1) : "";
                    loadMaterials(basePath + mtlFileName);
                } else if (line.startsWith("usemtl ")) {
                    currentMaterialName = line.substring(7).trim();
                } else if (line.startsWith("g ")) {
                    currentGroupName = line.substring(2).trim();
                } else if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    vertices.add(new Vector3(x, y, z));
                } else if (line.startsWith("f ")) {
                    String[] parts = line.split("\\s+");
                    List<Integer> faceIndices = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        String[] vertexData = parts[i].split("/");
                        int vertexIndex = Integer.parseInt(vertexData[0]) - 1;
                        faceIndices.add(vertexIndex);
                    }
                    Vector3[] faceVertices = new Vector3[faceIndices.size()];
                    for (int i = 0; i < faceIndices.size(); i++) {
                        faceVertices[i] = vertices.get(faceIndices.get(i));
                    }
                    // Obtém a cor do material atual
                    Color color = Color.LIGHT_GRAY;
                    boolean cullBackFace = true; // Valor padrão

                    if (currentMaterialName != null && materials.containsKey(currentMaterialName)) {
                        Material material = materials.get(currentMaterialName);
                        color = material.getDiffuseColor();
                        cullBackFace = material.isCullBackFace();
                    }
                    // Cria o polígono com o nome do grupo
                    PolygonGraphic polygon = new PolygonGraphic(currentGroupName, color, cullBackFace, faceVertices);
                    polygonGraphics.add(polygon);

                    // Se o polígono pertence a um grupo específico (por exemplo, "Wall" ou "Floor"), cria um objeto de colisão
                    if ("Wall".equalsIgnoreCase(currentGroupName) || "Floor".equalsIgnoreCase(currentGroupName)) {
                        collisionObjects.add(new CollisionObject(Arrays.asList(faceVertices)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMaterials(String mtlFileName) {
        InputStream mtlStream = getClass().getClassLoader().getResourceAsStream(mtlFileName);

        if (mtlStream == null) {
            System.err.println("Arquivo MTL não encontrado: " + mtlFileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mtlStream))) {
            String line;
            Material currentMaterial = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("#")) {
                    line = line.split("#")[0].trim();
                }
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("newmtl ")) {
                    String materialName = line.substring(7).trim();
                    currentMaterial = new Material(materialName, Color.LIGHT_GRAY, false);
                    materials.put(materialName, currentMaterial);
                } else if (line.startsWith("Kd ")) {
                    if (currentMaterial != null) {
                        String[] parts = line.split("\\s+");
                        float r = Float.parseFloat(parts[1]);
                        float g = Float.parseFloat(parts[2]);
                        float b = Float.parseFloat(parts[3]);
                        Color diffuseColor = new Color(r, g, b);
                        currentMaterial = new Material(currentMaterial.getName(), diffuseColor, false);
                        materials.put(currentMaterial.getName(), currentMaterial);
                    }
                }

                if (line.startsWith("cullBackFace ")) {
                    if (currentMaterial != null) {
                        String value = line.substring(13).trim();
                        boolean cullBackFace = Boolean.parseBoolean(value);
                        currentMaterial.setCullBackFace(cullBackFace);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void classifyPolygon(PolygonGraphic partitionPolygonGraphic, PolygonGraphic poly, List<PolygonGraphic> frontList, List<PolygonGraphic> backList) {
        // Implementa a lógica para classificar o polígono em frente, atrás ou dividindo o plano
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