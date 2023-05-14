import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import math

# Constantes
semillas = [123456, 789456, 456123, 369852, 852321]
# Sistema de Hormigas
numeroHormigas = 30
alpha = 1
beta = 2

# Sistema de Hormigas Elitista:
numeroHormigasElitista = 15
EVAPORACION = 0.1

def inicializarMatrizDistancias(coords):
    n = len(coords)
    dist_matriz = np.zeros((n, n))

    for i in range(n):
        for j in range(n):
            if i == j:
                dist_matriz[i][j] = 0.0
            else:
                dist_matriz[i][j] = distanciaEuclidea(coords[i], coords[j])

    return dist_matriz

def funcionCoste(dist_matrix, path):
    """
    Calcula el coste de un camino en función de una matriz de distancias y un camino.

    Args:
    - dist_matrix: matriz de distancias de tamaño nxn
    - path: camino representado como una lista de n ciudades

    Returns:
    - cost: el coste del camino
    """
    n = len(path)
    cost = 0
    for i in range(n-1):
        cost += dist_matrix[path[i]][path[i+1]]
    cost += dist_matrix[path[-1]][path[0]]  # añadimos la distancia de vuelta a la ciudad 0
    return cost


def TiempoParada(problema):
    if problema == "ch130":
        return 3 * 60
    if problema == "a280":
        return 8 * 60

def cantidadInicialFeromonas(size_problema, L):
    return 1 / (size_problema * L)


def distanciaEuclidea(coord1, coord2):
    return round(math.sqrt(math.pow((coord1[0] - coord2[0]), 2) +
                           math.pow((coord1[1] - coord2[1]), 2)))


def leerFicheroTSP(nombre):
    with open(nombre, 'r') as f:
        doc = f.read()

    # Obtiene la dimension del problema
    dimension = doc.split("\n")[3].split(": ")[1]

    node_coord_section = doc.split('NODE_COORD_SECTION')[1].strip()
    coords = []
    for line in node_coord_section.split('\n'):
        line = line.strip()
        if line == '':
            continue
        if line != "EOF":
            parts = line.split()
            coords.append((float(parts[1]), float(parts[2])))
    return dimension, coords


def representaPuntos(coords, title):
    """
    Esta función recibe una lista de tuplas de coordenadas (coord_x, coord_y) y representa los puntos en un gráfico utilizando Matplotlib.
    """
    x_coords = [coord[0] for coord in coords]
    y_coords = [coord[1] for coord in coords]

    fig, ax = plt.subplots()
    ax.scatter(x_coords, y_coords, s=1, c='black')
    plt.title(f"Puntos del problema {title}")
    plt.xlabel("Coordenada X")
    plt.ylabel("Coordenada Y")
    plt.show()


def plot_path(coords, path, titulo):
    # Dibujar los puntos
    x = [coord[0] for coord in coords]
    y = [coord[1] for coord in coords]
    plt.scatter(x, y, s=2, c="black")

    # Dibujar el camino
    path_x = [coords[i][0] for i in path]
    path_y = [coords[i][1] for i in path]
    path_x.append(coords[path[0]][0])
    path_y.append(coords[path[0]][1])
    plt.plot(path_x, path_y, color='red')

    # Configurar la gráfica
    plt.title(titulo)
    plt.xlabel("Coordenada x")
    plt.ylabel("Coordenada y")
    plt.show()

def reglaTransicion(L, tau, eta):
    print()


if __name__ == "__main__":
    print()