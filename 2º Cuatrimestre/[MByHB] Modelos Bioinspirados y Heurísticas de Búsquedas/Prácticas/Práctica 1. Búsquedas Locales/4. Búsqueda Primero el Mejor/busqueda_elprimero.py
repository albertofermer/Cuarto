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

# Algoritmo de busqueda el mejor vecino
def busqueda_primero(semilla, granularidad):
    solucion_actual = base.generar_inicial(semilla, 24, granularidad)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0
    while True:  # Repetir
        solucion_vecina = base.genera_vecinos(solucion_actual)  # Genera vecinos
        while True:  # Repetir
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # TODO: o hasta que se haya generado el espacio de busqueda completo
            if base.funcion_evaluacion(solucion_vecina) < base.funcion_evaluacion(mejor_vecino):
                break
            contador += 1

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        if base.funcion_evaluacion(solucion_vecina) > base.funcion_evaluacion(solucion_actual):
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual

        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if base.funcion_evaluacion(solucion_vecina) <= base.funcion_evaluacion(solucion_actual):
            break

    return solucion_actual