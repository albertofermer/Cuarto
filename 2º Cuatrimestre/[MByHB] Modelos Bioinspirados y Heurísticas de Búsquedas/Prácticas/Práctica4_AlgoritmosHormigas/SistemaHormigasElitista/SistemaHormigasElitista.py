import random
import sys

import Utils
import importlib
import numpy as np
import time

sys.path.append('..\Greedy')
import greedy


def SistemaHormigasElitista(semilla, problema):
    np.random.seed(semilla)
    random.seed(semilla)
    # Lectura de las coordenadas del problema:
    dimension, ciudades = Utils.leerFicheroTSP(f"..\\FicherosTSP\\{problema}.tsp")
    dimension = int(dimension)
    pathGreedy = greedy.tsp_greedy(semilla, problema)[1]

    # Inicializar matrices
    distancias = Utils.inicializarMatrizDistancias(ciudades)
    coste_greedy = Utils.funcionCoste(distancias, pathGreedy)
    feromonas = np.ones((dimension, dimension)) * (1 / (dimension * coste_greedy))
    # Sembrar Feromonas del camino del Greedy
    # sembrarFeromonas(feromonas, pathGreedy)
    solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    mejorActual = coste[0].copy()
    mejorGlobal = coste[0].copy()
    mejorHormiga = solucionHormigas[0]
    mejorHormigaActual = solucionHormigas[0]

    numEvaluaciones = 0
    MejorCosteIteracion = []
    # Comienza el algoritmo
    start = time.time()
    t = 0
    while (time.time() - start) < Utils.TiempoParada(problema):
        t += 1
        solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1
        for hormiga in range(len(solucionHormigas)):  # Para cada hormiga
            solucionHormigas[hormiga][0] = 0  # El primer nodo es el 0
            for nodo in range(1, dimension):  # Para cada nodo
                probabilidades = transicion(feromonas, distancias, solucionHormigas[hormiga])
                # Elegir un nodo en funcion de las probabilidades calculadas
                solucionHormigas[hormiga][nodo] = elegirNodo(probabilidades)
            coste[hormiga] = Utils.funcionCoste(distancias, solucionHormigas[hormiga])
            numEvaluaciones += 1
            if coste[hormiga] < mejorActual:
                mejorActual = coste[hormiga]
                mejorHormigaActual = solucionHormigas[hormiga].copy()

        actualizarFeromonas(distancias, solucionHormigas, feromonas, mejorHormiga)
        numEvaluaciones += Utils.numeroHormigas

        if mejorActual < mejorGlobal:
            mejorGlobal = mejorActual
            mejorHormiga = mejorHormigaActual.copy()
            print(mejorGlobal)
        MejorCosteIteracion.append(mejorActual.copy())

    print(f"Tiempo: {time.time() - start}")
    return mejorGlobal, mejorHormiga, numEvaluaciones, MejorCosteIteracion


def actualizarFeromonas(distancias, soluciones, feromonas, mejorHormiga):
    num_hormigas = len(soluciones)  # Número de hormigas en la población
    num_arcos = len(soluciones[0])  # Número de arcos en una solución

    delta = np.zeros((num_arcos, num_arcos))  # Matriz para almacenar la cantidad de feromonas a actualizar

    # Calcular los costes de todas las hormigas en paralelo utilizando operaciones vectorizadas
    costes_hormigas = np.array([Utils.funcionCoste(distancias, hormiga) for hormiga in soluciones])

    # Calcular el coste de la mejorHormiga
    mejor_coste_hormiga = Utils.funcionCoste(distancias, mejorHormiga)

    # Actualizar las feromonas para cada hormiga
    for hormiga in range(num_hormigas):
        # Obtener los arcos existentes en la solución de la hormiga
        arcos_existentes = existeArco(soluciones[hormiga], num_arcos)

        # Calcular el valor auxiliar para la actualización de feromonas
        aux = Utils.FACTOR_COSTE / costes_hormigas[hormiga]

        # Actualizar delta sumando el valor auxiliar en los arcos existentes de la hormiga
        delta[arcos_existentes] += aux

    mejor_coste_hormiga = 1e-10 if mejor_coste_hormiga == 0 else mejor_coste_hormiga
    # Actualizar delta sumando el término correspondiente a la mejorHormiga
    delta[existeArco(mejorHormiga, num_arcos)] += Utils.numeroHormigasElitista / mejor_coste_hormiga

    # Aplicar evaporación y actualizar las feromonas
    feromonas *= (1 - Utils.EVAPORACION)
    feromonas += delta


def existeArco(solucion, num_arcos):
    arcos = np.zeros((num_arcos, num_arcos), dtype=bool)  # Matriz para indicar la existencia de los arcos
    arcos[solucion[:-1], solucion[1:]] = True  # Marcar como verdaderos los arcos existentes en la solución
    return arcos



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

    if sumaTotal == 0:
        sumaTotal = 1e-10

    probabilidades = np.zeros(len(distancias))
    probabilidades[u_not_in_solucionHormiga] = (feromonas_r[u_not_in_solucionHormiga] ** Utils.alpha) * (
                distancias_r_inv[u_not_in_solucionHormiga] ** Utils.beta) / sumaTotal

    return probabilidades


def sembrarFeromonas(feromonas, path):
    path_np = np.array(path)
    feromonas[path_np[:-1], path_np[1:]] *= Utils.FACTOR_SEMBRADO


def elegirNodo(probabilidades):
    nodosNoVisitados = np.where(probabilidades != 0)[0]
    ciudad = random.choices(nodosNoVisitados, weights=probabilidades[np.nonzero(probabilidades)], k=1)[0]
    return ciudad


if __name__ == "__main__":
    Utils.graficasOCH(SistemaHormigasElitista)
