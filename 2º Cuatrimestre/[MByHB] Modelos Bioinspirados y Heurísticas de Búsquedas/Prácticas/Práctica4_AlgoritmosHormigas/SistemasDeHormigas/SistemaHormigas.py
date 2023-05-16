import random
import sys

import Utils
import importlib
import numpy as np
import time

sys.path.append('..\Greedy')
import greedy


def SistemaHormigas(semilla, problema):
    np.random.seed(semilla)
    random.seed(semilla)
    # Lectura de las coordenadas del problema:
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    dimension = int(dimension)
    pathGreedy = greedy.tsp_greedy(ciudades)

    # Inicializar matrices
    distancias = Utils.inicializarMatrizDistancias(ciudades)
    coste_greedy = Utils.funcionCoste(distancias, pathGreedy)
    feromonas = np.ones((dimension, dimension)) * (1 / (dimension * coste_greedy))
    # print(dimension)
    # Sembrar Feromonas del camino del Greedy
    sembrarFeromonas(feromonas, pathGreedy)
    # print(feromonas)
    solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    mejorActual = coste[0].copy()
    mejorGlobal = coste[0].copy()
    mejorHormiga = solucionHormigas[0]
    mejorHormigaActual = solucionHormigas[0]
    MejorCosteIteracion = []
    numeroEvaluaciones = 0
    # Comienza el algoritmo
    start = time.time()
    t = 0
    while (time.time() - start) < Utils.TiempoParada(problema) or t < 400:
        t += 1
        solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1
        for hormiga in range(len(solucionHormigas)):  # Para cada hormiga
            solucionHormigas[hormiga][0] = 0  # El primer nodo es el 0
            # print(f"Hormiga {hormiga}:{solucionHormigas[hormiga]}")
            for nodo in range(1, dimension):  # Para cada nodo
                probabilidades = transicion(feromonas, distancias, solucionHormigas[hormiga])
                # Elegir un nodo en funcion de las probabilidades calculadas
                solucionHormigas[hormiga][nodo] = elegirNodo(probabilidades)
            coste[hormiga] = Utils.funcionCoste(distancias, solucionHormigas[hormiga])
            numeroEvaluaciones += 1
            if coste[hormiga] < mejorActual:
                mejorActual = coste[hormiga]
                mejorHormigaActual = solucionHormigas[hormiga].copy()

        actualizarFeromonas(distancias, solucionHormigas, feromonas)
        numeroEvaluaciones += Utils.numeroHormigas

        if mejorActual < mejorGlobal:
            mejorGlobal = mejorActual
            mejorHormiga = mejorHormigaActual.copy()
            # print(mejorHormiga)
            # print(f"Coste: {mejorGlobal}")
        MejorCosteIteracion.append(mejorActual.copy())
    # print(f"Iteraciones: {t}")
    # print(f"Tiempo: {(time.time() - start)}")
    return mejorGlobal, mejorHormiga, numeroEvaluaciones, MejorCosteIteracion


def actualizarFeromonas(distancias, soluciones, feromonas):
    num_hormigas = len(soluciones)
    num_arcos = len(soluciones[0])

    delta = np.zeros((num_arcos, num_arcos))
    costes_hormigas = [Utils.funcionCoste(distancias, hormiga) for hormiga in soluciones]

    for hormiga in range(num_hormigas):
        for r in range(num_arcos):
            for s in range(num_arcos):
                if existeArco(soluciones[hormiga], r, s):
                    aux = Utils.FACTOR_COSTE / costes_hormigas[hormiga]
                    delta[r][s] += aux

    for r in range(num_arcos):
        for s in range(num_arcos):
            feromonas[r][s] = (1 - Utils.EVAPORACION) * feromonas[r][s] + delta[r][s]


def existeArco(solucion, r, s):
    solucion_np = np.array(solucion)
    indices = np.where((solucion_np[:-1] == r) & (solucion_np[1:] == s))[0]
    return len(indices) > 0


def transicion(feromonas, distancias, solucionHormiga):
    indices_validos = np.where(solucionHormiga != -1)[0]
    ultimoNodoVisitado = solucionHormiga[indices_validos[-1]]

    r = ultimoNodoVisitado
    # Obtiene la diferencia entre todos los nodos y los que se encuentran en solucionhormiga
    u_not_in_solucionHormiga = np.setdiff1d(np.arange(len(distancias)), solucionHormiga)
    feromonas_r = feromonas[r]
    # Realiza 1/distancia
    distancias_r_inv = np.reciprocal(distancias[r])
    sumaTotal = np.sum((feromonas_r[u_not_in_solucionHormiga] ** Utils.alpha) * (
                distancias_r_inv[u_not_in_solucionHormiga] ** Utils.beta))

    if sumaTotal <= 0:
        sumaTotal = 1e-10

    probabilidades = np.zeros(len(distancias))
    probabilidades[u_not_in_solucionHormiga] = (feromonas_r[u_not_in_solucionHormiga] ** Utils.alpha) * (
                distancias_r_inv[u_not_in_solucionHormiga] ** Utils.beta) / sumaTotal
    # print(probabilidades)
    return probabilidades


def sembrarFeromonas(feromonas, path):
    path_np = np.array(path)
    feromonas[path_np[:-1], path_np[1:]] *= Utils.FACTOR_SEMBRADO


def elegirNodo(probabilidades):
    nodosNoVisitados = np.where(probabilidades != 0)[0]
    ciudad = random.choices(nodosNoVisitados, weights=probabilidades[np.nonzero(probabilidades)], k=1)[0]
    return ciudad


if __name__ == "__main__":
    Utils.graficasOCH(SistemaHormigas)
