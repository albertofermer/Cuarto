import matplotlib.pyplot as plt
import numpy as np


# Constantes
SEMILLAS = [123456, 654321, 456789]
POBLACION_INICIAL = 15  # EXP
PORCENTAJE_MUTACION = 0.01  # EXP
K = 0.2
NUM_ITERACIONES = 1000   # EXP
ELITE = 5

# CHC
ALPHA = 0.01

# Problema
capacidad_bateria = 300  # Capacidad total de la bateria
porcentaje_bateria = 0  # Capacidad actual de la bateria

# Ejemplo Real
precio_compra_real = [26, 26, 25, 24, 23, 24, 25, 27, 30, 29, 34, 32, 31, 31, 25, 24, 25, 26, 34, 36, 39, 40, 38, 29]
precio_venta_real = [24, 23, 22, 23, 22, 22, 20, 20, 20, 19, 19, 20, 19, 20, 22, 23, 22, 23, 26, 28, 34, 35, 34, 24]
r_real = [0, 0, 0, 0, 0, 0, 0, 0, 100, 313, 500, 661, 786, 419, 865, 230, 239, 715, 634, 468, 285, 96, 0, 0]

# Ejemplo Aleatorio
precio_compra_random = [7, 7, 50, 25, 11, 26, 48, 45, 10, 14, 42, 14, 42, 22, 40, 34, 21, 31, 29, 34, 11, 37, 8, 50]
precio_venta_random = [1, 3, 21, 1, 10, 7, 44, 35, 4, 1, 23, 12, 30, 7, 30, 4, 9, 10, 6, 9, 8, 27, 7, 10]
r_random = [274, 345, 605, 810, 252, 56, 964, 98, 77, 816, 68, 261, 841, 897, 75, 489, 833, 96, 117, 956, 970, 255, 74, 926]


# Funciones
def get_vectores(israndom):
    if israndom:
        precio_venta = precio_venta_random
        precio_compra = precio_compra_random
        r = r_random
    else:
        precio_venta = precio_venta_real
        precio_compra = precio_compra_real
        r = r_real
    return precio_venta, precio_compra, r


def almacenar_bateria(bateria, energia_generada, dinero, i, israndom):
    precio_venta, precio_compra, r = get_vectores(israndom)
    # En caso de que quepa toda la energia disponible en la bateria.
    if bateria < capacidad_bateria and energia_generada <= (capacidad_bateria - bateria):
        bateria += energia_generada  # En KWh
        energia_generada = 0
    # En caso de que no quepa toda la energia disponible en la bateria, pero la bateria no esté llena:
    elif bateria < capacidad_bateria and energia_generada > (capacidad_bateria - bateria):
        energia_generada = energia_generada - (capacidad_bateria - bateria)  # En KWh
        bateria = capacidad_bateria

    # En caso de que la bateria esté llena bateria == capacidad_bateria, la vende
    # En caso de que sobre energia_disponible, la vende toda.
    dinero += energia_generada * precio_venta[i]
    energia_generada = 0

    return bateria, energia_generada, dinero


def vender(bateria, hora, solucion, dinero, israndom):
    precio_venta, precio_compra, r = get_vectores(israndom)
    # Vender
    energia_disponible = bateria + r[hora] * 0.2  # En KWh
    energia_vendida = abs(solucion[hora]) / 100 * energia_disponible  # En KWh
    dinero += energia_vendida * precio_venta[hora]  # En KWh

    if bateria >= energia_vendida:  # Si hay suficiente energia para vender en la bateria
        bateria -= energia_vendida  # Vende de la bateria
        energia_disponible = bateria + r[hora] * 0.2

    else:  # En otro caso
        energia_disponible -= energia_vendida  # Vendo toda la bateria y lo que falte de lo que haya generado.
        bateria = 0

    # Almacena en la bateria lo que sobre y vende lo que no quepa en la bateria
    bateria, energia_disponible, dinero = almacenar_bateria(bateria, energia_disponible - bateria, dinero, hora, israndom)

    return bateria, energia_disponible, dinero


def fitness(solucion, israndom):

    precio_venta, precio_compra, r = get_vectores(israndom)
    bateria = 0  # 1 -- 300 kWh
    dinero = 0  # en centimos
    # Listas para sacar los datos de las graficas
    dinero_acumulado = [0 for _ in range(24)]
    bateria_hora = [0 for _ in range(24)]
    energia_disponible_hora = [0 for _ in range(24)]
    for hora in range(24):
        if solucion[hora] >= 0:
            # Vende
            bateria, energia_disponible, dinero = vender(bateria, hora, solucion, dinero, israndom)
        else:
            # Comprar
            # Aunque hayamos decidido comprar, también generamos cierta energía ese día:
            energia_disponible = bateria + r[hora] * 0.2  # En KWh

            # Almacena y vende el sobrante
            bateria, energia_disponible, dinero = almacenar_bateria(bateria, energia_disponible - bateria, dinero, hora, israndom)

            # Compro un porcentaje de lo que me sobra de bateria. De esta forma no podemos sobrepasar el limite de la
            # bateria en ningun momento.
            energia_comprada = (solucion[hora] / 100) * (capacidad_bateria - bateria)  # (Sale negativo)
            dinero += energia_comprada * precio_compra[hora]  # Como energia_comprada es < 0 entonces se resta.

            bateria += -energia_comprada

            # Si la energia comprada es 0, entonces ¿vendo?
            if energia_comprada == 0:
                bateria, energia_disponible, dinero = vender(bateria, hora, solucion, dinero, israndom)

        # Guardamos los datos para las gráficas
        energia_disponible_hora[hora] = energia_disponible
        dinero_acumulado[hora] = dinero
        bateria_hora[hora] = bateria

    # Finalmente, vendemos toda la energia almacenada en la bateria
    dinero += bateria * precio_venta[23]
    bateria = 0

    dinero_acumulado[23] = dinero
    bateria_hora[23] = bateria
    #dinero_acumulado, bateria_hora
    return dinero, dinero_acumulado, bateria_hora


def fitnessPoblacion(poblacion, isRandom):
    dinero = np.zeros(len(poblacion), dtype=float)
    dinero_acumulado = np.zeros((len(poblacion),24), dtype=float)
    bateria_hora = np.zeros((len(poblacion),24), dtype=float)
    for i in range(len(poblacion)):
        dinero[i], dinero_acumulado[i], bateria_hora[i] = fitness(poblacion[i], isRandom)
    return dinero, dinero_acumulado, bateria_hora
def inicializar_poblacion(size):
    pob = np.random.randint(-100, 100, (size, 24), dtype=int)
    return pob

def grafica(funcion, israndom):
    for semilla in SEMILLAS:
        _, historico, mejor_valor_acumulado, mejor_individuo = funcion(semilla, israndom)
        # Convergencia de los individuos
        fig, ax = plt.subplots()
        ax.plot([i for i in range(len(historico[0]))], [i/100 for i in historico[0]], label="Fitness Mejor Individuo")
        ax.plot([i for i in range(len(historico[1]))], [i/100 for i in historico[1]], label="Fitness Peor Individuo")
        plt.legend()
        plt.xlabel("Veces que mejora o empeora")
        plt.ylabel("Dinero (€)")
        plt.title(f"Convergencia del Mejor Individuo y el Peor.\n Semilla: {semilla}")
        plt.savefig(f"./Graficas/Convergencia/{semilla}_{'Datos_Aleatorios' if israndom else 'Datos_Reales'}.png")
        plt.show()

        # Fitness
        fig2, ax = plt.subplots()
        ax.plot([i for i in range(len(mejor_valor_acumulado))], [i/100 for i in mejor_valor_acumulado], label="Fitness")
        plt.legend()
        plt.xlabel("Iteraciones")
        plt.ylabel("Dinero (€)")
        plt.title(f"Fitness del Mejor Individuo.\n Semilla: {semilla}")
        plt.savefig(f"./Graficas/Fitness/{semilla}_{'Datos_Aleatorios' if israndom else 'Datos_Reales'}.png")
        plt.show()

        # Simulación
        _, dinero_acumulado, bateria_hora = fitness(mejor_individuo,israndom)
        # Dinero acumulado en cada hora

        fig, ax = plt.subplots()
        plt.title(f"Simulación del Mejor Individuo.\n Semilla: {semilla}")
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
        plt.legend(leg, labs, loc='upper center', bbox_to_anchor=(0.5, 1.0), ncol=3)
        plt.savefig(f"./Graficas/Simulacion/{semilla}_{'Datos_Aleatorios' if israndom else 'Datos_Reales'}.png")
        plt.show()
