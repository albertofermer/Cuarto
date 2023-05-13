import Utils
import numpy as np
import time
def SistemaHormigas(semilla, problema):
    # Lectura de las coordenadas del problema:
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    dimension = int(dimension)
    # Inicializar matrices
    feromonas = np.ones((dimension,dimension))
    heuristica = Utils.inicializarMatrizDistancias(ciudades)
    visitados = np.zeros((dimension,dimension))
    solucionHormigas = np.ones((Utils.numeroHormigas,dimension), dtype=int) * 1
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    MejorHormiga = solucionHormigas[0]
    # print(solucionHormigas)
    start = time.time()
    while (time.time() - start) < 10:
        # Para k=1 hasta NumeroHormigas hacer:
        for k in range(Utils.numeroHormigas):
            solucionHormigas[k][0] = 0  # Nodo Inicial
            coste[k] = Utils.funcionCoste(heuristica, solucionHormigas[k]) # Calcula el Coste
            if Utils.funcionCoste(heuristica, MejorHormiga) < Utils.funcionCoste(heuristica, solucionHormigas[k]):
                MejorHormiga = solucionHormigas[k].copy()

            # Inicializa la matriz de nodos visitados
            visitados = inicializarMatrizVisitados(dimension)

            # Para cada paso en la construcción de la solución:
            for paso in range(1, dimension):
                # Actualiza la matriz de feromonas
                feromonas = actualizarFeromonas(feromonas, solucionHormigas[k], heuristica, dimension)
                # Actualiza la matriz de nodos visitados
                visitados = actualizarMatrizVisitados(visitados, solucionHormigas[k], dimension, paso)
                # Calcula la probabilidad de selección de cada ciudad
                prob_seleccion = calcularProbSeleccion(feromonas, heuristica, visitados, solucionHormigas[k], dimension, paso)
                # Selecciona la próxima ciudad a visitar
                siguiente_ciudad = seleccionarCiudad(prob_seleccion)
                # Añade la ciudad seleccionada a la solución
                solucionHormigas[k][paso] = siguiente_ciudad
            # Actualiza el coste de la solución
            coste[k] = Utils.funcionCoste(heuristica, solucionHormigas[k])
    # Devuelve la mejor solución encontrada
    return MejorHormiga, Utils.funcionCoste(heuristica, MejorHormiga)

def inicializarMatrizVisitados(dimension):
    """Función para inicializar la matriz de nodos visitados."""
    visitados = np.zeros((dimension, dimension))
    return visitados

def actualizarFeromonas(feromonas, solucionHormiga, heuristica, dimension, Q=10):
    """Función para actualizar la matriz de feromonas."""
    n = len(solucionHormiga)
    delta_feromonas = np.zeros((n, n))
    for i in range(n - 1):
        a = solucionHormiga[i]
        b = solucionHormiga[i + 1]
        delta_feromonas[a][b] += Q / heuristica[a][b]
        delta_feromonas[b][a] += Q / heuristica[a][b]
    # print(delta_feromonas.shape)
    # Actualiza las feromonas según el parámetro rho
    feromonas = (1 - Utils.EVAPORACION) * feromonas + delta_feromonas
    return feromonas

def actualizarMatrizVisitados(visitados, solucionHormiga, dimension, paso):
    """Función para actualizar la matriz de nodos visitados."""
    visitados[solucionHormiga[paso - 1]][solucionHormiga[paso]] = 1
    visitados[solucionHormiga[paso]][solucionHormiga[paso - 1]] = 1
    return visitados

def calcularProbSeleccion(feromonas, heuristica, visitados, solucionHormiga, dimension, paso, alpha=1, beta=5):
    """Función para calcular la probabilidad de selección de cada ciudad."""
    prob_seleccion = np.zeros(dimension)
    denom = 0
    for j in range(dimension):
        if visitados[solucionHormiga[paso - 1]][j] == 0:
            denom += (feromonas[solucionHormiga[paso - 1]][j] ** alpha) * ((1 / heuristica[solucionHormiga[paso - 1]][j]) ** beta)
    for j in range(dimension):
        if visitados[solucionHormiga[paso - 1]][j] == 0:
            numer = (feromonas[solucionHormiga[paso - 1]][j] ** alpha) * ((1 / heuristica[solucionHormiga[paso - 1]][j]) ** beta)
            prob_seleccion[j] = numer / (denom + 1)
    return prob_seleccion

def seleccionarCiudad(prob_seleccion):
    """Función para seleccionar la próxima ciudad a visitar."""
    r = np.random.uniform()
    acumulado = 0
    for j in range(len(prob_seleccion)):
        acumulado += prob_seleccion[j]
        if acumulado >= r:
            return j
    return j  # En caso de que no se seleccione ninguna ciudad, se retorna la última

if __name__ == "__main__":
    print(SistemaHormigas(123456, "ch130"))

