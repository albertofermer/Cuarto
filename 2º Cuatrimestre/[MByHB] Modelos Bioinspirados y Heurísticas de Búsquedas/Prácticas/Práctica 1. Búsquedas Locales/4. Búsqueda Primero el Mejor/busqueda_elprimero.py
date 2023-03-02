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


def genera_vecinos(solucion, granularidad, lista_entorno):
    solucion_vecina = solucion.copy()
    print(lista_entorno)
    while solucion_vecina in lista_entorno:
        solucion_vecina = solucion.copy()
        h = random.randint(0, 24)   # Accede a una posicion aleatoria
        accion = random.randint(0, 1)   # Elige una accion (decrementar o incrementar)

        if accion == 0:     # Incrementa
            solucion_vecina[h] += granularidad
            if solucion_vecina[h] > 100:
                solucion_vecina[h] = 100
        else:   # Decrementa
            solucion_vecina[h] -= granularidad
            if solucion_vecina[h] < -100:
                solucion_vecina[h] = -100

    return solucion_vecina


# Algoritmo de busqueda el mejor vecino
def busqueda_primero(semilla, granularidad):
    solucion_actual = base.generar_inicial(semilla, 24, granularidad)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0
    lista_vecinos = []

    while True:  # Repetir
        while True:  # Repetir
            solucion_vecina = genera_vecinos(solucion_actual, granularidad, lista_vecinos)  # Genera vecinos
            lista_vecinos.append(solucion_vecina)
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # o hasta que se haya generado el espacio de busqueda completo
            if base.funcion_evaluacion(solucion_vecina, isRandom) > base.funcion_evaluacion(mejor_vecino, isRandom) or \
                    len(lista_vecinos) >= 48:
                break

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        if base.funcion_evaluacion(solucion_vecina, isRandom) > base.funcion_evaluacion(solucion_actual, isRandom):
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual

        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if base.funcion_evaluacion(solucion_vecina, isRandom) <= base.funcion_evaluacion(solucion_actual, isRandom):
            break

    return solucion_actual


print(busqueda_primero(123456, 1))

