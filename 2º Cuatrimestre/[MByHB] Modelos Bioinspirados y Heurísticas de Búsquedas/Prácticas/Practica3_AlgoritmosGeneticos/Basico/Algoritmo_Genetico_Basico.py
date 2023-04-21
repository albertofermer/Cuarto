import matplotlib.pyplot as plt
import numpy as np

import Utils

'''
Función de mutación del algoritmo genético.

'''


def mutacion(individuo):
    ind_mutado = individuo.copy()
    if np.random.random() <= Utils.PORCENTAJE_MUTACION:
        # Obtiene el valor de la posición asignada
        posicion = np.random.randint(0, 24)
        valor = individuo[posicion] * np.random.uniform(0, 10) / 100
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

def fitness(poblacion):
    dinero = np.zeros(len(poblacion), dtype=float)
    dinero_acumulado = np.zeros((len(poblacion),24), dtype=float)
    bateria_hora = np.zeros((len(poblacion),24), dtype=float)
    for i in range(len(poblacion)):
        dinero[i], dinero_acumulado[i], bateria_hora[i] = Utils.fitness(poblacion[i])
    return dinero, dinero_acumulado, bateria_hora

def inicializar_poblacion(size):
    pob = np.random.randint(0, 23, (size, 24), dtype=int)
    return pob


def torneo(valores_cromosomas, k):
    probabilidades = valores_cromosomas / sum(valores_cromosomas)
    indices = np.random.choice(a=np.array([i for i in range(len(valores_cromosomas))]),
                               size=k, replace=False,
                               p=probabilidades)
    return list(indices)


def elitismo(poblacion, k):
    # Escoge los 5 mejores individuos.
    valores, _, _ = fitness(poblacion)
    # Obtener los índices que ordenan el vector de forma descendente
    indices_descendentes = np.argsort(-valores)
    # Obtener los 5 primeros índices (los índices de los 5 mayores números)
    top_5_indices = indices_descendentes[:k]

    return poblacion[top_5_indices].copy()


def algoritmo_genetico(semilla):
    np.random.seed(semilla)
    t = 0
    # Inicializar P(t)
    poblacion = inicializar_poblacion(Utils.POBLACION_INICIAL)
    # Evaluar P(t)
    #print(poblacion[0])
    valores_poblacion, dinero_acumulado, bateria_acumulada = fitness(poblacion)
    indice_maximo = np.argmax(valores_poblacion)
    indice_minimo = np.argmin(valores_poblacion)


    # Obtenemos el mejor individuo de la población inicial.
    mejor_individuo = poblacion[indice_maximo]
    mejor_valor = valores_poblacion[indice_maximo]
    historicoMejor = [mejor_valor]

    # Obtenemos el peor individuo de la poblacion inicial
    peor_individuo = poblacion[indice_minimo]
    peor_valor = valores_poblacion[indice_minimo]
    historicoPeor = [peor_valor]

    while t < Utils.NUM_ITERACIONES:
        t = t + 1
        # Seleccionamos la élite de la población:
        elite = elitismo(poblacion, Utils.ELITE)

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
            h1, h2 = cruce(poblacion[padres[i-Utils.ELITE][0].copy()].copy(),
                           poblacion[padres[Utils.ELITE][1].copy()].copy())
            hijos[i] = h1
            hijos[i + 1] = h2

        # Mutar P(t)
        hijos_mutados = np.apply_along_axis(mutacion, 1, hijos.copy())
        poblacion = hijos_mutados

        # Evaluar P(t)
        valores_poblacion, dinero_acumulado, bateria_acumulada = fitness(poblacion)

        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)

        # Obtenemos el mejor individuo de la población.
        if mejor_valor < valores_poblacion[indice_maximo]:
            mejor_individuo = poblacion[indice_maximo].copy()
            mejor_valor = valores_poblacion[indice_maximo].copy()

            # print(mejor_individuo)

        if peor_valor < valores_poblacion[indice_minimo]:
            # Obtenemos el peor individuo de la poblacion
            peor_individuo = poblacion[indice_minimo].copy()
            peor_valor = valores_poblacion[indice_minimo].copy()

        # print(f"Mejor Valor : {mejor_valor}")
        # print(f"Peor Valor : {peor_valor}")
        historicoMejor.append(mejor_valor)
        historicoPeor.append(peor_valor)
    return mejor_valor, (historicoMejor, historicoPeor)

if __name__ == "__main__":
    # poblacion = inicializar_poblacion(24)
    # fitness(poblacion)

    _, historico = algoritmo_genetico(123456)

    fig, ax = plt.subplots()
    ax.plot([i for i in range(len(historico[0]))], historico[0])
    #ax.scatter([i for i in range(len(historico[1]))], historico[1], s=0.5)
    plt.show()


