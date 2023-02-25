import random

import constantes
import funciones_base as base
import statistics
import matplotlib.pyplot as plt
import pandas as pd

isRandom = True
numero_repeticiones = 5

# Constantes
capacidad_bateria = constantes.capacidad_bateria
granularidad = constantes.granularidad[0]
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
evaluaciones = [0 for _ in range(numero_repeticiones)]
dinero = [0 for _ in range(numero_repeticiones)]


# El Algoritmo de Búsqueda Aleatoria (BA) consistirá en generar aleatoriamente una solución en cada
# iteración debiéndose ejecutar 100 iteraciones con cada semilla devolviendo la mejor de las iteraciones.
def busqueda_aleatoria(semilla):
    # Inicializacion de variables
    random.seed(semilla)  # Semilla
    best_dinero_acumulado = []
    best_bateria_hora = []

    contador_evaluaciones = 0
    mejor_evaluacion = -1

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

            mejor_evaluacion = contador_evaluaciones

        contador_evaluaciones += 1

    return max_dinero, best_dinero_acumulado, best_bateria_hora, contador_evaluaciones, solucion_mejor


def grafica_aleatoria():
    dinero_acumulado_aleatorio = []
    bateria_hora_aleatorio = []
    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        dinero_aleatorio, dinero_acumulado_aleatorio, bateria_hora_aleatorio, num_evaluaciones, solucion = busqueda_aleatoria(
            semillas[i])
        dinero[i] = dinero_aleatorio
        evaluaciones[i] = num_evaluaciones

        # Dinero acumulado en cada hora
        fig, ax = plt.subplots()
        ax.set_xticks(range(0, 23, 1))
        ax.plot([j for j in range(24)], dinero_acumulado_aleatorio, label="Dinero Acumulado")
        ax.scatter([j for j in range(24)], dinero_acumulado_aleatorio)

        # Capacidad de la bateria en cada hora
        ax.plot([j for j in range(24)], [b * max(dinero_acumulado_aleatorio) / capacidad_bateria
                                         for b in bateria_hora_aleatorio], c='orange', label="Bateria")
        ax.scatter([j for j in range(24)], [b * max(dinero_acumulado_aleatorio) / capacidad_bateria
                                            for b in bateria_hora_aleatorio], c='orange')
        ax.legend()  # La leyenda
        plt.show()  # Mostramos la gráfica

        print(solucion)

    # Generamos los datos obtenidos de la búsqueda
    data = {
        'Media Evaluaciones': [statistics.mean(evaluaciones)],
        'Mejor Evaluación': [min(evaluaciones)],
        'Desviación Evaluaciones': [statistics.stdev(evaluaciones)],
        'Media Dinero (€)': [round(statistics.mean(dinero) / 100, 2)],
        'Mejor Dinero (€)': [round(max(dinero) / 100, 2)],
        'Desviación Dinero (€)': [round(statistics.stdev(dinero) / 100, 2)]
    }

    # Opciones de Pandas para mostrar la tabla completa en la consola
    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_columns', None)
    pd.set_option('display.width', None)
    pd.set_option('display.max_colwidth', None)

    # Mostramos los datos obtenidos
    print(pd.DataFrame(data))


grafica_aleatoria()
