# Practica 1: Búsquedas Locales #


# Bibliotecas
import random
import numpy as np
import matplotlib.pyplot as plt
import constantes

# Constantes
capacidad_bateria = constantes.capacidad_bateria
precio_venta = constantes.precio_venta
precio_compra = constantes.precio_compra
r = constantes.r
granularidad = constantes.granularidad


np.random.seed(seed=6)











def busqueda_elprimero():
    return 0


# Algoritmo de busqueda el mejor vecino
def busqueda_elMejor():
    solucion_actual = generar_inicial(24)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0
    while True:  # Repetir
        solucion_vecina = genera_vecinos(solucion_actual)  # Genera vecinos
        while True:  # Repetir
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # TODO: o hasta que se haya generado el espacio de busqueda completo
            if funcion_evaluacion(solucion_vecina) < funcion_evaluacion(mejor_vecino):
                break
            contador += 1

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        if funcion_evaluacion(solucion_vecina) > funcion_evaluacion(solucion_actual):
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual

        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if funcion_evaluacion(solucion_vecina) <= funcion_evaluacion(solucion_actual):
            break

    return solucion_actual


# Main

