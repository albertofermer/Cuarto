import random

import constantes
import funciones_base as base
import statistics
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

isRandom = True
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

# Valores de estudio para rellenar la tabla.
evaluaciones = np.tile(np.array([0 for _ in range(numero_repeticiones)]), (3, 1))
dinero = np.tile(np.array([0 for _ in range(numero_repeticiones)]), (3, 1))


# El Algoritmo de Búsqueda Aleatoria (BA) consistirá en generar aleatoriamente una solución en cada
# iteración debiéndose ejecutar 100 iteraciones con cada semilla devolviendo la mejor de las iteraciones.
def busqueda_aleatoria(semilla, granularidad):
    # Inicializacion de variables
    random.seed(semilla)  # Semilla
    best_dinero_acumulado = []
    best_bateria_hora = []

    contador_evaluaciones = 0
    # mejor_evaluacion = -1

    # Genera la solucion Inicial
    solucion_inicial = base.generar_inicial(semilla, 24, granularidad)
    solucion_actual = solucion_inicial
    solucion_mejor = solucion_actual

    max_dinero = float('-inf')

    # Repetir
    for _ in range(100):
        # Genera solucion aleatoria
        solucion_actual = [random.randrange(-100, 101, granularidad) for _ in range(24)]
        # Comprueba si la funcion objetivo es mejor
        objetivo_actual, dinero_acumulado_act, bateria_hora_act = base.funcion_evaluacion(solucion_actual)

        if objetivo_actual > max_dinero:
            max_dinero = objetivo_actual
            solucion_mejor = solucion_actual
            best_dinero_acumulado = dinero_acumulado_act
            best_bateria_hora = bateria_hora_act

            # mejor_evaluacion = contador_evaluaciones

        contador_evaluaciones += 1

    return max_dinero, best_dinero_acumulado, best_bateria_hora, contador_evaluaciones, solucion_mejor


def grafica_aleatoria():

    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        for g in range(len(granularidades)):
            dinero_aleatorio, dinero_acumulado_aleatorio, bateria_hora_aleatorio, num_evaluaciones, solucion = \
                busqueda_aleatoria(semillas[i], granularidades[g])

            dinero[g, i] = dinero_aleatorio
            evaluaciones[g, i] = num_evaluaciones

            # Dinero acumulado en cada hora
            fig, ax = plt.subplots()
            plt.title(f"Búsqueda Aleatoria. G = {granularidades[g]}, S = {semillas[i]}")
            ax.set_xticks(range(0, 23, 1))
            ax.plot([j for j in range(24)], [cent/100 for cent in dinero_acumulado_aleatorio], label="Dinero Acumulado")
            ax.scatter([j for j in range(24)], [cent/100 for cent in dinero_acumulado_aleatorio])

            # Capacidad de la bateria en cada hora
            ax.plot([j for j in range(24)], bateria_hora_aleatorio, c='orange', label="Bateria")
            ax.scatter([j for j in range(24)], bateria_hora_aleatorio, c='orange')
            ax.legend()  # La leyenda
            plt.xlabel("Horas")
            plt.ylabel("Euros (€)")
            if not isRandom:
                plt.savefig(f'.\\graficas\\ProblemaReal\\'
                            f'randomsearch_g{granularidades[g]}_s{semillas[i]}_ProblemaReal.png')
            else:
                plt.savefig(f'.\\graficas\\ProblemaAleatorio\\'
                            f'randomsearch_g{granularidades[g]}_s{semillas[i]}_ProblemaAleatorio.png')
            plt.show()  # Mostramos la gráfica

            print(solucion)

    # Generamos los datos obtenidos de la búsqueda
    for i in range(3):
        data = {
            'Media Evaluaciones': [statistics.mean(evaluaciones[i, :])],
            'Mejor Evaluación': [min(evaluaciones[i, :])],
            'Desviación Evaluaciones': [statistics.stdev(evaluaciones[i, :])],
            'Media Dinero (€)': [round(statistics.mean(dinero[i, :]) / 100, 2)],
            'Mejor Dinero (€)': [round(max(dinero[i, :]) / 100, 2)],
            'Desviación Dinero (€)': [round(statistics.stdev(dinero[i, :]) / 100, 2)]
        }

        # Opciones de Pandas para mostrar la tabla completa en la consola
        pd.set_option('display.max_rows', None)
        pd.set_option('display.max_columns', None)
        pd.set_option('display.width', None)
        pd.set_option('display.max_colwidth', None)

        # Mostramos los datos obtenidos
        print(f"Granularidad: {granularidades[i]}")
        print(pd.DataFrame(data))


grafica_aleatoria()
