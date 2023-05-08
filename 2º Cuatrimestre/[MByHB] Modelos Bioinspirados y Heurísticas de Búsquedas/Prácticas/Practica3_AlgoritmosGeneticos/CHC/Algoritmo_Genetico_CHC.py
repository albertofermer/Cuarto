import statistics
import pandas as pd
import Utils
import numpy as np


def blx_alpha(parent_1, parent_2, alpha):
    """
    Operador de cruce BLX-alpha para dos padres con valores reales.

    :param parent_1: Cromosoma padre 1.
    :param parent_2: Cromosoma padre 2.
    :param alpha: Valor de alpha para el operador BLX-alpha.
    :return: Dos hijos generados a partir de los padres mediante el operador BLX-alpha.
    """

    # Creamos dos hijos vacíos con el mismo tamaño que los padres.
    child_1 = np.zeros_like(parent_1)
    child_2 = np.zeros_like(parent_2)

    # Recorremos los genes de los padres y generamos los genes de los hijos.
    for i in range(len(parent_1)):
        # Calculamos los límites del intervalo de cruce para el gen i.
        cmin = min(parent_1[i], parent_2[i])
        cmax = max(parent_1[i], parent_2[i])
        I = cmax - cmin

        # Generamos los genes de los hijos utilizando el operador BLX-alpha.
        delta = alpha * I
        rand = np.random.uniform(-delta, 1 + delta)
        child_1[i] = 0.5 * ((1 + rand) * parent_1[i] + (1 - rand) * parent_2[i])
        child_2[i] = 0.5 * ((1 - rand) * parent_1[i] + (1 + rand) * parent_2[i])

        # Ajustamos los valores de los hijos si superan los límites permitidos.
        child_1[i] = max(min(child_1[i], 100), -100)
        child_2[i] = max(min(child_2[i], 100), -100)

    return child_1, child_2


def seleccionar(poblacion, hijos):
    # Concatenar población y hijos
    poblacion_total = np.vstack((poblacion, hijos))
    valores_poblacion_total = Utils.fitnessPoblacion(poblacion_total, isRandom=False)[0]

    # Ordenar de forma descendente según los valores de fitness
    orden = np.argsort(-valores_poblacion_total)
    poblacion_total = poblacion_total[orden]


    # Eliminar individuos duplicados
    _, unique_indices = np.unique(poblacion_total, axis=0, return_index=True)
    poblacion_total = poblacion_total[unique_indices]

    # Seleccionar los primeros n individuos únicos, donde n es el tamaño de la población original
    poblacion_nueva = poblacion_total[:len(poblacion)]

    return poblacion_nueva


def hayAlgunMiembroMejor(padres, hijos, isRandom):
    """
    Comprueba si existe algún valor en hijos que sea mejor que algún valor de padres.

    Args:
        padres (numpy.ndarray): La primera matriz de valores.
        hijos (numpy.ndarray): La segunda matriz de valores.

    Returns:
        bool: True si existe algún valor en hijos que es mayor que algún valor de padres, False en caso contrario.
    """

    fitness_padres = Utils.fitnessPoblacion(padres, isRandom)[0]
    fitness_hijos = Utils.fitnessPoblacion(hijos,isRandom)[0]
    return np.any(fitness_hijos > fitness_padres)

def select_s(padres, hijos, isRandom):
    """
    Reemplaza los peores miembros de padres por los mejores miembros de hijos mientras haya algún miembro en hijos mejor que en padres.

    Args:
        padres (numpy.ndarray): La matriz de padres.
        hijos (numpy.ndarray): La matriz de hijos.

    Returns:
        numpy.ndarray: La matriz de padres actualizada.
    """
    valores_padres = Utils.fitnessPoblacion(padres, isRandom)[0]
    valores_hijos = Utils.fitnessPoblacion(hijos, isRandom)[0]

    while np.any(valores_hijos > valores_padres):
        indices_padres = np.argsort(valores_padres)
        indices_hijos = np.argsort(valores_hijos)[::-1]
        for i in range(len(indices_hijos)):
            if valores_hijos[indices_hijos[i]] > valores_padres[indices_padres[i]]:
                padres[indices_padres[i]] = hijos[indices_hijos[i]]
                valores_padres[indices_padres[i]] = valores_hijos[indices_hijos[i]]

    return padres
def CHC_exp(semilla, alpha, iteraciones, israndom):
    np.random.seed(semilla)

    t = 0
    d = 24 / 4
    # Inicializar Poblacion
    poblacion = Utils.inicializar_poblacion(Utils.POBLACION_INICIAL - 1)
    # Evaluar Poblacion
    valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, israndom)
    indice_maximo = np.argmax(valores_poblacion)
    indice_minimo = np.argmin(valores_poblacion)

    # Mejor Valor Inicial
    mejor_individuo = poblacion[indice_maximo]
    mejor_valor = valores_poblacion[indice_maximo]

    # Peor Individuo Inicial
    peor_individuo = poblacion[indice_minimo]
    peor_valor = valores_poblacion[indice_minimo]

    # Graficas:
    mejorValorAcumulado = [mejor_valor]
    historicoPeor = [peor_valor]
    historicoMejor = [mejor_valor]
    mejorIndividuoGenAnterior = mejor_individuo.copy()

    # Inicializamos al numero de individuos porque hemos calculado el fitness anteriormente.
    numero_evaluaciones = len(poblacion)

    numero_reinicios = 0

    # Mientras no se cumpla la condicion
    while t < iteraciones:
        t += 1
        # Seleccionar_r
        parejas = poblacion.copy()
        np.random.shuffle(parejas)

        # Cruzar
        hijos = np.empty_like(poblacion)
        for i in range(0, len(parejas), 2):
            hijo1, hijo2 = blx_alpha(parejas[i], parejas[i + 1], alpha)
            hijos[i] = hijo1
            hijos[i + 1] = hijo2

        # Evaluar Poblacion
        # valores_poblacion, _, _ = Utils.fitnessPoblacion(hijos, israndom)

        # Seleccionar
        poblacion = select_s(parejas, hijos, israndom)
        valores_poblacion, _, _ = Utils.fitnessPoblacion(poblacion, israndom)
        numero_evaluaciones += len(poblacion)
        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)

        if valores_poblacion[indice_maximo] > mejor_valor:  # Actualizamos el mejor valor de toda la historia
            mejor_individuo = poblacion[indice_maximo].copy()
            mejor_valor = valores_poblacion[indice_maximo].copy()
            historicoPeor.append(peor_valor)
            # print(f"Maximo valor: {mejor_valor}")
            # print(f"Minimo valor: {peor_valor}")
            t = 0
            historicoMejor.append(mejor_valor)
            # print("Mejora")
        mejorValorAcumulado.append(mejor_valor)

        # Obtenemos el mejor individuo de la generación
        mejorIndividuoGenAnterior = poblacion[indice_maximo].copy()

        if peor_valor < valores_poblacion[indice_minimo]:
            # Obtenemos el peor individuo de la poblacion
            peor_valor = valores_poblacion[indice_minimo].copy()
            # historicoPeor.append(peor_valor)

        # Si P(t) == P(t-1)
        if np.array_equal(poblacion, hijos):
            d -= 1

        # Si d < 0 -> diverge P(t) y d = 24/4
        if d < 0:
            #diverge(poblacion, hijos)
            # En el arranque los valores de un cromosoma corresponden al mejor individuo de la generación anterior y el resto serán
            # aleatorios
            d = 24/4
            poblacion = Utils.inicializar_poblacion(Utils.POBLACION_INICIAL - 1)
            poblacion[0] = mejorIndividuoGenAnterior.copy()
            # print(poblacion)
            numero_reinicios += 1

    print(f"Reinicios: {numero_reinicios}")

    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo, numero_evaluaciones

def experimentar():
    mejor_valor = 0
    for alpha in Utils.ALPHA_EXP:
        for iteraciones in Utils.ITERACIONESCHC_EXP:
                valores = [0 for i in range(len(Utils.SEMILLAS))]
                for semilla in range(len(Utils.SEMILLAS)):
                    valor = CHC_exp(Utils.SEMILLAS[semilla], alpha, iteraciones, israndom=False)[0]
                    valores[semilla] = valor
                    media_valores = statistics.mean(valores)
                    print("-----------") if semilla >= 2 else print()

                    if semilla >= 2 and media_valores > mejor_valor:
                        mejor_valor = valor
                        data = { "Alpha" : [alpha],
                                "Iteraciones": [iteraciones],
                                "Valor Obtenido": [mejor_valor]}
                        # Opciones de Pandas para mostrar la tabla completa en la consola
                        pd.set_option('display.max_rows', None)
                        pd.set_option('display.max_columns', None)
                        pd.set_option('display.width', None)
                        pd.set_option('display.max_colwidth', None)

                        # Mostramos los datos obtenidos
                        print(pd.DataFrame(data))

def CHC(semilla, israndom):
    np.random.seed(semilla)

    t = 0
    d = 24 / 4
    # Inicializar Poblacion
    poblacion = Utils.inicializar_poblacion(Utils.POBLACION_INICIAL - 1)
    # Evaluar Poblacion
    valores_poblacion, dinero_acumulado, bateria_acumulada = Utils.fitnessPoblacion(poblacion, israndom)
    indice_maximo = np.argmax(valores_poblacion)
    indice_minimo = np.argmin(valores_poblacion)

    # Mejor Valor Inicial
    mejor_individuo = poblacion[indice_maximo]
    mejor_valor = valores_poblacion[indice_maximo]

    # Peor Individuo Inicial
    peor_individuo = poblacion[indice_minimo]
    peor_valor = valores_poblacion[indice_minimo]

    # Graficas:
    mejorValorAcumulado = [mejor_valor]
    historicoPeor = [peor_valor]
    historicoMejor = [mejor_valor]
    mejorIndividuoGenAnterior = mejor_individuo.copy()

    # Inicializamos al numero de individuos porque hemos calculado el fitness anteriormente.
    numero_evaluaciones = len(poblacion)

    numero_reinicios = 0

    # Mientras no se cumpla la condicion
    while t < Utils.NUM_ITERACIONES_CHC:
        t += 1
        # Seleccionar_r
        parejas = poblacion.copy()
        np.random.shuffle(parejas)

        # Cruzar
        hijos = np.empty_like(poblacion)
        for i in range(0, len(parejas), 2):
            hijo1, hijo2 = blx_alpha(parejas[i], parejas[i + 1], Utils.ALPHA)
            hijos[i] = hijo1
            hijos[i + 1] = hijo2

        # Evaluar Poblacion
        # valores_poblacion, _, _ = Utils.fitnessPoblacion(hijos, israndom)

        # Seleccionar
        poblacion = select_s(parejas, hijos, israndom)
        valores_poblacion, _, _ = Utils.fitnessPoblacion(poblacion, israndom)
        numero_evaluaciones += len(poblacion)
        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)

        if valores_poblacion[indice_maximo] > mejor_valor:  # Actualizamos el mejor valor de toda la historia
            mejor_individuo = poblacion[indice_maximo].copy()
            mejor_valor = valores_poblacion[indice_maximo].copy()
            historicoPeor.append(peor_valor)
            # print(f"Maximo valor: {mejor_valor}")
            # print(f"Minimo valor: {peor_valor}")
            t = 0
            historicoMejor.append(mejor_valor)
            # print("Mejora")
        mejorValorAcumulado.append(mejor_valor)

        # Obtenemos el mejor individuo de la generación
        mejorIndividuoGenAnterior = poblacion[indice_maximo].copy()

        if peor_valor < valores_poblacion[indice_minimo]:
            # Obtenemos el peor individuo de la poblacion
            peor_valor = valores_poblacion[indice_minimo].copy()
            # historicoPeor.append(peor_valor)

        # Si P(t) == P(t-1)
        if np.array_equal(poblacion, hijos):
            d -= 1

        # Si d < 0 -> diverge P(t) y d = 24/4
        if d < 0:
            #diverge(poblacion, hijos)
            # En el arranque los valores de un cromosoma corresponden al mejor individuo de la generación anterior y el resto serán
            # aleatorios
            d = 24/4
            poblacion = Utils.inicializar_poblacion(Utils.POBLACION_INICIAL - 1)
            poblacion[0] = mejorIndividuoGenAnterior.copy()
            # print(poblacion)
            numero_reinicios += 1
    print(f"Reinicios: {numero_reinicios}")

    return mejor_valor, (historicoMejor, historicoPeor), mejorValorAcumulado, mejor_individuo, numero_evaluaciones


if __name__ == "__main__":
    Utils.grafica(CHC, israndom=False)
    # experimentar()