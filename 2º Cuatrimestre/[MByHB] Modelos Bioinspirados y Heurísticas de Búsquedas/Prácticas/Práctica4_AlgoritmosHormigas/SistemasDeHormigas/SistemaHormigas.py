import sys

import Utils
import importlib
sys.path.append('..\Greedy')
import greedy
import numpy as np
import time


def SistemaHormigas(semilla, problema):
    # Lectura de las coordenadas del problema:
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    dimension = int(dimension)

    # Inicializar matrices
    feromonas = np.ones((dimension, dimension))
    # Sembrar Feromonas del camino del Greedy
    # print(feromonas)
    pathGreedy = greedy.tsp_greedy(ciudades)
    sembrarFeromonas(pathGreedy,feromonas)
    # print(feromonas)
    distancias = Utils.inicializarMatrizDistancias(ciudades)
    solucionHormigas = np.zeros((Utils.numeroHormigas, dimension), dtype=int)
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    mejorActual = coste[0].copy()
    mejorGlobal = coste[0].copy()

    # Comienza el algoritmo
    start = time.time()
    while (time.time() - start) < 10:
        for hormiga in range(len(solucionHormigas)):  # Para cada hormiga
            for nodo in range(1,dimension):  # Para cada nodo
                probabilidades = transicion(feromonas, distancias, solucionHormigas[hormiga])
                # Elegir unn nodo en funcion de las probabilidades calculadas
                #np.random.choice()
                solucionHormigas[hormiga][nodo] = transicion(feromonas, distancias, solucionHormigas[hormiga])
                coste[hormiga] = Utils.funcionCoste(distancias, solucionHormigas[hormiga])
                if coste[hormiga] < mejorActual:
                    mejorActual = coste[hormiga]
            # Aporte de Feromonas
            aportarFeromonas(distancias, solucionHormigas[hormiga], feromonas)
        # Evaporacion de Feromonas
        evaporarFeromonas(distancias, solucionHormigas, feromonas)

        if mejorActual < mejorGlobal:
            mejorGlobal = mejorActual

    return mejorGlobal


def transicion(feromonas, distancias, solucionHormiga):
    sumaTotal = 0
    for r in range(len(distancias)):
        for u in range(len(distancias)):
            if u not in solucionHormiga:
                sumaTotal += np.power(feromonas[r][u], Utils.alpha) * np.power((1 / (distancias[r][u] + 1)), Utils.beta)

    probabilidades = np.zeros((len(distancias), len(distancias)))

    for r in range(len(distancias)):
        for u in range(len(distancias)):
            if u not in solucionHormiga:
                probabilidades[r][u] = (np.power(feromonas[r][u], Utils.alpha) * np.power((1 / (distancias[r][u] + 1)),
                                                                                          Utils.beta)) / sumaTotal
            else:
                probabilidades[r][u] = 0
    return probabilidades


def inicializarMatrizVisitados(dimension):
    """Función para inicializar la matriz de nodos visitados."""
    visitados = np.zeros((dimension, dimension))
    return visitados


def aportarFeromonas(distancias, solucionHormiga, feromonas):
    for i in range(len(solucionHormiga) - 1):
        feromonas[i][i + 1] += 1 / (Utils.funcionCoste(distancias, solucionHormiga))


def evaporarFeromonas(distancias, solucionHormigas, feromonas):
    for hormiga in solucionHormigas:
        for i in range(len(solucionHormigas[hormiga]) - 1):
            feromonas[i][i + 1] = (1 - Utils.EVAPORACION) * feromonas[i][i + 1]

def sembrarFeromonas(path, feromonas):
    for nodo in range(len(path) - 1):
        feromonas[nodo][nodo+1] += 1


if __name__ == "__main__":
    print(SistemaHormigas(123456, "test10"))
