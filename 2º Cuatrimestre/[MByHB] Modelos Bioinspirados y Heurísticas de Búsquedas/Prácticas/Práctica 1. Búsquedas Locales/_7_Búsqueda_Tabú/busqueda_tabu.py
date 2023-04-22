import random

import constantes
import funciones_base as base
import statistics
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

isRandom = not False
numero_repeticiones = 5

# Constantes
capacidad_bateria = constantes.capacidad_bateria
# granularidades = constantes.granularidad
semillas = constantes.semillas
precio_venta, precio_compra, r = base.get_vectores(isRandom)

evaluaciones = np.array([0]*numero_repeticiones, dtype=np.float64)
dinero = np.array([0]*numero_repeticiones, dtype=np.float64)

# Parámetros Tabú:
'''
Memoria a Corto Plazo: def mov(pos, cantidad)
· Orientado a Atributo: A pos le corresponde la nueva cantidad absoluta.
· Orientado a Movimiento: A pos le corresponde la cantidad anterior mas la nueva cantidad.

Tenencia Tabú:
Cada 1000 iteraciones se reinicia. La longitud de la cola se divide/multiplica por 2.

🟢️ Criterio de Aspiración: Que sea mejor que la mejor solución

🟢 Lista de Candidatos: No existe una lista de candidatos. Nuestros candidatos será la generación de X vecinos.
Se generan los mismos vecinos que en ES (10).

'''

'''
Generación de Vecinos:
Generaremos un vecino incrementando o decrementando aleatoriamente una posición aleatoria 
de la solución actual.
'''


def GenerarVecino(semilla, solucion, pos):
    granularidad = 10  # En este algoritmo utilizaremos granularidad 10 fija.
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


'''
Matriz de Frecuencias (M):
Se utiliza para almacenar una estadística de las veces que ya hemos estado en un vecino.
Se le dará mayor probabilidad a los valores menos habituales. 
Al comienzo, se inicializa a 1 para que toda la probabilidad sea uniforme.

Como las variables son enteras, se utiliza una matriz bidimensional, como contador de
las veces que la variable i toma el valor k: M[i,k]
'''


def IncrementarPosicion(memoria_frecuencias, solucion):
    mf = memoria_frecuencias.copy()
    valores = [i for i in range(-100, 110, 10)]
    for h in range(len(solucion)):
        mf[h][valores.index(solucion[h])] += 1

    return mf


'''
Greedy Probabilistico:
La solución greedy debe generar soluciones con mayor probabilidad para los valores
que menos veces se han producido en cada hora. 

Para cada hora se deberá calcular las inversas de los valores acumulados y luego
normalizar para tener valores entre 0-1 correspondiente a su probabilidad. Se tirará
un dado y se elige el primer valor que supera dicho valor añadiéndose a la solución
greedy
'''


def GreedyProbabilistico(matrizProbabilidades, granularidad):
    # Calcular Inversas:
    M_invertida = 1 / matrizProbabilidades
    suma_total = sum(np.transpose(M_invertida))  # Vector con la suma por filas
    diag_suma = np.diag(1 / suma_total)  # Diagonalizamos el vector para poder multiplicarlo
    matriz_normalizada = diag_suma.dot(M_invertida)
    valores = [i for i in range(-100, 110, granularidad)]
    solucion_greedy = []
    for hora in range(24):
        numero = random.random()
        suma = 0
        for i in range(-100, 110, granularidad):
            suma += matriz_normalizada[hora, valores.index(i)]
            if numero < suma:
                solucion_greedy.append(i)
                break
    return solucion_greedy


def AddListaTabu(pos, lista_tabu, tenencia, indice):
    lista = lista_tabu.copy()
    if len(lista) < tenencia:
        lista.append(pos)
    else:
        lista[indice % tenencia] = pos
    return lista


'''
Algoritmo Búsqueda Tabú:
Selección de estrategias de reinicialización:
 · Reinicialización construyendo una solución inicial aleatoria : 25%
 · Memoria a Largo Plazo : 50%
 · Reinicialización desde la mejor solución : 25%
'''


def BusquedaTabu(isRandom, semilla, iteraciones_maximas, numero_vecinos, granularidad):
    random.seed(semilla)
    solucion_actual = base.generar_inicial(semilla, 24, granularidad)
    solucion_mejor = solucion_actual
    coste_mejor = base.funcion_evaluacion(solucion_mejor, isRandom)[0]
    M = np.ones((24, 21))
    num_iteraciones = 1
    '''
    Variables de Evaluación
    '''
    num_evaluaciones = 0
    dinero_acumulado = []
    bateria_acumulada = []
    '''
    Lista Tabú
    '''
    tenencia_tabu = 4
    lista_tabu = []

    while num_iteraciones <= iteraciones_maximas:
        hora = -1
        valor = -1
        # Generamos numero_vecinos
        for nv in range(numero_vecinos):
            hora = random.randint(0, 23)
            valor = solucion_actual[hora]
            solucion_prima = GenerarVecino(semilla, solucion_actual, hora)

            # Si supera el criterio de aspiración
            num_evaluaciones += 1
            if base.funcion_evaluacion(solucion_prima, isRandom)[0] > coste_mejor \
                    or (hora, solucion_prima[hora]) not in lista_tabu:
                # Evaluar S'
                num_evaluaciones += 1
                coste_vecino, dinero_acumulado_vecino, bateria_acumulada_vecino = base.funcion_evaluacion(solucion_prima, isRandom)

                if coste_vecino > coste_mejor:
                    coste_mejor = coste_vecino
                    solucion_mejor = solucion_prima
                    dinero_acumulado = dinero_acumulado_vecino
                    bateria_acumulada = bateria_acumulada_vecino

        # END-FOR
        # Realizar el movimiento elegido
        solucion_actual = solucion_mejor

        lista_tabu = AddListaTabu((hora, valor), lista_tabu, tenencia_tabu, num_iteraciones)
        M = IncrementarPosicion(M, solucion_actual)

        '''
        Estimar el número máximo de iteraciones en total. Se realizarán 4 reinicializaciones, es decir,
        una cada Numtotal-iteraciones/4. El tamaño inicial de cada lista tabú será n=4, estos valores
        cambiarán después de las reinicializaciones según se ha comentado. 
        Variar el tamaño de la lista tabú, incrementándola o reduciéndola en un 50% según una decisión
        aleatoria uniforme, empezando la lista desde vacío en cada reinicialización
        '''
        if num_iteraciones % round(iteraciones_maximas / 4) == 0:
            # Reiniciar lista tabú
            rand = random.uniform(0, 1)
            if rand < 0.5:
                tenencia_tabu -= tenencia_tabu * 0.5
            else:
                tenencia_tabu += tenencia_tabu * 0.5
            tenencia_tabu = int(np.ceil(tenencia_tabu))
            #print(f"T: {tenencia_tabu}")
            lista_tabu = []

            '''
            Selección de estrategias de reinicialización:
             · Reinicialización construyendo una solución inicial aleatoria : 25%
             · Memoria a Largo Plazo : 50%
             · Reinicialización desde la mejor solución : 25%
            '''
            num = random.random()
            if num < 0.25:
                #print("Reinicializacion Aleatoria")
                # Reinicialización construyendo una solución inicial aleatoria : 25%
                solucion_actual = base.generar_inicial(semilla, 24, granularidad)
            elif 0.25 <= num < 0.5:
                #print("Reinicializacion Mejor Solucion")
                # Reinicialización desde la mejor solución : 25%
                solucion_actual = solucion_mejor
            else:
                # Memoria a Largo Plazo : 50%
                solucion_actual = GreedyProbabilistico(M, granularidad)
                #print("MLP")

        num_iteraciones += 1
    # END-WHILE
    return coste_mejor, dinero_acumulado, bateria_acumulada, num_evaluaciones, solucion_mejor


def GraficaBusquedaTabu():
    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        dinero_mejor, dinero_acumulado, bateria_hora, num_evaluaciones_mejor, solucion = BusquedaTabu(False,semillas[i], 1000,
                                                                                                      40, 10)
        dinero[i] = dinero_mejor
        evaluaciones[i] = num_evaluaciones_mejor

        # Dinero acumulado en cada hora
        fig, ax = plt.subplots()
        plt.title(f"Búsqueda Tabú. G = {10}, S = {semillas[i]}")
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
                        f'tabu_search_g{10}_s{semillas[i]}_ProblemaReal.png')
        else:
            plt.savefig(f'.\\graficas\\ProblemaAleatorio\\'
                        f'tabu_search_g{10}_s{semillas[i]}_ProblemaAleatorio.png')
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
    print(f"Granularidad: {10}")
    print(pd.DataFrame(data))


if __name__ == "__main__":
    GraficaBusquedaTabu()
