package br.com.andre.graphic;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * A classe World representa o ambiente 3D contendo todos os polígonos a serem renderizados.
 */
public class World {
    private List<Polygon> polygons;
    private Map<String, Material> materials;

    public World(String path) {
        polygons = new ArrayList<>();
        materials = new HashMap<>();
        if (Objects.nonNull(path) && !path.isEmpty()) {
            loadFromOBJ(path);
        } else {
            initializePolygonsTest();
        }
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

    /**
     * Inicializa polígonos padrão para testes (um cubo).
     */
    private void initializePolygonsTest() {
        // Cria um cubo simples
        Vector3 v1 = new Vector3(-1, -1, -1);
        Vector3 v2 = new Vector3(1, -1, -1);
        Vector3 v3 = new Vector3(1, 1, -1);
        Vector3 v4 = new Vector3(-1, 1, -1);
        Vector3 v5 = new Vector3(-1, -1, 1);
        Vector3 v6 = new Vector3(1, -1, 1);
        Vector3 v7 = new Vector3(1, 1, 1);
        Vector3 v8 = new Vector3(-1, 1, 1);

        // Frente
        polygons.add(new Polygon(Color.BLUE, true, v1, v2, v3, v4));
        // Trás
        polygons.add(new Polygon(Color.RED, true, v5, v6, v7, v8));
        // Esquerda
        polygons.add(new Polygon(Color.GREEN, true, v1, v4, v8, v5));
        // Direita
        polygons.add(new Polygon(Color.GRAY, true, v2, v6, v7, v3));
        // Topo (definir cullBackFace como false para renderizar ambos os lados)
        polygons.add(new Polygon(Color.WHITE, false, v4, v3, v7, v8));
        // Base (definir cullBackFace como false)
        polygons.add(new Polygon(Color.PINK, false, v1, v5, v6, v2));
    }

    /**
     * Obtém a lista de polígonos no mundo.
     *
     * @return a lista de polígonos
     */
    public List<Polygon> getPolygons() {
        return polygons;
    }
}