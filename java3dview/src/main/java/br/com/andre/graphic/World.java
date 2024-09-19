package br.com.andre.graphic;

import br.com.andre.bsp.BSPNode;

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
    private List<Polygon> polygons;
    private Map<String, Material> materials;
    private BSPNode bspTree;

    /**
     * Construtor que cria um novo mundo, carregando polígonos de um arquivo ou inicializando polígonos padrão.
     *
     * @param path o caminho para o arquivo OBJ a ser carregado
     */
    public World(String path) throws IllegalArgumentException {
        polygons = new ArrayList<>();
        materials = new HashMap<>();

        if (path == null || path.isEmpty()) {
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
        bspTree = buildBSPNode(polygons);
    }

    private BSPNode buildBSPNode(List<Polygon> polygonList) {
        if (polygonList.isEmpty()) {
            return null;
        }

        // Escolhe um polígono como partição (aqui escolhemos o primeiro)
        Polygon partitionPolygon = polygonList.get(0);

        List<Polygon> frontList = new ArrayList<>();
        List<Polygon> backList = new ArrayList<>();

        for (int i = 1; i < polygonList.size(); i++) {
            Polygon poly = polygonList.get(i);
            // Classifica o polígono em frente, atrás ou dividindo o plano
            classifyPolygon(partitionPolygon, poly, frontList, backList);
        }

        BSPNode node = new BSPNode(Collections.singletonList(partitionPolygon));
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
        InputStream objStream = getClass().getClassLoader().getResourceAsStream(path);

        if (objStream == null) {
            System.err.println("Arquivo não encontrado: " + path);
            return;
        }

        List<Vector3> vertices = new ArrayList<>();
        String currentMaterialName = null;

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
                    loadMaterials(mtlFileName);
                } else if (line.startsWith("usemtl ")) {
                    currentMaterialName = line.substring(7).trim();
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
                    // Obtém a cor e a flag de culling do material atual
                    Color color = Color.LIGHT_GRAY;
                    boolean cullBackFace = true; // Valor padrão

                    if (currentMaterialName != null && materials.containsKey(currentMaterialName)) {
                        Material material = materials.get(currentMaterialName);
                        color = material.getDiffuseColor();
                        cullBackFace = material.isCullBackFace(); // Supondo que você adicionou este atributo na classe Material
                    }

                    polygons.add(new Polygon(color, cullBackFace, faceVertices));
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
                // Remove comentários inline
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
                        // Atualiza a cor difusa do material atual
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

    private void classifyPolygon(Polygon partitionPolygon, Polygon poly, List<Polygon> frontList, List<Polygon> backList) {
        // Implementa a lógica para classificar o polígono em frente, atrás ou dividindo o plano
        Vector3 normal = calculatePolygonNormal(partitionPolygon);
        Vector3 center = calculatePolygonCenter(poly);

        Vector3 partitionCenter = calculatePolygonCenter(partitionPolygon);
        Vector3 toPoly = center.subtract(partitionCenter);

        if (normal.dot(toPoly) >= 0) {
            frontList.add(poly);
        } else {
            backList.add(poly);
        }
    }
}