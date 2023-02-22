# Practica 1: Búsquedas Locales #
import random

# Bibliotecas
import numpy as np
np.random.seed(seed=6)

import matplotlib.pyplot as plt

capacidad_bateria = 300  # Capacidad total de la bateria
porcentaje_bateria = 0  # Capacidad actual de la bateria
wb = np.array([0.0 for _ in range(24)])   # % Lleno de la bateria
wg = np.array([np.random.randint(0, 100) for _ in range(24)])   # Energia generada en cada hora
precio_compra = [26, 26, 25, 24,23,24,25,27,30,29,34,32,31,31,25,24,25,26,34,36,39,40,38,29]
precio_venta = [24, 23, 22, 23,22,22,20,20,20,19,19,20,19,20,22,23,22,23,26,28,34,35,34,24]
r = [0, 0, 0, 0, 0, 0, 0, 0, 100, 313, 500, 661, 786, 419, 865, 230, 239, 715, 634, 468, 285, 96,
0, 0]
precio_venta = np.array([np.random.randn()*100 for _ in range(24)])
precio_compra = np.array([np.random.randn()*100 for _ in range(24)])


# Funcion de Coste
def funcion_coste(solucion):
    coste = sum(solucion[0, :] * precio_venta - solucion[1, :]*precio_compra)
    return coste


# Generador de la solucion Inicial
def generar_inicial(longitud_vector):
    solucion_inicial = np.tile(np.array([random.randint(0,100) for i in range(longitud_vector)]), [3, 1])
    return solucion_inicial


# Generador de Soluciones Vecinas
def genera_vecinos(solucion):
    vecino = solucion

    h = np.random.randint(0, 23)     # Numero aleatorio para seleccionar la columna

    while True:
        r = np.random.randint(0, 3)     # Numero aleatorio para seleccionar la fia
        if r == 0 and sum(wb[0:h]) > 0:      # Modifica la fila de venta
            #print("vende")
            v = np.random.randint(0, wb[h]*capacidad_bateria + wg[h])
            vecino[r, h] = v
            return vecino
        elif r == 1 and sum(wb[0:h]) < 1:    # Modifica la fila de compra
            #print("compra")
            print(wb)
            c = np.random.randint(0, (1-wb[h])*capacidad_bateria)
            vecino[r, h] = c
            wb[h] = c/capacidad_bateria

            return vecino
        elif r == 3 and sum(wb[0:h]) < 1:           # Modifica la fila de almacenamiento
            #print("almacena")
            a = np.random.randint(0, (1-wb[h])*capacidad_bateria)
            vecino[r, h] = a
            wb[h] = a/capacidad_bateria
            return vecino


# Funcion de aceptacion de soluciones
def acepta(solucion):
    return True


# Algoritmo de busqueda el mejor vecino
def busqueda_elMejor():

    solucion_actual = generar_inicial(longitud_vector=24)   # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0
    while True:  # Repetir
        while True:     # Repetir
            solucion_vecina = genera_vecinos(solucion_actual)    # Genera vecinos
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
#print(genera_vecinos(generar_inicial(24)))

s = busqueda_elMejor()
print(funcion_coste(s))
print(s)
