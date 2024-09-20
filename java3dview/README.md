
# Java 3D View

O **Java 3D View** é um projeto de estudo em desenvolvimento, com o propósito de criar uma interface gráfica em 3D utilizando Java 17, sem o uso de bibliotecas externas. A aplicação simula visualizações em ambientes 3D, permitindo que o usuário navegue por cenários criados diretamente no código com controle de câmera e renderização básica.

## Stack utilizada

- **Back-end:** Java 17
- **Interface gráfica:** Swing (JPanel)
- **Processamento de gráficos 3D:** Estruturas próprias como BSPTree, `Polygon` e `Renderer`

## Funcionalidades

- Renderização de cenas 3D em tempo real
- Controle de câmera para navegação em cenários 3D
- Sistema de visualização baseado em polígonos
- Organização espacial dos polígonos utilizando uma árvore BSP (Binary Space Partitioning)
- Multiplataforma (compilável para Windows e MacOS)
- Taxa de quadros configurada (60 FPS)

## Estrutura do projeto

### Principais classes

1. **`Game.java`**:
    - Gerencia o loop principal do jogo, atualizando a lógica de movimentação da câmera e renderizando a cena a cada frame.
    - Controla o framerate do jogo (60 FPS) e gerencia o input do usuário para movimentação da câmera e controle da visão com o mouse.

2. **`Camera.java`**:
    - Implementa os movimentos de translação e rotação no espaço 3D.

3. **`PolygonGraphic.java`**:
    - Representa um polígono gráfico, usado na construção de objetos 3D.
    - Contém métodos para calcular o normal do polígono, necessário para determinar sua orientação espacial.

4. **`BSPNode.java`**:
    - Utiliza uma estrutura de árvore BSP para organizar os polígonos e otimizar a renderização da cena 3D, ordenando-os em relação à câmera.
    - Cada `BSPNode` contém um polígono e referências para subnós da árvore, permitindo a organização espacial eficiente.

5. **`Renderer.java`**:
    - Responsável pela lógica de renderização dos polígonos na tela.
    - Faz a projeção dos objetos 3D para o plano 2D da tela e cuida da conversão de coordenadas tridimensionais.

6. **`World.java`**:
    - Representa o mundo do jogo, contendo os objetos e os polígonos a serem renderizados.
    - Constrói a cena inicial e organiza a disposição dos objetos e polígonos que a compõem.

7. **`Vector3.java`**:
    - Representa um vetor tridimensional usado em cálculos geométricos para posicionamento, direção e normais de polígonos.

### Fluxo de execução

1. **Inicialização**: O programa é iniciado a partir da classe `Game`, que configura a janela e o painel de renderização.
2. **Loop principal**: O loop principal controla a atualização e renderização da cena, chamando métodos de atualização da câmera e de desenho dos polígonos na tela.
3. **Input do usuário**: O input do teclado e do mouse controla a movimentação da câmera e a visão do jogador, que são processados pela classe `Camera`.
4. **Renderização**: Os polígonos são organizados na árvore BSP (`BSPNode`), projetados e desenhados na tela pelo `Renderer`.

## Instalação e uso

### Requisitos
- Java 17 ou superior
- Maven

### Compilação e execução

Para compilar o projeto, execute o seguinte comando:

```bash
mvn clean package
```

Em seguida, execute o programa com:

```bash
java -jar target/Java3DView.jar
```

### Geração de instalador (Windows e MacOS)

Para gerar um instalador utilizando o `jpackage`, execute o seguinte comando:

```bash
jpackage --type msi --input ./target/input --dest ./target/output --name Java3DView --main-jar Java3DView.jar --main-class br.com.andre.Main --win-dir-chooser --win-shortcut
```

No caso do MacOS, use o parâmetro `--type dmg`:

```bash
jpackage --type dmg --input ./target/input --dest ./target/output --name Java3DView --main-jar Java3DView.jar --main-class br.com.andre.Main
```

## Licença

[MIT License](LICENSE)