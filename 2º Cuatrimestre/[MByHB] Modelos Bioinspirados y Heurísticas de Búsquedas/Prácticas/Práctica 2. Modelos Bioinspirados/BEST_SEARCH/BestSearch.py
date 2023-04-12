# Punto de Comienzo para Rosenbrock : [0,0]
# Punto de Comienzo para Rastrigin: [1,1]
import numpy as np

import Utils


def generar_vecinos(coord):
    vecinos = []
    for i in range(-1, 2):
        for j in range(-1, 2):
            x = coord[0] + i * Utils.GRANULARIDAD
            y = coord[1] + j * Utils.GRANULARIDAD
            # Aseguramos que no supere los límites
            x = max(min(x, Utils.MAX_POS_BL), Utils.MIN_POS_BL)
            y = max(min(y, Utils.MAX_POS_BL), Utils.MIN_POS_BL)
            if i != 0 or j != 0:
                vecinos.append(np.array([x, y]))
    return vecinos


def busqueda_elmejor(funcion):
    num_evaluaciones = 0
    solucion_actual = np.array([1, 1], dtype=float) if funcion == Utils.RastriginFunction else np.array([0, 0],
                                                                                                          dtype=float)
    valor_actual = funcion(solucion_actual)

    while num_evaluaciones < 300:  # Repetir
        mejor_vecino = solucion_actual.copy()
        for vecino in generar_vecinos(solucion_actual):
            num_evaluaciones += 2
            if funcion(vecino) < funcion(mejor_vecino):
                mejor_vecino = vecino

        # Fin-Para
        # Si el objetivo(mejor_vecino) es mejor que objetivo(solucion_actual)
        num_evaluaciones += 2
        if funcion(mejor_vecino) < funcion(solucion_actual):
            solucion_actual = mejor_vecino  # Actualiza solucion_actual
            print(solucion_actual)

        # Condicion de salida
        num_evaluaciones += 2
        if funcion(mejor_vecino) >= funcion(solucion_actual):
            break

    return solucion_actual, funcion(solucion_actual), num_evaluaciones


print(busqueda_elmejor(Utils.RastriginFunction))
