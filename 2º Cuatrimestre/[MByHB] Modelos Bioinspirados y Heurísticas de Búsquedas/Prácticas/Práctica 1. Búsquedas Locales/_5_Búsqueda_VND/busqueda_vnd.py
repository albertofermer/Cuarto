import random

import constantes
import funciones_base as base
import statistics
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from itertools import combinations

isRandom = False
numero_repeticiones = 5

# Constantes
capacidad_bateria = constantes.capacidad_bateria
granularidades = constantes.granularidad
semillas = constantes.semillas

# Numero de Individuos que Cambia la busqueda cada vez que aumente el k:
#   k = 1 : Cambia 1 individuo
#   k = 2 : Cambia 2 individuos
#   k = 3 : Cambia 4 individuos
#   k = 4 : Cambia 6 individuos
#   k = 5 : Cambia 8 individuos
estructura_entornos = [1, 2, 3, 4, 5]

if isRandom:
    precio_venta = constantes.precio_venta_random
    precio_compra = constantes.precio_compra_random
    r = constantes.r_random
else:
    precio_venta = constantes.precio_venta
    precio_compra = constantes.precio_compra
    r = constantes.r

evaluaciones = np.tile(np.array([0 for _ in range(numero_repeticiones)], dtype=np.float64), (3, 1))
dinero = np.tile(np.array([0 for _ in range(numero_repeticiones)], dtype=np.float64), (3, 1))


def entorno(solucion_actual, granularidad, k):
    entorno_soluciones = []
    n = len(solucion_actual)
    for indices in combinations(range(n), estructura_entornos[k]):
        new_vector_suma = solucion_actual[:]
        new_vector_resta = solucion_actual[:]
        for i in indices:
            new_vector_suma[i] += granularidad
            if new_vector_suma[i] > 100:
                new_vector_suma[i] = 100
            new_vector_resta[i] -= granularidad
            if new_vector_resta[i] < -100:
                new_vector_resta[i] = -100
        yield new_vector_suma
        yield new_vector_resta
    return entorno_soluciones


def busqueda_elmejor(solucion_par, k, granularidad):
    solucion_inicial = solucion_par
    solucion_actual = solucion_inicial
    best_dinero_acumulado = []
    best_bateria_hora = []
    max_dinero = 0
    num_evaluaciones = 0

    while True:  # Repetir
        mejor_vecino = solucion_actual
        dinero_vecino, dinero_acumulado_vecino, bateria_hora_vecino = base.funcion_evaluacion(mejor_vecino, isRandom)

        for s_prima in entorno(solucion_actual, granularidad, k):  # Repetir para toda S' perteneciente a E(S_act)
            # Si el objetivo(s_prima) es mejor que objetivo(mejor_vecino)

            dinero_sprima, dinero_acumulado_sprima, bateria_hora_sprima = base.funcion_evaluacion(s_prima, isRandom)

            if dinero_sprima > dinero_vecino:
                mejor_vecino = s_prima  # Actualizamos el mejor vecino
                dinero_vecino, dinero_acumulado_vecino, bateria_hora_vecino = base.funcion_evaluacion(mejor_vecino,
                                                                                                      isRandom)
                num_evaluaciones += 1
                best_dinero_acumulado = dinero_acumulado_vecino
                best_bateria_hora = bateria_hora_vecino
                max_dinero = dinero_vecino

        # Fin-Para
        # Si el objetivo(mejor_vecino) es mejor que objetivo(solucion_actual)
        if dinero_vecino > base.funcion_evaluacion(solucion_actual, isRandom)[0]:
            solucion_actual = mejor_vecino  # Actualiza solucion_actual

        if dinero_vecino <= base.funcion_evaluacion(solucion_actual, isRandom)[0]:
            break

    return max_dinero, best_dinero_acumulado, best_bateria_hora, num_evaluaciones, solucion_actual  # Devuelve la solucion actual


def busqueda_elmejor_vnd(semilla, granularidad):
    random.seed(semilla)

    solucion_inicial = base.generar_inicial(semilla, 24, granularidad)
    solucion_actual = solucion_inicial
    best_dinero_acumulado = []
    best_bateria_hora = []
    max_dinero = 0
    num_evaluaciones = 0
    k = 0

    while k < len(estructura_entornos):  # Repetir, hasta que k=kmax, la siguiente secuencia:
        #print(f"K = {k}")
        # a) Exploración del entorno: Encontrar la mejor solución s' del k-ésimo entorno de s.
        dinero_obtenido, dinero_acumulado, bateria_hora, n_evaluaciones, solucion_obtenida = busqueda_elmejor(
            solucion_inicial, k, granularidad)

        # b) Moverse o no: Si la solución obtenida s' es mejor que s, hacer s <- s' y k <- 1;
        #    en otro caso, hacer k <- k + 1
        num_evaluaciones += n_evaluaciones + 1
        if dinero_obtenido > base.funcion_evaluacion(solucion_actual, isRandom)[0]:
            solucion_actual = solucion_obtenida
            max_dinero = dinero_obtenido
            best_dinero_acumulado = dinero_acumulado
            best_bateria_hora = bateria_hora
            k = 0
        else:
            k += 1

    return max_dinero, best_dinero_acumulado, best_bateria_hora, num_evaluaciones, solucion_actual


def grafica_elmejor_vnd():
    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        ingresos_granularidad = np.tile(np.array([0 for _ in range(24)], dtype=np.float64), (3, 1))
        for g in range(len(granularidades)):
            dinero_mejor, dinero_acumulado, bateria_hora, num_evaluaciones_mejor, solucion = \
                busqueda_elmejor_vnd(semillas[i], granularidades[g])

            ingresos_granularidad[g] = dinero_acumulado

            dinero[g, i] = dinero_mejor
            evaluaciones[g, i] = num_evaluaciones_mejor

            # Dinero acumulado en cada hora
            fig, ax = plt.subplots()
            plt.title(f"Búsqueda El Mejor. G = {granularidades[g]}, S = {semillas[i]}")
            ax.set_xticks(range(0, 23, 1))
            ln0 = ax.plot([j for j in range(24)], [cent / 100 for cent in dinero_acumulado],
                          label="Dinero Acumulado")
            ax.scatter([j for j in range(24)], [cent / 100 for cent in dinero_acumulado])

            # Capacidad de la bateria en cada hora
            ax1 = ax.twinx()
            ln1 = ax1.plot([j for j in range(24)], bateria_hora, c='orange', label="Bateria")
            ax1.scatter([j for j in range(24)], bateria_hora, c='orange')
            ax.set_xlabel("Horas")
            ax.set_ylabel("Euros (€)")
            ax1.set_ylabel("MW")
            ax1.set(ylim=ax.get_ylim())
            leg = ln0 + ln1
            labs = [legend.get_label() for legend in leg]
            plt.legend(leg, labs, loc='upper center', bbox_to_anchor=(0.5, 1.17), ncol=3)
            if not isRandom:
                plt.savefig(f'.\\graficas\\ProblemaReal\\'
                            f'best_search_VND_g{granularidades[g]}_s{semillas[i]}_ProblemaReal.png')
            else:
                plt.savefig(f'.\\graficas\\ProblemaAleatorio\\'
                            f'best_search_VND_g{granularidades[g]}_s{semillas[i]}_ProblemaAleatorio.png')
            plt.show()  # Mostramos la gráfica
            plt.close()
            print(solucion)
        # Grafica de ingresos de las tres granularidades:
        fig, ax = plt.subplots()
        plt.title(f"Comparacion de ingresos por granularidad\n Semilla: {semillas[i]}")
        for granularidad in range(3):
            ln0 = ax.plot([j for j in range(24)], [cent / 100 for cent in ingresos_granularidad[granularidad, :]],
                          label=f"Ingresos con granularidad = {granularidades[granularidad]}")
            ax.scatter([j for j in range(24)], [cent / 100 for cent in ingresos_granularidad[granularidad, :]])

        plt.legend()
        ax.set_xticks(range(0, 24, 1))
        plt.xlabel("Horas")
        plt.ylabel("Euros (€)")

        if not isRandom:
            plt.savefig(f'.\\graficas\\ProblemaReal\\ingresos-granularidad\\'
                        f'best_search_VND_s{semillas[i]}_ProblemaReal.png')
        else:
            plt.savefig(f'.\\graficas\\ProblemaAleatorio\\ingresos-granularidad\\'
                        f'best_search_VND_s{semillas[i]}_ProblemaAleatorio.png')

        plt.show()
        plt.close()

    # Generamos los datos obtenidos de la búsqueda
    for gr in range(3):
        data = {
            'Media Evaluaciones': [statistics.mean(evaluaciones[gr, :])],
            'Mejor Evaluación': [min(evaluaciones[gr, :])],
            'Desviación Evaluaciones': [statistics.stdev(evaluaciones[gr, :])],
            'Media Dinero (€)': [round(statistics.mean(dinero[gr, :]) / 100, 2)],
            'Mejor Dinero (€)': [round(max(dinero[gr, :]) / 100, 2)],
            'Desviación Dinero (€)': [round(statistics.stdev(dinero[gr, :]) / 100, 2)]
        }

        # Opciones de Pandas para mostrar la tabla completa en la consola
        pd.set_option('display.max_rows', None)
        pd.set_option('display.max_columns', None)
        pd.set_option('display.width', None)
        pd.set_option('display.max_colwidth', None)

        # Mostramos los datos obtenidos
        print(f"Granularidad: {granularidades[gr]}")
        print(pd.DataFrame(data))


grafica_elmejor_vnd()
