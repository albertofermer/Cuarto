import numpy as np

import Utils

'''
Función de mutación del algoritmo genético.

'''


def mutacion(individuo):
    # Obtiene el valor de la posición asignada
    posicion = np.random.uniform(0, 23)
    valor = individuo[posicion] * np.random.uniform(0, 10) / 100
    ind_mutado = individuo.copy()
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


def inicializar_poblacion(size):
    pob = np.random.randint(0, 23, (size, 24))
    return pob


def fitness(cromosoma):
    return sum(cromosoma)


def torneo(valores_cromosomas, k):
    probabilidades = valores_cromosomas / sum(valores_cromosomas)
    indices = np.random.choice(a=np.array([i for i in range(len(valores_cromosomas))]),
                               size=k, replace=False,
                               p=probabilidades)
    return list(indices)


def elitismo(poblacion, k):
    # Escoge los 5 mejores individuos.
    valores = np.apply_along_axis(fitness, 1, poblacion)
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
    valores_poblacion = np.apply_along_axis(fitness, 1, poblacion)
    while t < Utils.NUM_ITERACIONES:
        t = t + 1
        # Seleccionar los índices de los padres
        candidatos = np.array([torneo(valores_poblacion, int(Utils.K * Utils.POBLACION_INICIAL)) for _ in
                               range(Utils.POBLACION_INICIAL)])

        # Elegimos a los L=2 mejores de cada trío de padres
        padres = np.zeros(shape=(Utils.POBLACION_INICIAL, 2), dtype=int)
        for i in range(Utils.POBLACION_INICIAL):
            padres[i] = candidatos[i][np.argsort(valores_poblacion[candidatos[i]])[-2:]]

        elite = elitismo(padres,5)
        # Cruce
        hijos = np.zeros(shape=(Utils.POBLACION_INICIAL, 24), dtype=int)
        for i in range(0, Utils.POBLACION_INICIAL, 2):
            if i < Utils.POBLACION_INICIAL - 1:
                # print(f"Padres: {poblacion[padres[i][0]]} -- {poblacion[padres][i][1]}")
                # print(type(poblacion[padres[i][1]]))
                h1, h2 = cruce(poblacion[padres[i][0].copy()].copy(), poblacion[padres[i][1].copy()].copy())
                # print(f"Hijos: {h1} -- {h2}")
                hijos[i] = h1
                hijos[i + 1] = h2

        #elitismo(hijos, 5)
# Mutar

# Evaluar


if __name__ == "__main__":
    # h1, h2 = cruce(np.array([0, 1, 2, 3, 4, 5, 6, 7, 8, 9]), np.array([10, 11, 12, 13, 14, 15, 16, 17, 18, 19]))
    # print(h1)
    # print(h2)
    # np.random.seed(123456)
    poblacion = inicializar_poblacion(Utils.POBLACION_INICIAL)
    # Evaluar P(t)
    valores_poblacion = np.apply_along_axis(fitness, 1, poblacion)
    h = np.array(
        [torneo(valores_poblacion, int(Utils.K * Utils.POBLACION_INICIAL)) for _ in range(Utils.POBLACION_INICIAL)])
    padres = np.zeros(shape=(Utils.POBLACION_INICIAL, 2), dtype=int)
    for i in range(Utils.POBLACION_INICIAL):
        padres[i] = h[i][np.argsort(valores_poblacion[h[i]])[-2:]]

    hijos = np.zeros(shape=(Utils.POBLACION_INICIAL, 24), dtype=int)
    for i in range(0, Utils.POBLACION_INICIAL, 2):
        # print(i)
        if i < Utils.POBLACION_INICIAL - 1:
            # print(f"Padres: {poblacion[padres[i][0]]} -- {poblacion[padres][i][1]}")
            # print(type(poblacion[padres[i][1]]))
            h1, h2 = cruce(poblacion[padres[i][0].copy()].copy(), poblacion[padres[i][1].copy()].copy())
            # print(f"Hijos: {h1} -- {h2}")
            hijos[i] = h1
            hijos[i + 1] = h2
    print(hijos)
    elitismo(hijos, 5)
