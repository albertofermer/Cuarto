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
    sembrarFeromonas(feromonas,  pathGreedy)
    # print(feromonas)
    solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    mejorActual = coste[0].copy()
    mejorGlobal = coste[0].copy()
    mejorHormiga = solucionHormigas[0]
    mejorHormigaActual = solucionHormigas[0]
    # Comienza el algoritmo
    start = time.time()
    while (time.time() - start) < Utils.TiempoParada(problema):
        solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1
        for hormiga in range(len(solucionHormigas)):  # Para cada hormiga
            solucionHormigas[hormiga][0] = 0  # El primer nodo es el 0
            # print(f"Hormiga {hormiga}:{solucionHormigas[hormiga]}")
            for nodo in range(1, dimension):  # Para cada nodo
                probabilidades = transicion(feromonas, distancias, solucionHormigas[hormiga])
                # Elegir un nodo en funcion de las probabilidades calculadas
                ciudad = elegirNodo(probabilidades)
                solucionHormigas[hormiga][nodo] = ciudad
            coste[hormiga] = Utils.funcionCoste(distancias, solucionHormigas[hormiga])
            if coste[hormiga] < mejorActual:
                mejorActual = coste[hormiga]
                mejorHormigaActual = solucionHormigas[hormiga]

        #     # Aporte de Feromonas
        #     aportarFeromonas(distancias, solucionHormigas[hormiga], feromonas)
        #
        # # Evaporacion de Feromonas
        # evaporarFeromonas(feromonas)
        actualizarFeromonas(distancias, solucionHormigas, feromonas)

        if mejorActual < mejorGlobal:
            mejorGlobal = mejorActual
            mejorHormiga = mejorHormigaActual.copy()
            print(mejorHormiga)
            print(f"Coste: {mejorGlobal}")

    Utils.plot_path(ciudades, mejorHormiga, f"Hormiga: {mejorGlobal}")
    return mejorGlobal, mejorHormiga


def actualizarFeromonas(distancias, soluciones, feromonas):
    delta = np.zeros((len(soluciones[0]),len(soluciones[0])))
    # Calculo de delta
    for r in range(len(soluciones[0])):  # Para cada arista
        for s in range(len(soluciones[0])):
            aux = 0
            for hormiga in range(len(soluciones)):  # Para cada hormiga
                if existeArco(soluciones[hormiga], r, s):  # Si la hormiga ha visitado el arco r -> r+1
                    aux += 1 / Utils.funcionCoste(distancias, soluciones[hormiga])
            # print(aux)
            delta[r][s] = aux
    # Actualizacion de cada arco de feromonas
    for r in range(len(soluciones[0])):
        for s in range(len(soluciones[0])):
            feromonas[r][s] = (1 - Utils.EVAPORACION) * feromonas[r][s] + delta[r][s]


def existeArco(solucion, r, s):
    encontrado = False
    indice = 0
    while not encontrado and indice < len(solucion) - 1:
        # print(solucion)
        if solucion[indice] == r and solucion[indice + 1] == s:
            return True
        indice += 1
    return False


def transicion(feromonas, distancias, solucionHormiga):
    sumaTotal = 0
    i = 0
    ultimoNodoVisitado = 0
    while solucionHormiga[i] != -1 and i < len(solucionHormiga):
        # print(solucionHormiga[i])
        if solucionHormiga[i] != -1:
            ultimoNodoVisitado = solucionHormiga[i]
            i += 1

    r = ultimoNodoVisitado
    for u in range(len(distancias)):
        if u not in solucionHormiga:
            sumaTotal += (feromonas[r][u] ** Utils.alpha) * (1 / (distancias[r][u])) ** Utils.beta

    probabilidades = np.zeros((len(distancias)))

    if sumaTotal == 0: sumaTotal = 1e-10

    for s in range(len(distancias)):
        if s not in solucionHormiga:
            probabilidades[s] = (feromonas[r][s] ** Utils.alpha) * (1 / (distancias[r][s])) ** Utils.beta / sumaTotal
        else:
            probabilidades[s] = 0
    return probabilidades


def aportarFeromonas(distancias, solucionHormiga, feromonas):
    for r in range(len(solucionHormiga)):
        for s in range(len(solucionHormiga)):
            if existeArco(solucionHormiga, r, s):
                feromonas[r][s] += 1 / (Utils.funcionCoste(distancias, solucionHormiga))
    # print(feromonas)


def evaporarFeromonas(feromonas):
    feromonas *= (1 - Utils.EVAPORACION)


def sembrarFeromonas(feromonas, path):
    for nodo in range(len(path) - 1):
        feromonas[path[nodo]][path[nodo + 1]] *= 15


def elegirNodo(probabilidades):
    nodosNoVisitados = np.where(probabilidades != 0)[0]
    ciudad = random.choices(nodosNoVisitados, weights=probabilidades[np.nonzero(probabilidades)], k=1)[0]
    return ciudad


if __name__ == "__main__":
    print(SistemaHormigas(123456, "ch130"))
