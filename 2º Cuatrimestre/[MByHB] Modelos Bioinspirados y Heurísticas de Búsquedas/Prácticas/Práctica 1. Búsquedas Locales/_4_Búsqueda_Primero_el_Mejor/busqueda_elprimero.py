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


def genera_vecinos(solucion, granularidad, pos):
    solucion_vecina = solucion.copy()

    accion = random.randint(0, 1)  # Elige una accion (decrementar o incrementar)

    if accion == 0:  # Incrementa
        solucion_vecina[pos] += granularidad
        if solucion_vecina[pos] > 100:
            solucion_vecina[pos] = 100
    else:  # Decrementa
        solucion_vecina[pos] -= granularidad
        if solucion_vecina[pos] < -100:
            solucion_vecina[pos] = -100

    return solucion_vecina


# Algoritmo de busqueda el mejor vecino
def busqueda_primero(semilla, granularidad):
    solucion_actual = base.generar_inicial(semilla, 24, granularidad)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0

    while True:  # Repetir
        pos = 0
        while True:  # Repetir
            solucion_vecina = genera_vecinos(solucion_actual, granularidad, pos)  # Genera vecinos
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # o hasta que se haya generado el espacio de busqueda completo
            contador += 2
            if base.funcion_evaluacion(solucion_vecina, isRandom) > base.funcion_evaluacion(mejor_vecino, isRandom) or \
                    pos > 23:
                break
            pos += 1

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        contador += 2
        if base.funcion_evaluacion(solucion_vecina, isRandom) > base.funcion_evaluacion(solucion_actual, isRandom):
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual

        contador += 2
        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if base.funcion_evaluacion(solucion_vecina, isRandom) <= base.funcion_evaluacion(solucion_actual, isRandom):
            break

    print(contador)
    return solucion_actual


print(busqueda_primero(521463, 1))
