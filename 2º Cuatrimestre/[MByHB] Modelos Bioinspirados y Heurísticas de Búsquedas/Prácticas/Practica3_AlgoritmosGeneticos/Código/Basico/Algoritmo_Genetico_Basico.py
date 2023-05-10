import statistics

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import Utils


def mutacion(individuo, porcentaje):
    ind_mutado = individuo.copy()
    if np.random.random() <= porcentaje:
        # Obtiene el valor de la posición asignada
        posicion = np.random.randint(0, 24)
        valor = individuo[posicion] * np.random.uniform(0, 10) / 100
        valor = 10
        # Sumar o Restar
        if np.random.uniform() < 0.5:
            ind_mutado[posicion] += valor
        else:
            ind_mutado[posicion] -= valor

        # Comprobamos que no supere los límites
        if ind_mutado[posicion] > 100:
            ind_mutado[posicion] = 100
        elif ind_mutado[posicion] < -100:
            ind_mutado[posicion] = -100
    return ind_mutado


def cruce(individuo1, individuo2):
    pos1 = np.random.randint(0, len(individuo1) - 1)
    pos2 = np.random.randint(0, len(individuo1) - 1)
    min_pos = pos2 if pos2 < pos1 else pos1
    max_pos = pos2 if pos2 > pos1 else pos1
    # print(f"Puntos de Corte: {min_pos},{max_pos}")
    hijo1 = np.append(individuo1[0:min_pos].copy(), np.append(individuo2[min_pos:max_pos].copy(),
                                                              individuo1[max_pos:len(individuo1)].copy()))
    hijo2 = np.append(individuo2[0:min_pos].copy(), np.append(individuo1[min_pos:max_pos].copy(),
                                                              individuo2[max_pos:len(individuo2)].copy()))
    return hijo1, hijo2


def torneo(valores_cromosomas, k):
    probabilidades = valores_cromosomas / sum(valores_cromosomas)
    indices = np.random.choice(a=np.array([i for i in range(len(valores_cromosomas))]),
                               size=k, replace=False,
                               p=probabilidades)
    return list(indices)


def elitismo(poblacion, k, isRandom):
    # Escoge los 5 mejores individuos.
    valores, _, _ = Utils.fitnessPoblacion(poblacion, isRandom)
    # Obtener los índices que ordenan el vector de forma descendente
    indices_descendentes = np.argsort(-valores)
    # Obtener los 5 primeros índices (los índices de los 5 mayores números)
    top_5_indices = indices_descendentes[:k]

    return poblacion[top_5_indices].copy()

def algoritmo_genetico_generacional_exp(semilla, poblacion_inicial, numIteraciones, mut, isRandom):
    np.random.seed(semilla)
    t = 0
    # Inicializar P(t)
    poblacion = Utils.inicializar_poblacion(poblacion_inicial)
    # Evaluar P(t)
    valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, isRandom)
    indice_maximo = np.argmax(valores_poblacion)
    indice_minimo = np.argmin(valores_poblacion)

    # Obtenemos el mejor individuo de la población inicial.
    mejor_individuo = poblacion[indice_maximo]
    mejor_valor = valores_poblacion[indice_maximo]
    historicoMejor = [mejor_valor]
    mejorValorAcumulado = [mejor_valor]

    # Obtenemos el peor individuo de la poblacion inicial
    peor_individuo = poblacion[indice_minimo]
    peor_valor = valores_poblacion[indice_minimo]
    historicoPeor = [peor_valor]

    # Evaluaciones
    # Inicializamos el numero de evaluaciones a la longitud de la poblacion porque la hemos evaluado inicialmente.
    numero_evaluaciones = len(poblacion)

    while t < numIteraciones:
        t = t + 1
        # Seleccionamos la élite de la población:
        elite = elitismo(poblacion, Utils.ELITE, isRandom)
        numero_evaluaciones += len(poblacion) # Evaluamos a toda la poblacion para escoger la élite.

        # Seleccionar los índices de los padres (K=3)
        # 2 veces torneo
        candidatos = np.array([torneo(valores_poblacion, int(Utils.K * poblacion_inicial)) for _ in
                               range(poblacion_inicial)])

        # Elegimos a los L=2 mejores de cada trío de padres
        padres = np.zeros(shape=(poblacion_inicial-Utils.ELITE, 2), dtype=int)
        # Generamos el numero de la población menos el número de individuos
        # de la élite parejas.
        for i in range(poblacion_inicial-Utils.ELITE):
            padres[i] = candidatos[i][np.argsort(valores_poblacion[candidatos[i]])[-2:]]

        # Recombinar P(t)
        hijos = np.zeros(shape=(poblacion_inicial, 24), dtype=int)
        # Añado la élite a la siguiente generación:
        hijos[0:Utils.ELITE, :] = elite
        for i in range(Utils.ELITE, poblacion_inicial, 2):
            h1, h2 = cruce(poblacion[padres[i-Utils.ELITE][0].copy()],
                           poblacion[padres[Utils.ELITE][1].copy()])
            hijos[i] = h1
            hijos[i + 1] = h2

        # Mutar P(t)
        hijos_mutados = np.apply_along_axis(mutacion, 1, hijos.copy(), mut)
        poblacion = hijos_mutados

        # Evaluar P(t)
        valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, isRandom)
        numero_evaluaciones += len(poblacion)
        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)

        # Obtenemos el mejor individuo de la población.
        if mejor_valor < valores_poblacion[indice_maximo]:
            mejor_individuo = poblacion[indice_maximo].copy()
            mejor_valor = valores_poblacion[indice_maximo].copy()
            historicoMejor.append(mejor_valor)
            historicoPeor.append(peor_valor)
            t = 0   # Cuando mejora, reiniciamos el contador de iteraciones.

        mejorValorAcumulado.append(mejor_valor)

        if peor_valor < valores_poblacion[indice_minimo]:
            # Obtenemos el peor individuo de la poblacion
            peor_valor = valores_poblacion[indice_minimo].copy()

    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo, numero_evaluaciones


def algoritmo_genetico_generacional(semilla, isRandom):
    np.random.seed(semilla)
    t = 0
    # Inicializar P(t)
    poblacion = Utils.inicializar_poblacion(Utils.POBLACION_INICIAL)
    # Evaluar P(t)
    valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, isRandom)
    indice_maximo = np.argmax(valores_poblacion)
    indice_minimo = np.argmin(valores_poblacion)

    # Obtenemos el mejor individuo de la población inicial.
    mejor_individuo = poblacion[indice_maximo]
    mejor_valor = valores_poblacion[indice_maximo]
    historicoMejor = [mejor_valor]
    mejorValorAcumulado = [mejor_valor]

    # Obtenemos el peor individuo de la poblacion inicial
    peor_individuo = poblacion[indice_minimo]
    peor_valor = valores_poblacion[indice_minimo]
    historicoPeor = [peor_valor]

    # Evaluaciones
    # Inicializamos el numero de evaluaciones a la longitud de la poblacion porque la hemos evaluado inicialmente.
    numero_evaluaciones = len(poblacion)

    while t < Utils.NUM_ITERACIONES:
        t = t + 1
        # Seleccionamos la élite de la población:
        elite = elitismo(poblacion, Utils.ELITE, isRandom)
        numero_evaluaciones += len(poblacion) # Evaluamos a toda la poblacion para escoger la élite.

        # Seleccionar los índices de los padres (K=3)
        candidatos = np.array([torneo(valores_poblacion, int(Utils.K * Utils.POBLACION_INICIAL)) for _ in
                               range(Utils.POBLACION_INICIAL)])

        # Elegimos a los L=2 mejores de cada trío de padres
        padres = np.zeros(shape=(Utils.POBLACION_INICIAL-Utils.ELITE, 2), dtype=int)
        # Generamos el numero de la población menos el número de individuos
        # de la élite parejas.
        for i in range(Utils.POBLACION_INICIAL-Utils.ELITE):
            padres[i] = candidatos[i][np.argsort(valores_poblacion[candidatos[i]])[-2:]]

        # Recombinar P(t)
        hijos = np.zeros(shape=(Utils.POBLACION_INICIAL, 24), dtype=int)
        # Añado la élite a la siguiente generación:
        hijos[0:Utils.ELITE, :] = elite
        for i in range(Utils.ELITE, Utils.POBLACION_INICIAL, 2):
            h1, h2 = cruce(poblacion[padres[i-Utils.ELITE][0].copy()],
                           poblacion[padres[Utils.ELITE][1].copy()])
            hijos[i] = h1
            hijos[i + 1] = h2

        # Mutar P(t)
        hijos_mutados = np.apply_along_axis(mutacion, 1, hijos.copy(), Utils.PORCENTAJE_MUTACION)
        poblacion = hijos_mutados

        # Evaluar P(t)
        valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, isRandom)
        numero_evaluaciones += len(poblacion)
        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)

        # Obtenemos el mejor individuo de la población.
        if mejor_valor < valores_poblacion[indice_maximo]:
            mejor_individuo = poblacion[indice_maximo].copy()
            mejor_valor = valores_poblacion[indice_maximo].copy()
            historicoMejor.append(mejor_valor)
            historicoPeor.append(peor_valor)
            t = 0   # Cuando mejora, reiniciamos el contador de iteraciones.

        mejorValorAcumulado.append(mejor_valor)

        if peor_valor < valores_poblacion[indice_minimo]:
            # Obtenemos el peor individuo de la poblacion
            peor_valor = valores_poblacion[indice_minimo].copy()

    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo, numero_evaluaciones

def experimentar():
    mejor_valor = 0
    data = {}
    for iteraciones in Utils.NUMITER_EXP:
        for poblacion in Utils.POBINICIAL_EXP:
            for mut in Utils.PTAJE_MUT:
                valores = [0 for i in range(len(Utils.SEMILLAS))]
                for semilla in range(len(Utils.SEMILLAS)):
                    valor = algoritmo_genetico_generacional_exp(Utils.SEMILLAS[semilla], poblacion, iteraciones, mut, isRandom=False)[0]
                    valores[semilla] = valor
                    media_valores = statistics.mean(valores)
                    if semilla >= 2 and media_valores > mejor_valor:
                        mejor_valor = valor
                        data = {"Iteraciones": [iteraciones],
                                "Poblacion Inicial": [poblacion],
                                "Porcentaje Mutacion": [mut],
                                "Valor Obtenido": [mejor_valor]}
                        # Opciones de Pandas para mostrar la tabla completa en la consola
                        pd.set_option('display.max_rows', None)
                        pd.set_option('display.max_columns', None)
                        pd.set_option('display.width', None)
                        pd.set_option('display.max_colwidth', None)

                        # Mostramos los datos obtenidos
                        print(pd.DataFrame(data))
    return data
if __name__ == "__main__":
    Utils.grafica(algoritmo_genetico_generacional, israndom=False)
    # experimentar()



