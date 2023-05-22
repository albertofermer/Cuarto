import random
import sys

import Utils
import importlib
import numpy as np
import time
sys.path.append('../Greedy')
import greedy


def SistemaHormigasElitista(semilla, problema):
    """
    Algoritmo de optimización basado en colonia de hormigas elitistas. Lanza una cantidad de hormigas durante cierto tiempo
    y almacena la mejor solución obtenida para el problema TSP pasado por parámetro. El tiempo que tarda en
    ejecutarse viene definido por el problema siendo 'ch130' 3 minutos y 'a280' 8 minutos. El número de hormigas es de
    30 individuos.

    La diferencia con el algoritmo de hormigas convencional es que cada vez que actualiza las feromonas, aporta
    un nuevo término en el mejor camino que ha generado el algoritmo.

    Args:
        semilla: Semilla numérica para generar los números aleatorios que utiliza el algoritmo.
        problema: Nombre del problema TSP para el que se está ejecutando el algoritmo de Sistema de Hormigas. Puede
        ser 'ch130' o 'a280'

    Returns:
        mejorGlobal: El coste más bajo de camino obtenido por la mejor hormiga.
        mejorHormiga: El camino con el coste más bajo de todas las hormigas durante la ejecución del algoritmo.
        numeroEvaluaciones: Número de evaluaciones que ha realizado el algoritmo en total.
        MejorCosteIteracion: El coste más bajo obtenido en cada una de las iteraciones.

    """
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
    coste = np.ones(Utils.numeroHormigas) * float('inf')
    mejorActual = coste_greedy
    mejorGlobal = coste_greedy
    mejorHormiga = pathGreedy
    mejorHormigaActual = pathGreedy
    MejorCosteIteracion = []
    numeroEvaluaciones = 0
    # Comienza el algoritmo
    start = time.time()

    while (time.time() - start) < Utils.TiempoParada(problema):
        # Inicializamos las soluciones de las hormigas a -1.
        solucionHormigas = np.ones((Utils.numeroHormigas, dimension), dtype=int) * -1

        for hormiga in range(len(solucionHormigas)):  # Para cada hormiga
            # Inicializamos el primer nodo de cada hormiga al nodo 0.
            solucionHormigas[hormiga][0] = 0

            for nodo in range(1, dimension):  # Para cada nodo
                # Calculamos las probabilidades de transicionar a cada una de las ciudades.
                probabilidades = transicion(feromonas, distancias, solucionHormigas[hormiga])

                # Elegir un nodo en funcion de las probabilidades calculadas
                solucionHormigas[hormiga][nodo] = elegirNodo(probabilidades)

            # Calculamos el coste de cada hormiga una vez haya terminado de recorrer el camino.
            coste[hormiga] = Utils.funcionCoste(distancias, solucionHormigas[hormiga])
            numeroEvaluaciones += 1

            # Guardamos la mejor hormiga de esta iteración
            if coste[hormiga] < mejorActual:
                mejorActual = coste[hormiga]
                mejorHormigaActual = solucionHormigas[hormiga].copy()

        # Actualizamos la matriz de feromonas con el aporte y evaporación.
        actualizarFeromonas(distancias, solucionHormigas, feromonas, mejorHormiga)
        numeroEvaluaciones += Utils.numeroHormigas

        # Guardamos la mejor hormiga global
        if mejorActual < mejorGlobal:
            mejorGlobal = mejorActual
            mejorHormiga = mejorHormigaActual.copy()
            print(mejorGlobal)

        MejorCosteIteracion.append(mejorActual.copy())
    print(f"Tiempo: {time.time() - start}")
    return mejorGlobal, mejorHormiga, numeroEvaluaciones, MejorCosteIteracion


def actualizarFeromonas(distancias, soluciones, feromonas, mejorHormiga):
    """
    Función para actualizar las feromonas del problema después de haber lanzado todas las hormigas.
    Args:
        distancias: Matriz de distancias entre las ciudades.
        soluciones: Matriz de las soluciones obtenidas por todas las hormigas.
        feromonas: Matriz de feromonas actual.
        mejorHormiga: Mejor camino obtenido hasta el momento.

    Returns:
        Matriz de feromonas actualizada después del aporte de feromonas de cada hormiga y después de la
    evaporación.

    """
    num_hormigas = len(soluciones)  # Número de hormigas en la población
    num_arcos = len(soluciones[0])  # Número de arcos en una solución
    delta = np.zeros((num_arcos, num_arcos))  # Matriz para almacenar la cantidad de feromonas a actualizar

    # Calcular los costes de todas las hormigas en paralelo utilizando operaciones vectorizadas
    costes_hormigas = np.array([Utils.funcionCoste(distancias, hormiga) for hormiga in soluciones])

    # Calcular el coste de la mejorHormiga
    mejor_coste_hormiga = Utils.funcionCoste(distancias, mejorHormiga)
    # Si el coste de la mejor hormiga es 0, entonces se iguala a 1e-10 para evitar divisiones entre 0.
    mejor_coste_hormiga = 1e-10 if mejor_coste_hormiga == 0 else mejor_coste_hormiga

    # Actualizar las feromonas para cada hormiga
    for hormiga in range(num_hormigas):
        # Obtener los arcos existentes en la solución de la hormiga
        arcos_existentes = existeArco(soluciones[hormiga], num_arcos)
        # Actualizar delta sumando el valor auxiliar en los arcos existentes de la hormiga
        delta[arcos_existentes] += Utils.FACTOR_COSTE / costes_hormigas[hormiga]

    # Actualizar delta sumando el término correspondiente a la mejorHormiga.
    delta[existeArco(mejorHormiga, num_arcos)] += Utils.numeroHormigasElitista / mejor_coste_hormiga

    # Aplicar evaporación y actualizar las feromonas
    feromonas *= (1 - Utils.EVAPORACION)    # Evaporacion
    feromonas += delta  # Aporte


def existeArco(solucion, num_arcos):
    """
    Devuelve una matriz booleana que indica si el arco existe en una solución.
    Args:
        solucion: Camino que ha seguido una hormiga hasta el momento.
        num_arcos: Número de arcos totales que existen en el problema

    Returns:
        arcos: Una matriz booleana que indica con True los arcos existentes en la solución y con False lo contrario.

    """
    arcos = np.zeros((num_arcos, num_arcos), dtype=bool)  # Matriz para indicar la existencia de los arcos
    arcos[solucion[:-1], solucion[1:]] = True  # Marcar como verdaderos los arcos existentes en la solución
    return arcos


def transicion(feromonas, distancias, solucionHormiga):
    """
        Args:
            feromonas: Matriz de feromonas con las que se calcularán las probabilidades de transicionar a un nodo específico.
            distancias: Matriz de distancias entre los nodos que servirá para saber a qué nodos puede transicionar la hormiga.
            solucionHormiga: Solución actual de la hormiga que permite saber qué nodos ha visitado.

        Returns:
            probabilidades: Vector de probabilidades para transicionar a cada nodo del problema. Los nodos ya visitados
                            tendrán una probabilidad de cero.
    """

    # Comprueba los nodos en los que la hormiga ha estado.
    indices_validos = np.where(solucionHormiga != -1)[0]

    # Almacena el último nodo visitado.
    r = solucionHormiga[indices_validos[-1]]

    # Obtiene una lista de nodos en los que la hormiga no ha estado haciendo la diferencia entre los conjuntos
    # de nodos en los que sí ha estado y la totalidad de los nodos que existen.
    u_not_in_solucionHormiga = np.setdiff1d(np.arange(len(distancias)), solucionHormiga)

    # Almacena las feromonas del último nodo visitado (r)
    feromonas_r = feromonas[r]

    # Realiza la inversa de las distancias como medida heurística
    distancias_r_inv = np.reciprocal(distancias[r])

    # Calcula el denominador de la función de transición
    aux = (feromonas_r[u_not_in_solucionHormiga] ** Utils.alpha) * (distancias_r_inv[u_not_in_solucionHormiga] ** Utils.beta)
    sumaTotal = np.sum(aux)

    # En caso de que la suma total sea 0, se convierte en 1e-10 para evitar divisiones entre 0
    if sumaTotal == 0:
        sumaTotal = 1e-10

    # Se genera un vector de probabilidades inicializado a 0 para que los nodos ya visitados tengan probabilidad nula de
    # volver a ser visitados por la hormiga.
    probabilidades = np.zeros(len(distancias))

    # Se calculan las probabilidades de cada nodo en función de la regla de transición para todos los nodos en los que
    # la hormiga no ha estado (u_not_in_solucionHormiga).
    probabilidades[u_not_in_solucionHormiga] = aux / sumaTotal

    # Devuelve el vector de probabilidades.
    return probabilidades


def sembrarFeromonas(feromonas, path):
    path_np = np.array(path)
    feromonas[path_np[:-1], path_np[1:]] *= Utils.FACTOR_SEMBRADO


def elegirNodo(probabilidades):
    """
    Función para, a partir de las probabilidades calculadas con la función de transición, elegir el siguiente
    nodo al que va a transicionar la hormiga.
    Args:
        probabilidades: Vector de probabilidades obtenido utilizando la función de transición

    Returns:
        ciudad: El índice del nodo siguiente que va a visitar la hormiga.

    """
    # Inicializamos un vector de nodos no visitados utilizando las probabilidades diferentes a 0 que hemos
    # obtenido con la función de transición.
    nodosNoVisitados = np.where(probabilidades != 0)[0]

    # Elegimos aleatoriamente utilizando dichas probabilidades no nulas una ciudad para que la hormiga transicione.
    ciudad = random.choices(nodosNoVisitados, weights=probabilidades[np.nonzero(probabilidades)], k=1)[0]

    # Devolvemos la ciudad que ha salido elegida.
    return ciudad


if __name__ == "__main__":
    Utils.graficasOCH(SistemaHormigasElitista)
