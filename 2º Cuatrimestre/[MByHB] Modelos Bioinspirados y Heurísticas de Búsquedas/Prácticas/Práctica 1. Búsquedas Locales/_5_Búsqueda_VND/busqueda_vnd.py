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

# Granularidades:
# estructura_entornos = [1, 5, 10, 15, 20]

precio_venta, precio_compra, r = base.get_vectores(isRandom)

evaluaciones = np.array([0] * numero_repeticiones, dtype=np.float64)
dinero = np.array([0] * numero_repeticiones, dtype=np.float64)


def generar_vecino(solucion_actual, granularidad, pos):
    solucion_vecina = solucion_actual.copy()
    suma = pos < 24
    if not suma and solucion_vecina[pos % 24] - granularidad >= -100:
        solucion_vecina[pos % 24] -= granularidad
    elif not suma:
        solucion_vecina[pos % 24] = -100
    elif suma and solucion_vecina[pos % 24] + granularidad <= 100:
        solucion_vecina[pos % 24] += granularidad
    else:
        solucion_vecina[pos % 24] = 100

    return solucion_vecina


def busqueda_elmejor(isRandom, solucion_par, k, estructura_entornos):
    solucion_inicial = solucion_par
    solucion_actual = solucion_inicial
    num_evaluaciones = 1
    dinero_actual = base.funcion_evaluacion(solucion_actual, isRandom)[0]
    best_dinero_acumulado = []
    best_bateria_hora = []
    max_dinero = 0

    while True:  # Repetir
        mejor_vecino = solucion_actual
        num_evaluaciones += 1
        dinero_vecino, dinero_acumulado_vecino, bateria_hora_vecino = base.funcion_evaluacion(mejor_vecino, isRandom)

        for vecino in range(48):  # Repetir para toda S' perteneciente a E(S_act)
            # Si el objetivo(s_prima) es mejor que objetivo(mejor_vecino)
            s_prima = generar_vecino(solucion_actual, estructura_entornos[k], vecino)
            num_evaluaciones += 1
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
        if dinero_vecino > dinero_actual:
            solucion_actual = mejor_vecino  # Actualiza solucion_actual
            num_evaluaciones += 1
            dinero_actual = base.funcion_evaluacion(solucion_actual, isRandom)[0]

        if dinero_vecino <= dinero_actual:
            break

    return max_dinero, best_dinero_acumulado, best_bateria_hora, num_evaluaciones, solucion_actual  # Devuelve la solucion actual


def busqueda_elmejor_vnd(isRandom, semilla, estructura_entornos):
    random.seed(semilla)

    solucion_inicial = base.generar_inicial(semilla, 24, estructura_entornos[0])
    solucion_actual = solucion_inicial
    num_evaluaciones = 1
    dinero_actual = base.funcion_evaluacion(solucion_actual, isRandom)[0]
    best_dinero_acumulado = []
    best_bateria_hora = []
    max_dinero = 0

    k = 0

    while k < len(estructura_entornos):  # Repetir, hasta que k=kmax, la siguiente secuencia:
        #print(f"K = {k}")
        # a) Exploración del entorno: Encontrar la mejor solución s' del k-ésimo entorno de s.
        dinero_obtenido, dinero_acumulado, bateria_hora, n_evaluaciones, solucion_obtenida = busqueda_elmejor(
            isRandom, solucion_actual, k, estructura_entornos)
        # print(f"dinero_btenido = {dinero_obtenido}")
        # print(f"dinero_actual = {dinero_actual}")
        # b) Moverse o no: Si la solución obtenida s' es mejor que s, hacer s <- s' y k <- 1;
        #    en otro caso, hacer k <- k + 1
        num_evaluaciones += n_evaluaciones
        if dinero_obtenido > dinero_actual:
            solucion_actual = solucion_obtenida
            num_evaluaciones += 1
            dinero_actual, best_dinero_acumulado, best_bateria_hora = base.funcion_evaluacion(solucion_actual, isRandom)
            max_dinero = dinero_obtenido
            k = 0
        else:
            k += 1

    return max_dinero, best_dinero_acumulado, best_bateria_hora, num_evaluaciones, solucion_actual


def grafica_elmejor_vnd():
    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        ingresos_granularidad = np.tile(np.array([0 for _ in range(24)], dtype=np.float64), (3, 1))
        dinero_mejor, dinero_acumulado, bateria_hora, num_evaluaciones_mejor, solucion = \
            busqueda_elmejor_vnd(isRandom, semillas[i], [1, 5, 10, 15, 20])

        dinero[i] = dinero_mejor
        evaluaciones[i] = num_evaluaciones_mejor

        # Dinero acumulado en cada hora
        fig, ax = plt.subplots()
        plt.title(f"Búsqueda El Mejor VND. S = {semillas[i]}")
        ax.set_xticks(range(0, 24, 1))
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
        # ax1.set(ylim=ax.get_ylim())
        leg = ln0 + ln1
        labs = [legend.get_label() for legend in leg]
        plt.legend(leg, labs, loc='upper center', bbox_to_anchor=(0.5, 1.17), ncol=3)
        if not isRandom:
            plt.savefig(f'.\\graficas\\ProblemaReal\\'
                        f'best_search_VND_s{semillas[i]}_ProblemaReal.png')
        else:
            plt.savefig(f'.\\graficas\\ProblemaAleatorio\\'
                        f'best_search_VND_s{semillas[i]}_ProblemaAleatorio.png')
        plt.show()  # Mostramos la gráfica
        plt.close()
        print(solucion)

    # Generamos los datos obtenidos de la búsqueda
    data = {
        'Media Evaluaciones': [statistics.mean(evaluaciones[:])],
        'Mejor Evaluación': [min(evaluaciones[:])],
        'Desviación Evaluaciones': [statistics.stdev(evaluaciones[:])],
        'Media Dinero (€)': [round(statistics.mean(dinero[:]) / 100, 2)],
        'Mejor Dinero (€)': [round(max(dinero[:]) / 100, 2)],
        'Desviación Dinero (€)': [round(statistics.stdev(dinero[:]) / 100, 2)]
    }

    # Opciones de Pandas para mostrar la tabla completa en la consola
    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_columns', None)
    pd.set_option('display.width', None)
    pd.set_option('display.max_colwidth', None)

    # Mostramos los datos obtenidos
    print(pd.DataFrame(data))


if __name__ == "__main__":
    grafica_elmejor_vnd()
