import numpy as np
import Utils

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


def algoritmo_genetico_generacional_multimodal(semilla, isRandom):
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

    # Multimodal
    P = 0   # Numero de generaciones que han transcurrido.
    while t < Utils.NUM_ITERACIONES:
        t = t + 1
        # Seleccionamos la élite de la población:
        elite = elitismo(poblacion, Utils.ELITE, isRandom)

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
            if np.random.random_integers(0, 100) < 80: # Probabilidad de cruce del 80%
                h1, h2 = cruce(poblacion[padres[i-Utils.ELITE][0].copy()],
                               poblacion[padres[Utils.ELITE][1].copy()])
            else:   # Si no, se copian como hijos.
                h1 = poblacion[padres[i-Utils.ELITE][0].copy()]
                h2 = poblacion[padres[Utils.ELITE][0].copy()]
            hijos[i] = h1
            hijos[i + 1] = h2

        # Mutar P(t)
        hijos_mutados = np.apply_along_axis(mutacion, 1, hijos.copy())
        poblacion = hijos_mutados

        # Evaluar P(t)
        valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, isRandom)
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

    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo


if __name__ == "__main__":
    Utils.grafica(algoritmo_genetico_generacional_multimodal, israndom=False)



