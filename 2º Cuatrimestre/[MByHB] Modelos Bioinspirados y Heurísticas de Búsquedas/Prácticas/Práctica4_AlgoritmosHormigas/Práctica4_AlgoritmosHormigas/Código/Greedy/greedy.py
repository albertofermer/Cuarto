import numpy as np
import Utils


def tsp_greedy(semilla, problema):
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    n = len(ciudades)
    visited = [False] * n
    path = [0]
    visited[0] = True
    current_city = 0
    numEvaluaciones = 0
    coste = 0
    distancias = Utils.inicializarMatrizDistancias(ciudades)
    while len(path) < n:
        min_distance = np.inf
        nearest_city = None
        for i in range(n):
            if not visited[i]:
                distance = Utils.distanciaEuclidea(ciudades[current_city], ciudades[i])
                if distance < min_distance:
                    min_distance = distance
                    nearest_city = i
        visited[nearest_city] = True
        path.append(nearest_city)
        current_city = nearest_city
    coste = Utils.funcionCoste(distancias, path)
    numEvaluaciones += 1
    return coste, path, numEvaluaciones, [coste]


if __name__ == "__main__":
    Utils.graficasOCH(tsp_greedy)
