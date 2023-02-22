# Practica 1: Búsquedas Locales #
import random

# Bibliotecas
import numpy as np

np.random.seed(seed=6)

import matplotlib.pyplot as plt

capacidad_bateria = 300  # Capacidad total de la bateria
porcentaje_bateria = 0  # Capacidad actual de la bateria
wb = np.array([0.0 for _ in range(24)])  # % Lleno de la bateria
wg = np.array([np.random.randint(0, 100) for _ in range(24)])  # Energia generada en cada hora

precio_compra = [26, 26, 25, 24, 23, 24, 25, 27, 30, 29, 34, 32, 31, 31, 25, 24, 25, 26, 34, 36, 39, 40, 38, 29]
precio_venta = [24, 23, 22, 23, 22, 22, 20, 20, 20, 19, 19, 20, 19, 20, 22, 23, 22, 23, 26, 28, 34, 35, 34, 24]
r = [0, 0, 0, 0, 0, 0, 0, 0, 100, 313, 500, 661, 786, 419, 865, 230, 239, 715, 634, 468, 285, 96, 0, 0]


# Funcion de Coste
def funcion_coste(solucion):
    coste = sum(solucion[0, :] * precio_venta - solucion[1, :] * precio_compra)
    return coste


# Generador de la solucion Inicial
def generar_inicial(longitud_vector):
    # Genera un vector aleatorio de porcentaje de venta/compra 
    solucion_inicial = [random.randint(-100, 100) for i in range(longitud_vector)]
    return solucion_inicial


# Generador de Soluciones Vecinas
def genera_vecinos(solucion):
    vecino = solucion

    h = np.random.randint(0, 23)  # Numero aleatorio para seleccionar la columna


# Funcion de aceptacion de soluciones
def acepta(solucion):
    return True


# Algoritmo de busqueda el mejor vecino
def busqueda_elMejor():
    solucion_actual = generar_inicial(longitud_vector=24)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0
    while True:  # Repetir
        while True:  # Repetir
            solucion_vecina = genera_vecinos(solucion_actual)  # Genera vecinos
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # TODO: o hasta que se haya generado el espacio de busqueda completo
            if funcion_coste(solucion_vecina) < funcion_coste(mejor_vecino):
                break
            contador += 1

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        if funcion_coste(solucion_vecina) > funcion_coste(solucion_actual):
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual

        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if funcion_coste(solucion_vecina) <= funcion_coste(solucion_actual):
            break

    return solucion_actual


# Main
print(generar_inicial(24))
