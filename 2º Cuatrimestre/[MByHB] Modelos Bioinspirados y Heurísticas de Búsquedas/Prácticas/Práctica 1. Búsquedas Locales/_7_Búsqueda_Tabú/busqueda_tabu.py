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
# granularidades = constantes.granularidad
semillas = constantes.semillas
precio_venta, precio_compra, r = base.get_vectores(isRandom)

evaluaciones = np.tile(np.array([0 for _ in range(numero_repeticiones)], dtype=np.float64), (3, 1))
dinero = np.tile(np.array([0 for _ in range(numero_repeticiones)], dtype=np.float64), (3, 1))

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



def GenerarVecino(solucion, pos):
    granularidad = 10   # En este algoritmo utilizaremos granularidad 10 fija.
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


def InicializarMemoriaDeFrecuencias():
    return np.ones((24, 21))


def IncrementarPosicion(memoria_frecuencias, posicion):
    fila, columna = posicion
    memoria_frecuencias[fila, posicion] += 1
    return memoria_frecuencias


def RuletaInversa():
    return


'''
Greedy Probabilistico:
La solución greedy debe generar soluciones con mayor probabilidad para los valores
que menos veces se han producido en cada hora. 

Para cada hora se deberá calcular las inversas de los valores acumulados y luego
normalizar para tener valores entre 0-1 correspondiente a su probabilidad. Se tirará
un dado y se elige el primer valor que supera dicho valor añadiéndose a la solución
greedy
'''


def GreedyProbabilistico(semilla, cromosoma, matrizProbabilidades):
    random.seed(semilla)
    solucion_greedy = []
    for hora in cromosoma:
        numero = random.random()
        suma = 0
        for i in range(-100, 110, 10):
            suma += matrizProbabilidades(hora, i)
            if numero < suma:
                solucion_greedy.append(i)
                break
    return solucion_greedy


'''
Criterio de Aspiración:
Criterio que permite saltarse el tabú.
En nuestro caso será que la solución actual sea mejor que la solución mejor.
'''


def CriterioDeAspiracion(coste_actual, coste_mejor):
    return coste_actual > coste_mejor


def AddListaTabu(pos, lista_tabu, tenencia, indice):
    if indice < tenencia:
        lista_tabu.append(pos)
    else:
        lista_tabu[indice % tenencia] = pos


'''
Algoritmo Búsqueda Tabú:
Selección de estrategias de reinicialización:
 · Reinicialización construyendo una solución inicial aleatoria : 25%
 · Memoria a Largo Plazo : 50%
 · Reinicialización desde la mejor solución : 25%
'''


def BusquedaTabu(semilla, iteraciones_maximas, numero_vecinos):
    random.seed(semilla)
    solucion_actual = base.generar_inicial(semilla, 24, 10)
    solucion_mejor = solucion_actual
    coste_mejor = base.funcion_evaluacion(solucion_mejor, isRandom)[0]
    M = InicializarMemoriaDeFrecuencias()
    num_iteraciones = 0
    '''
    Lista Tabú
    '''
    tenencia_tabu = 4
    lista_tabu = []

    while(num_iteraciones < iteraciones_maximas):
        '''
        Estimar el número máximo de iteraciones en total. Se realizarán 4 reinicializaciones, es decir,
        una cada Numtotal-iteraciones/4. El tamaño inicial de cada lista tabú será n=4, estos valores
        cambiarán después de las reinicializaciones según se ha comentado.
        
        Variar el tamaño de la lista tabú, incrementándola o reduciéndola en un 50% según una decisión
        aleatoria uniforme, empezando la lista desde vacío en cada reinicialización
        '''
        if num_iteraciones % round(iteraciones_maximas/4):
            # Reiniciar lista tabú
            rand = random.random()
            if rand < 0.5:
                tenencia_tabu -= tenencia_tabu*1/2
            else:
                tenencia_tabu += tenencia_tabu*1/2
            lista_tabu = []

        hora = -1
        # Generamos numero_vecinos
        for nv in range(numero_vecinos):
            hora = random.randint(0, 23)
            solucion_prima = GenerarVecino(solucion_actual, hora)

            # Si supera el criterio de aspiración
            if CriterioDeAspiracion(base.funcion_evaluacion(solucion_prima, isRandom)[0], coste_mejor)\
                    or (hora, solucion_prima[hora]) in lista_tabu:
                # Evaluar S'
                coste_vecino = base.funcion_evaluacion(solucion_prima, isRandom)[0]

                if coste_vecino > coste_mejor:
                    coste_mejor = coste_vecino
                    solucion_mejor = solucion_prima

        # Realizar el movimiento elegido
        solucion_actual = solucion_mejor
        coste_actual = coste_mejor
        lista_tabu = AddListaTabu((hora, solucion_actual[hora]),lista_tabu, tenencia_tabu)
        IncrementarPosicion(M, (hora, solucion_actual[hora]))






