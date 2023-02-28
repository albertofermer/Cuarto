import random

import constantes
import funciones_base as base
import statistics
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

isRandom = False
numero_repeticiones = 5

# Constantes
capacidad_bateria = constantes.capacidad_bateria
granularidades = constantes.granularidad
semillas = constantes.semillas

if isRandom:
    precio_venta = constantes.precio_venta_random
    precio_compra = constantes.precio_compra_random
    r = constantes.r_random
else:
    precio_venta = constantes.precio_venta
    precio_compra = constantes.precio_compra
    r = constantes.r


def entorno(solucion_actual, granularidad):
    entorno_soluciones = []
    for i in range(24):
        solucion_candidata = solucion_actual.copy()

        if solucion_candidata[i] + granularidad <= 100:
            solucion_candidata[i] += granularidad
        else:
            solucion_candidata[i] = 100

        for j in range(23, -1, -1):
            solucion_candidata2 = solucion_candidata.copy()
            if solucion_candidata2[j] - granularidad >= -100:
                solucion_candidata2[j] -= granularidad
            else:
                solucion_candidata2[j] = -100

            entorno_soluciones.append(solucion_candidata2)
    return entorno_soluciones


def busqueda_elmejor(semilla, granularidad):
    solucion_inicial = base.generar_inicial(semilla, 24, granularidad)
    solucion_actual = solucion_inicial

    while True:  # Repetir
        mejor_vecino = solucion_actual
        for s_prima in entorno(solucion_actual, granularidad):  # Repetir para toda S' perteneciente a E(S_act)
            # Si el objetivo(s_prima) es mejor que objetivo(mejor_vecino)
            if base.funcion_evaluacion(s_prima) > base.funcion_evaluacion(mejor_vecino):
                mejor_vecino = s_prima  # Actualizamos el mejor vecino
        # Fin-Para
        # Si el objetivo(mejor_vecino) es mejor que objetivo(solucion_actual)
        if base.funcion_evaluacion(mejor_vecino) > base.funcion_evaluacion(solucion_actual):
            solucion_actual = mejor_vecino  # Actualiza solucion_actual
        else:  # En caso contrario
            break  # Sale del bucle
    return solucion_actual  # Devuelve la solucion actual


print((busqueda_elmejor(123456, 1)))