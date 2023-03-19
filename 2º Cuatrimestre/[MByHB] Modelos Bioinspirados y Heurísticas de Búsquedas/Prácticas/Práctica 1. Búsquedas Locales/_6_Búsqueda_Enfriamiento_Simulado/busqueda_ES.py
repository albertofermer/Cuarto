import math
import random

import constantes
import funciones_base as base
import _1_Búsqueda_Greedy.busqueda_greedy as greedy
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

evaluaciones = np.tile(np.array([0] * numero_repeticiones, dtype=np.float64), (3, 1))
dinero = np.tile(np.array([0] * numero_repeticiones, dtype=np.float64), (3, 1))

# Hiperparámetros de Control
mu = [0.1, 0.15, 0.2, 0.25, 0.3]
phi = [0.1, 0.15, 0.2, 0.25, 0.3]
num_vecinos = [5, 10, 15, 20]


def temperatura_inicial(mu_, phi_, s0):
    return (mu_ / (-np.log(phi_))) * base.funcion_evaluacion(s0, isRandom)[0]


def seleccionar_solucion(solucion, granularidad, pos):
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


def enfriamiento_simulado(semilla, granularidad, num_vecinos_, mu_, phi_):
    random.seed(semilla)
    solucion_actual = greedy.greedy(isRandom)[3]  # Solucion Greedy
    solucion_mejor = solucion_actual
    num_evaluaciones = 1
    dinero_mejor = base.funcion_evaluacion(solucion_mejor, isRandom)[0]
    t0 = temperatura_inicial(mu_, phi_, solucion_actual)
    t = t0
    k = 1

    dinero_acumulado = []
    temperatura = [t]
    bateria_acumulada = []

    rechaza = 0
    total = 0

    while k < 300:  # El algoritmo finaliza cuando se alcance un num maximo de iteraciones
        for i in range(num_vecinos_):  # Condicion de Enfriamiento: Cuando se hayan generado un num de vecinos.

            solucion_candidata = seleccionar_solucion(solucion_actual, granularidad, random.randint(0, 23))

            num_evaluaciones += 1
            dinero_candidato = base.funcion_evaluacion(solucion_candidata, isRandom)[0]
            delta = dinero_mejor - dinero_candidato

            if delta < 0 or random.uniform(0, 1) < np.exp(-delta / t):  # Si es mejor la coge
                solucion_actual = solucion_candidata
                num_evaluaciones += 1
                dinero_candidato = base.funcion_evaluacion(solucion_candidata, isRandom)[0]
                # Escogemos la mejor solucion
                if dinero_candidato > dinero_mejor:
                    solucion_mejor = solucion_candidata
                    num_evaluaciones += 1
                    dinero_mejor, dinero_acumulado, bateria_acumulada = base.funcion_evaluacion(solucion_mejor,
                                                                                                isRandom)
            else:
                if k == 1: rechaza += 1

            if k == 1: total += 1

        t = t0 / (1 + k)  # Esquema de Enfriamiento: Esquema de Cauchy
        k += 1
        temperatura.append(t)
    return dinero_mejor, dinero_acumulado, bateria_acumulada, num_evaluaciones, temperatura, solucion_mejor, rechaza / total


def experimentacion_parametros():
    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        for g in range(len(granularidades)):
            for m in range(len(mu)):
                for p in range(len(phi)):
                    dinero_mejor, dinero_acumulado, bateria_hora, num_evaluaciones_mejor, temperatura, solucion, cociente = \
                        enfriamiento_simulado(
                            semillas[i],
                            granularidades[g],
                            100,
                            mu[m],
                            phi[p])
                    # print(cociente)
                    data = {
                        'mu': [mu[m]],
                        'phi': [phi[p]],
                        'empeoramiento / total': [cociente],
                        'T0': [temperatura_inicial(mu[m], phi[p], greedy.greedy(isRandom)[3])]
                    }
                    # print(cociente)
                    if 0.2 <= cociente < 0.21:
                        max_dinero = dinero_mejor
                        max_mu = mu[m]
                        max_phi = phi[p]

                        print(data)
    # print(f"mu = {max_mu} \nphi = {max_phi} \ndinero = {max_dinero}")


def graficas_enfriamiento_simulado(nv, m, p):
    # Llamamos a la funcion de búsqueda:
    temperatura_enfriamiento = []
    for i in range(numero_repeticiones):
        ingresos_granularidad = np.tile(np.array([0 for _ in range(24)], dtype=np.float64), (3, 1))
        for g in range(len(granularidades)):
            dinero_mejor, dinero_acumulado, bateria_hora, num_evaluaciones_mejor, temperatura, solucion, _ = \
                enfriamiento_simulado(
                    semillas[i],
                    granularidades[g],
                    nv,
                    m,
                    p)

            temperatura_enfriamiento = temperatura
            ingresos_granularidad[g] = dinero_acumulado
            dinero[g, i] = dinero_mejor
            evaluaciones[g, i] = num_evaluaciones_mejor

            # Dinero acumulado en cada hora
            fig, ax = plt.subplots()
            plt.title(f"Búsqueda Enfriamiento Simulado. G = {granularidades[g]}, S = {semillas[i]}")
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
            ax1.set(ylim=ax.get_ylim())
            leg = ln0 + ln1
            labs = [legend.get_label() for legend in leg]
            plt.legend(leg, labs, loc='upper center', bbox_to_anchor=(0.5, 1.17), ncol=3)
            if not isRandom:
                plt.savefig(f'.\\graficas\\ProblemaReal\\'
                            f'simulated_anealing_g{granularidades[g]}_s{semillas[i]}_ProblemaReal.png')
            else:
                plt.savefig(f'.\\graficas\\ProblemaAleatorio\\'
                            f'simulated_anealing_g{granularidades[g]}_s{semillas[i]}_ProblemaAleatorio.png')
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
                        f'simulated_anealing_s{semillas[i]}_ProblemaReal.png')
        else:
            plt.savefig(f'.\\graficas\\ProblemaAleatorio\\ingresos-granularidad\\'
                        f'simulated_anealing_s{semillas[i]}_ProblemaAleatorio.png')
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
    # Representa el enfriamiento de la temperatura
    fig, ax = plt.subplots()
    plt.title(f"Esquema de Enfriamiento")
    ax.plot([j for j in range(len(temperatura_enfriamiento))], temperatura_enfriamiento, label="Temperatura")
    plt.legend()
    plt.xlabel("Iteraciones")
    plt.ylabel("T")
    # plt.yscale('log')
    if not isRandom:
        plt.savefig(f'.\\graficas\\ProblemaReal\\esquema-enfriamiento\\'
                    f'simulated_anealing_ProblemaReal.png')
    else:
        plt.savefig(f'.\\graficas\\ProblemaAleatorio\\esquema-enfriamiento\\'
                    f'simulated_anealing_ProblemaAleatorio.png')
    plt.show()
    plt.close()


if __name__ == "__main__":
    if isRandom:
        # experimentacion_parametros()
        graficas_enfriamiento_simulado(15, 0.25, 0.3)
    else:
        # experimentacion_parametros()
        graficas_enfriamiento_simulado(15, 0.3, 0.25)
        # dinero, _, _, _, _, s, _ = enfriamiento_simulado(123456, 1, 20, 0.3, 0.3)
        # print(s)
        # print(f"Dinero = {dinero}")
        # print(f"F(s) = {base.funcion_evaluacion(s, isRandom)[0]}")
        # print(f"D_acum(s) = {base.funcion_evaluacion(s, isRandom)[1]}")
        # print(f"B_acum(s) = {base.funcion_evaluacion(s, isRandom)[2]}")

    # 0.2 // 0.3 -> datos aleatorios
    # 0.3 // 0.2 -> datos reales
