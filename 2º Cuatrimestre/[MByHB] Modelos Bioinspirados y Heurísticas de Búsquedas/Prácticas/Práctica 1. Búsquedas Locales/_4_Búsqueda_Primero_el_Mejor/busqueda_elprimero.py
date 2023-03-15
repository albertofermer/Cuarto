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

evaluaciones = np.tile(np.array([0 for _ in range(numero_repeticiones)], dtype=np.float64), (3, 1))
dinero = np.tile(np.array([0 for _ in range(numero_repeticiones)], dtype=np.float64), (3, 1))

if isRandom:
    precio_venta = constantes.precio_venta_random
    precio_compra = constantes.precio_compra_random
    r = constantes.r_random
else:
    precio_venta = constantes.precio_venta
    precio_compra = constantes.precio_compra
    r = constantes.r


def genera_vecinos(solucion, granularidad, pos):
    solucion_vecina = solucion.copy()
    accion = random.randint(0, 1)  # Elige una accion (decrementar o incrementar)
    if pos % 2 == 0:
        accion = 0
    elif pos % 2 == 1:
        accion = 1

    pos = int(np.floor(pos/2))

    if accion == 0:  # Incrementa
        solucion_vecina[pos] += granularidad
        if solucion_vecina[pos] > 100:
            solucion_vecina[pos] = 100
    else:  # Decrementa
        solucion_vecina[pos] -= granularidad
        if solucion_vecina[pos] < -100:
            solucion_vecina[pos] = -100

    return solucion_vecina


# Algoritmo de busqueda el mejor vecino
def busqueda_primero(semilla, granularidad):
    random.seed(semilla)
    solucion_actual = base.generar_inicial(semilla, 24, granularidad)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0

    while True:  # Repetir
        pos = 0
        while True:  # Repetir
            solucion_vecina = genera_vecinos(solucion_actual, granularidad, pos)  # Genera vecinos
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # o hasta que se haya generado el espacio de busqueda completo
            contador += 2
            if base.funcion_evaluacion(solucion_vecina, isRandom)[0] > base.funcion_evaluacion(mejor_vecino, isRandom)[0]\
                    or pos > 47:

                break
            pos += 1

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        contador += 2

        if base.funcion_evaluacion(solucion_vecina, isRandom)[0] > base.funcion_evaluacion(solucion_actual, isRandom)[0]:
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual


        contador += 2
        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if base.funcion_evaluacion(solucion_vecina, isRandom)[0] <= base.funcion_evaluacion(solucion_actual, isRandom)[0]:
            break

    #print(contador)
    return base.funcion_evaluacion(solucion_actual, isRandom), contador, solucion_actual


def graficas_primero():
    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        ingresos_granularidad = np.tile(np.array([0 for _ in range(24)], dtype=np.float64), (3, 1))
        for g in range(len(granularidades)):
            (dinero_mejor, dinero_acumulado, bateria_hora), num_evaluaciones, solucion = busqueda_primero(semillas[i],
                                                                                                          granularidades[g])

            ingresos_granularidad[g] = dinero_acumulado
            dinero[g, i] = dinero_mejor
            evaluaciones[g, i] = num_evaluaciones

            # Dinero acumulado en cada hora
            fig, ax = plt.subplots()
            plt.title(f"Búsqueda Primero El Mejor. G = {granularidades[g]}, S = {semillas[i]}")
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
            #ax1.set(ylim=ax.get_ylim())
            leg = ln0 + ln1
            labs = [legend.get_label() for legend in leg]
            plt.legend(leg, labs, loc='upper center', bbox_to_anchor=(0.5, 1.17), ncol=3)
            if not isRandom:
                plt.savefig(f'.\\graficas\\ProblemaReal\\'
                            f'firstbest_search_g{granularidades[g]}_s{semillas[i]}_ProblemaReal.png')
            else:
                plt.savefig(f'.\\graficas\\ProblemaAleatorio\\'
                            f'firstbest_search_g{granularidades[g]}_s{semillas[i]}_ProblemaAleatorio.png')
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
                        f'firstbest_search_s{semillas[i]}_ProblemaReal.png')
        else:
            plt.savefig(f'.\\graficas\\ProblemaAleatorio\\ingresos-granularidad\\'
                        f'firstbest_search_s{semillas[i]}_ProblemaAleatorio.png')
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


if __name__ == "__main__":
    graficas_primero()
