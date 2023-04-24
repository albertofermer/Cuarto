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

    mejor_individuo = poblacion[indice_maximo]
    mejor_valor = valores_poblacion[indice_maximo]
    # Mientras no se cumpla la condicion
    while t < Utils.NUM_ITERACIONES:
        t += 1
        # Seleccionar_r
        parejas = poblacion.copy()
        np.random.shuffle(parejas)

        # Cruzar
        hijos = np.empty_like(poblacion)
        for i in range(0, len(parejas), 2):
            hijo1, hijo2 = blx_alpha(pob[i], pob[i + 1], Utils.ALPHA)
            hijos[i] = hijo1
            hijos[i + 1] = hijo2

        # Evaluar Poblacion
        valores_poblacion, _, _ = Utils.fitnessPoblacion(hijos, israndom)
        indice_maximo = np.argmax(valores_poblacion)
        indice_minimo = np.argmin(valores_poblacion)

        if valores_poblacion[indice_maximo] > mejor_valor:
            mejor_individuo = poblacion[indice_maximo]
            mejor_valor = valores_poblacion[indice_maximo]

        # Seleccionar

        # Si P(t) == P(t-1)
        # d--

        # Si d < 0 -> diverge P(t) y actualiza d


if __name__ == "__main__":
    pob = Utils.inicializar_poblacion(16)
    hijos = np.zeros(shape=(16, 24), dtype=int)
    for i in range(0, len(pob), 2):
        print(i)
        hijo1, hijo2 = blx_alpha(pob[i], pob[i + 1], Utils.ALPHA)
        hijos[i] = hijo1
        hijos[i + 1] = hijo2
