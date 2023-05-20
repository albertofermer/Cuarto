import statistics
import pandas as pd
import numpy as np
import Utils

def mutacion(individuo,mut):
    ind_mutado = individuo.copy()
    if np.random.random() <= mut:
        # Obtiene el valor de la posición asignada
        posicion = np.random.randint(0, 24)
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
    # print(f"Cruce: {individuo1} + {individuo2} =\n {hijo1} y {hijo2}")
    return hijo1, hijo2


def torneo(valores_cromosomas, k):

    valores_cromosomas[valores_cromosomas < 0] = 0
    probabilidades = valores_cromosomas / sum(valores_cromosomas)
    indices = np.random.choice(a=np.array([i for i in range(len(valores_cromosomas))]),
                               size=k, replace=True,
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

def clearing(poblacion, radio, kappa, israndom):
    # Realizamos la seleccion sobre los individuos dominantes, por tanto, hay que limpiar a los individuos de cada nicho.
    # Obtenemos los valores del fitness de la poblacion
    fitness_poblacion = Utils.fitnessPoblacion(poblacion,israndom)[0]
    # Clasificación de la población de forma descendente
    indices_ordenados = np.argsort(-fitness_poblacion)
    # Se escoge al mejor individuo y se compara con el resto de forma descendente. Aquellos individuos que estén dentro de
    # su radio, quedan eliminados.
    for i in range(len(fitness_poblacion)):
        if fitness_poblacion[indices_ordenados[i]] > 0:
            numGanadores = 1
            for j in range(i+1,len(fitness_poblacion)):
                if fitness_poblacion[indices_ordenados[j]] > 0 and \
                        Utils.distanciaEuclidea(poblacion[indices_ordenados[i]], poblacion[indices_ordenados[j]]) < radio:
                    if numGanadores < kappa:
                        numGanadores += 1
                    else:
                        fitness_poblacion[indices_ordenados[j]] = 0
    # print(fitness_poblacion)
    return poblacion[fitness_poblacion[indices_ordenados] != 0] # Devuelve los individos que no han sido eliminados.

def algoritmo_genetico_generacional_multimodal_exp(semilla, pob, mut, generaciones, radio, kappa, isRandom):
    np.random.seed(semilla)
    t = 0
    # Inicializar P(t)
    poblacion = Utils.inicializar_poblacion(pob)
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

    numero_evaluaciones = len(poblacion)

    # Multimodal
    P = 0   # Numero de generaciones que han transcurrido.
    while t < Utils.NUM_ITERACIONES_MULTIMODAL:
        t = t + 1
        # Seleccionamos la élite de la población:
        elite = elitismo(poblacion, Utils.ELITE, isRandom)
        numero_evaluaciones += len(poblacion)

        # Realizamos CLEARING:
        if P >= generaciones:
            # print(f"Antes: {len(poblacion)}")
            P = 0
            numero_evaluaciones += len(poblacion)
            poblacion = clearing(poblacion, radio, kappa, isRandom)

            valores_poblacion = Utils.fitnessPoblacion(poblacion, isRandom)[0]
            numero_evaluaciones += len(poblacion)

        # Seleccionar los índices de los padres (K=3)
        candidatos = np.array([torneo(valores_poblacion, 3) for _ in
                               range(pob)])

        # Elegimos a los L=2 mejores de cada trío de padres
        padres = np.zeros(shape=(pob-Utils.ELITE, 2), dtype=int)
        # Generamos el numero de la población menos el número de individuos
        # de la élite parejas.
        for i in range(pob-Utils.ELITE):
            padres[i] = candidatos[i][np.argsort(valores_poblacion[candidatos[i]])[-2:]]

        # Recombinar P(t)
        hijos = np.zeros(shape=(pob, 24), dtype=int)
        # Añado la élite a la siguiente generación:
        hijos[0:Utils.ELITE, :] = elite
        for i in range(Utils.ELITE, pob, 2):
            if np.random.uniform() < 0.8: # Probabilidad de cruce del 80%
                h1, h2 = cruce(poblacion[padres[i-Utils.ELITE][0].copy()],
                               poblacion[padres[Utils.ELITE][1].copy()])
            else:   # Si no, se copian como hijos.
                h1 = poblacion[padres[i-Utils.ELITE][0].copy()]
                h2 = poblacion[padres[Utils.ELITE][0].copy()]
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
            t = 0   # Cuando mejora, reiniciamos el contador de iteraciones.

        P += 1 # Aumentamos una generación

    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo, numero_evaluaciones


def algoritmo_genetico_generacional_multimodal(semilla, isRandom):
    np.random.seed(semilla)
    t = 0
    # Inicializar P(t)
    poblacion = Utils.inicializar_poblacion(Utils.POB_INICIAL_MULTIMODAL)
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

    numero_evaluaciones = len(poblacion)

    # Multimodal
    P = 0   # Numero de generaciones que han transcurrido.
    while t < Utils.NUM_ITERACIONES_MULTIMODAL:
        t = t + 1
        # Seleccionamos la élite de la población:
        elite = elitismo(poblacion, Utils.ELITE, isRandom)
        numero_evaluaciones += len(poblacion)

        # Realizamos CLEARING:
        if P >= Utils.NUMERO_GENERACIONES_CLEARING:
            # print(f"Antes: {len(poblacion)}")
            P = 0
            numero_evaluaciones += len(poblacion)
            poblacion = clearing(poblacion, Utils.RADIO_CLEARING, Utils.KAPPA, isRandom)
            valores_poblacion = Utils.fitnessPoblacion(poblacion, isRandom)[0]
            numero_evaluaciones += len(poblacion)

        # Seleccionar los índices de los padres (K=3)
        candidatos = np.array([torneo(valores_poblacion, 3) for _ in
                               range(Utils.POB_INICIAL_MULTIMODAL)])

        # Elegimos a los L=2 mejores de cada trío de padres
        padres = np.zeros(shape=(Utils.POB_INICIAL_MULTIMODAL-Utils.ELITE, 2), dtype=int)
        # Generamos el numero de la población menos el número de individuos
        # de la élite parejas.
        for i in range(Utils.POB_INICIAL_MULTIMODAL-Utils.ELITE):
            padres[i] = candidatos[i][np.argsort(valores_poblacion[candidatos[i]])[-2:]]

        # Recombinar P(t)
        hijos = np.zeros(shape=(Utils.POB_INICIAL_MULTIMODAL, 24), dtype=int)
        # Añado la élite a la siguiente generación:
        hijos[0:Utils.ELITE, :] = elite
        for i in range(Utils.ELITE, Utils.POB_INICIAL_MULTIMODAL , 2):
            if np.random.uniform() < 0.8: # Probabilidad de cruce del 80%
                h1, h2 = cruce(poblacion[padres[i-Utils.ELITE][0].copy()],
                               poblacion[padres[Utils.ELITE][1].copy()])
            else:   # Si no, se copian como hijos.
                h1 = poblacion[padres[i-Utils.ELITE][0].copy()]
                h2 = poblacion[padres[Utils.ELITE][0].copy()]
            hijos[i] = h1
            hijos[i + 1] = h2

        # Mutar P(t)
        hijos_mutados = np.apply_along_axis(mutacion, 1, hijos.copy(), Utils.MUTACION_MULTIMODAL)
        poblacion = hijos_mutados.copy()
        # print(f"{poblacion}")
        # Evaluar P(t)
        valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, isRandom)
        numero_evaluaciones += len(poblacion)
        indice_maximo = np.argmax(valores_poblacion)
        mejor_valor = valores_poblacion[indice_maximo]

        # Obtenemos el mejor individuo de la población.
        if mejor_valor < valores_poblacion[indice_maximo]:
            mejor_individuo = poblacion[indice_maximo].copy()
            mejor_valor = valores_poblacion[indice_maximo]
            t = 0   # Cuando mejora, reiniciamos el contador de iteraciones.


        valores_poblacion, _ , _ = Utils.fitnessPoblacion(poblacion, isRandom)
        numero_evaluaciones += len(poblacion)
        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)
        peor_valor = valores_poblacion[indice_minimo].copy()
        mejor_valor = valores_poblacion[indice_maximo].copy()
        P += 1 # Aumentamos una generación
        mejorValorAcumulado.append(mejor_valor.copy())
        historicoMejor.append(mejor_valor.copy())
        historicoPeor.append(peor_valor.copy())
    # Clearing final con kappa = 1
    print(f"Numero Nichos Final: {len(clearing(poblacion, Utils.RADIO_CLEARING, 1, isRandom))}")
    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo, numero_evaluaciones

def experimentar():
    mejor_valor = 0
    best_data = {}
    data = {}
    for poblacion in Utils.POBINICIAL_EXP:
        for mut in Utils.PTAJE_MUT:
            for generaciones in Utils.NUMGENCLEARING_EXP:
                for kappa in Utils.KAPPA_EXP:
                    valores = [0 for i in range(len(Utils.SEMILLAS))]
                    for semilla in range(len(Utils.SEMILLAS)):
                            valor = algoritmo_genetico_generacional_multimodal_exp(Utils.SEMILLAS[semilla],
                                                                                   poblacion, mut, generaciones, Utils.RADIO_CLEARING,
                                                                                   Utils.KAPPA,
                                                                                   isRandom=False)[0]
                            valores[semilla] = valor
                            media_valores = statistics.mean(valores)
                            data = {"Poblacion Inicial ": [poblacion],
                                         "Porcentaje Mutacion ": [mut],
                                         "Generaciones": [generaciones],
                                         "Radio": [Utils.RADIO_CLEARING],
                                         "Kappa": [kappa],
                                         "Valor Obtenido": [valor]}
                            # Opciones de Pandas para mostrar la tabla completa en la consola
                            pd.set_option('display.max_rows', None)
                            pd.set_option('display.max_columns', None)
                            pd.set_option('display.width', None)
                            pd.set_option('display.max_colwidth', None)
                            print(pd.DataFrame(data))

                            if semilla >= 2 and media_valores > mejor_valor:
                                mejor_valor = media_valores
                                best_data = {"Poblacion Inicial " : [poblacion],
                                        "Porcentaje Mutacion " : [mut],
                                        "Generaciones": [generaciones],
                                        "Radio": [Utils.RADIO_CLEARING],
                                        "Kappa": [kappa],
                                        "Valor Obtenido": [mejor_valor]}
                                # Opciones de Pandas para mostrar la tabla completa en la consola
                                pd.set_option('display.max_rows', None)
                                pd.set_option('display.max_columns', None)
                                pd.set_option('display.width', None)
                                pd.set_option('display.max_colwidth', None)

                                # Mostramos los datos obtenidos
                                print(pd.DataFrame(best_data))
    return best_data
if __name__ == "__main__":
    # data = experimentar()
    # print("\n")
    # print("BEST PARAMETERS:\n ")
    # print(pd.DataFrame(data))
    Utils.grafica(algoritmo_genetico_generacional_multimodal, israndom=False)
    # print(algoritmo_genetico_generacional_multimodal(12456, False)[0])
