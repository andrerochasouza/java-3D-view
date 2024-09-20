package br.com.andre.graphic;

import br.com.andre.collision.CollisionObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OBJLoader carrega modelos OBJ e materiais associados.
 */
public class OBJLoader {

    public static void loadOBJ(String path, List<PolygonGraphic> polygonGraphics, Map<String, Material> materials, List<CollisionObject> collisionObjects) {
        InputStream objStream = OBJLoader.class.getResourceAsStream("/" + path);

        if (objStream == null) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + path);
        }

        List<Vector3> vertices = new ArrayList<>();
        String currentMaterialName = null;
        String currentGroupName = null;

        // Mapas para rastrear min e max por grupo
        Map<String, Vector3> groupMins = new HashMap<>();
        Map<String, Vector3> groupMaxs = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(objStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.contains("#")) {
                    line = line.split("#")[0].trim();
                }
                if (line.isEmpty()) continue;

                if (line.startsWith("mtllib ")) {
                    String mtlFileName = line.substring(7).trim();
                    String basePath = path.contains("/") ? path.substring(0, path.lastIndexOf('/') + 1) : "";
                    loadMaterials(basePath + mtlFileName, materials);
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
                    Color color = Color.LIGHT_GRAY;
                    boolean cullBackFace = true;

                    if (currentMaterialName != null && materials.containsKey(currentMaterialName)) {
                        Material material = materials.get(currentMaterialName);
                        color = material.getDiffuseColor();
                        cullBackFace = material.isCullBackFace();
                    }
                    PolygonGraphic polygon = new PolygonGraphic(currentGroupName, color, cullBackFace, faceVertices);
                    polygonGraphics.add(polygon);

                    // Verifica se o grupo atual é "Wall" ou "Floor"
                    if ("Wall".equalsIgnoreCase(currentGroupName) || "Floor".equalsIgnoreCase(currentGroupName)) {
                        // Inicializa os valores min e max para o grupo se ainda não estiverem
                        if (!groupMins.containsKey(currentGroupName)) {
                            groupMins.put(currentGroupName, new Vector3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE));
                            groupMaxs.put(currentGroupName, new Vector3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE));
                        }

                        Vector3 currentMin = groupMins.get(currentGroupName);
                        Vector3 currentMax = groupMaxs.get(currentGroupName);

                        // Atualiza os valores min e max com os vértices do polígono atual
                        for (Vector3 vertex : faceVertices) {
                            if (vertex.getX() < currentMin.getX()) currentMin = currentMin.setX(vertex.getX());
                            if (vertex.getY() < currentMin.getY()) currentMin = currentMin.setY(vertex.getY());
                            if (vertex.getZ() < currentMin.getZ()) currentMin = currentMin.setZ(vertex.getZ());

                            if (vertex.getX() > currentMax.getX()) currentMax = currentMax.setX(vertex.getX());
                            if (vertex.getY() > currentMax.getY()) currentMax = currentMax.setY(vertex.getY());
                            if (vertex.getZ() > currentMax.getZ()) currentMax = currentMax.setZ(vertex.getZ());
                        }

                        // Atualiza os mapas com os novos valores min e max
                        groupMins.put(currentGroupName, currentMin);
                        groupMaxs.put(currentGroupName, currentMax);
                    }
                }
            }

            // Após processar todas as linhas, cria os CollisionObjects com os limites calculados
            for (String groupName : groupMins.keySet()) {
                Vector3 min = groupMins.get(groupName);
                Vector3 max = groupMaxs.get(groupName);
                collisionObjects.add(new CollisionObject(groupName, min, max));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadMaterials(String mtlPath, Map<String, Material> materials) {
        InputStream mtlStream = OBJLoader.class.getResourceAsStream("/" + mtlPath);
        if (mtlStream == null) {
            throw new IllegalArgumentException("Arquivo MTL não encontrado: " + mtlPath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mtlStream))) {
            String line;
            String currentMaterialName = null;
            Color diffuseColor = Color.LIGHT_GRAY;
            boolean cullBackFace = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("newmtl ")) {
                    if (currentMaterialName != null) {
                        materials.put(currentMaterialName, new Material(currentMaterialName, diffuseColor, cullBackFace));
                    }
                    currentMaterialName = line.substring(7).trim();
                    diffuseColor = Color.LIGHT_GRAY;
                    cullBackFace = true;
                } else if (line.startsWith("Kd ")) {
                    String[] parts = line.split("\\s+");
                    float r = Float.parseFloat(parts[1]);
                    float g = Float.parseFloat(parts[2]);
                    float b = Float.parseFloat(parts[3]);
                    diffuseColor = new Color(r, g, b);
                } else if (line.startsWith("illum ")) {
                    // Por exemplo, para definir se deve ou não culling
                    String illum = line.substring(6).trim();
                    cullBackFace = !"0".equals(illum);
                }
            }

            // Adiciona o último material
            if (currentMaterialName != null) {
                materials.put(currentMaterialName, new Material(currentMaterialName, diffuseColor, cullBackFace));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}