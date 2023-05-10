import numpy as np
import Utils
def tsp_greedy(coords):
    n = len(coords)
    visited = [False] * n
    path = [0]
    visited[0] = True
    current_city = 0

    while len(path) < n:
        min_distance = np.inf
        nearest_city = None
        for i in range(n):
            if not visited[i]:
                distance = Utils.distanciaEuclidea(coords[current_city], coords[i])
                if distance < min_distance:
                    min_distance = distance
                    nearest_city = i
        visited[nearest_city] = True
        path.append(nearest_city)
        current_city = nearest_city
    return path

if __name__ == "__main__":
    dimension, coords = Utils.leerFicheroTSP("../FicherosTSP/a280.tsp")
    distancias = Utils.inicializarMatrizDistancias(coords)
    path = tsp_greedy(coords)
    Utils.plot_path(coords, path)