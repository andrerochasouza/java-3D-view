
# Java 3D View

O **Java 3D View** é um projeto de estudo em desenvolvimento, com o próposito de criar 
uma interface gráfica em 3D utilizando Java 17, sem bibliotecas externas. A aplicação 
permite que o usuário navegue por cenários 3D com controle total do movimento da câmera
e teclado.

## Demonstração

Insira um gif ou um link de alguma demonstração


## Stack utilizada

**Back-end:** Java 17


## Funcionalidades

- Visualização 3D
- Preview em tempo real 
- Modo tela cheia
- Multiplataforma

## Compilação e geração de instalador (Windows e MacOS)

```bash
  mvn clean package
  jpackage --input .\target\input --dest .\target\output --name Java3DView --main-jar Java3DView.jar --main-class br.com.andre.Main
```
    