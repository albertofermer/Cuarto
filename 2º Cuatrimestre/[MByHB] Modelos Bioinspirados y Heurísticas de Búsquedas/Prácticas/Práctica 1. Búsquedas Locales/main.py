# Practica 1: Búsquedas Locales #

# Bibliotecas
import numpy as np
import matplotlib.pyplot as plt


# Funcion de Coste
def funcion_coste(solucion):
    coste = 0
    return coste


# Generador de la solucion Inicial
def generar_inicial(longitud_vector):
    solucion_inicial = np.tile(np.array([0 for i in range(longitud_vector)]), [3, 1])
    return solucion_inicial


# Generador de Soluciones Vecinas
def genera_vecinos(solucion):
    vecino = 0
    # Generamos un numero aleatorio r entre 0 y 2 para seleccionar qué valor hay que modificar
    seed = 10
    np.random.seed(seed)
    
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
            if funcion_coste(solucion_vecina) < funcion_coste(mejor_vecino) or contador > 100:
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
print(busqueda_elMejor())


