# Practica 1: Búsquedas Locales #


# Bibliotecas
import random
import numpy as np
import matplotlib.pyplot as plt

np.random.seed(seed=6)


def almacenar_bateria(bateria, energia_disponible, dinero, i):
    # En caso de que quepa toda la energia disponible en la bateria.
    if bateria < capacidad_bateria and energia_disponible <= (capacidad_bateria - bateria) * 1000:
        bateria += energia_disponible / 1000
        energia_disponible = 0
    # En caso de que no quepa toda la energia disponible en la bateria, pero la bateria no esté llena:
    elif bateria < capacidad_bateria and energia_disponible > (capacidad_bateria - bateria) * 1000:
        energia_disponible = energia_disponible - (capacidad_bateria - bateria) * 1000
        bateria = capacidad_bateria

    # En caso de que la bateria esté llena bateria == capacidad_bateria, la vende
    # En caso de que sobre energia_disponible, la vende toda.
    dinero += (energia_disponible / 1000) * precio_venta[i]
    energia_disponible = 0

    return bateria, energia_disponible, dinero


# Funcion de Coste
def funcion_evaluacion(solucion):
    bateria = 0  # 1 -- 300 kWh
    dinero = 0  # en centimos

    for i in range(24):
        if solucion[i] >= 0:
            # Vender
            energia_disponible = bateria + r[i] * 0.2 * 1000  # En Wh
            energia_vendida = solucion[i] * energia_disponible
            dinero += (energia_vendida / 1000) * precio_venta[i]  # En KWh
            energia_disponible -= energia_vendida

            # Almacena en la bateria y vende el sobrante
            bateria, energia_disponible, dinero = almacenar_bateria(bateria, energia_disponible, dinero, i)

        elif solucion[i] < 0:
            # Comprar
            # Aunque hayamos decidido comprar, también generamos cierta energía ese día:
            energia_disponible = bateria + r[i] * 0.2 * 1000

            # Almacena y vende el sobrante
            bateria, energia_disponible, dinero = almacenar_bateria(bateria, energia_disponible, dinero, i)

            # Compro un porcentaje de lo que me sobra de bateria. De esta forma no podemos sobrepasar el limite de la
            # bateria en ningun momento.
            energia_comprada = solucion[i] * (capacidad_bateria - bateria)
            dinero -= energia_comprada * precio_compra

    # Finalmente, vendemos toda la energia almacenada en la bateria
    dinero += bateria * precio_venta[24]
    return dinero


# Generador de la solucion Inicial
def generar_inicial(longitud_vector, granularidad):
    # Genera un vector aleatorio de porcentaje de venta/compra
    # Estudiar la granularidad de la solucion

    # Granularidad = 1 : 0, 1, 2, 3 ... 100
    # Granularidad = 5 : 0, 5, 10, 15 ... 100
    # Granularidad = 10: 0, 10, 20, 30 ... 100

    solucion_inicial = [random.randrange(-100, 101, granularidad) for _ in range(longitud_vector)]

    return solucion_inicial


# Generador de Soluciones Vecinas
def genera_vecinos(solucion, granularidad):
    vecino = solucion
    h = np.random.randint(0, 23)  # Numero aleatorio para seleccionar la columna

    return vecino


# Algoritmo greedy:
# Para efectuar la comparativa de resultados entre los distintos algoritmos de búsqueda, se debe
# implementar como algoritmo básico, un Greedy, siguiendo la heurística de guardar desde el principio
# hasta que se llene y luego vender en el pico de precio del dia toda la energia. A partir de ese momento vender toda
# la energia.
def greedy():
    hora_venta = precio_venta.index(max(precio_venta))  # Obtiene la hora con el precio mas alto.
    bateria = 0
    dinero = 0

    dinero_acumulado = [0 for _ in range(24)]
    bateria_hora = [0 for _ in range(24)]
    energia_disponible_hora = [0 for _ in range(24)]

    for hora in range(24):
        # Cuando llegue la hora en la que hay que vender, se vende toda la energia que hay en la bateria + la que se
        # haya generado. A partir de dicha hora se vende toda la energia que genere.
        if hora >= hora_venta:
            energia_disponible = r[hora_venta] * 0.2 * 1000
            dinero += (bateria + energia_disponible / 1000) * precio_venta[hora_venta]
            bateria = 0
        # Si la hora es anterior a la hora de venta, se almacena en la bateria
        else:
            # Guarda toda la energia hasta que se llene
            energia_disponible = bateria + r[hora] * 0.2 * 1000

            if bateria < capacidad_bateria and energia_disponible <= (capacidad_bateria - bateria) * 1000:
                bateria += energia_disponible / 1000
                energia_disponible = 0
            elif bateria < capacidad_bateria and energia_disponible > (capacidad_bateria - bateria) * 1000:
                energia_disponible = energia_disponible - (capacidad_bateria - bateria) * 1000
                bateria = capacidad_bateria

            # Si sobra energia, se vende
            dinero += energia_disponible / 1000 * precio_venta[hora]

        # Graficas
        energia_disponible_hora[hora] = energia_disponible
        dinero_acumulado[hora] = dinero
        bateria_hora[hora] = bateria

    return dinero, dinero_acumulado, bateria_hora, energia_disponible_hora


# El Algoritmo de Búsqueda Aleatoria (BA) consistirá en generar aleatoriamente una solución en cada
# iteración debiéndose ejecutar 100 iteraciones con cada semilla devolviendo la mejor de las iteraciones.
def busqueda_aleatoria():
    return 0


def busqueda_elprimero():
    return 0


# Algoritmo de busqueda el mejor vecino
def busqueda_elMejor():
    solucion_actual = generar_inicial(24, granularidad)  # Genera la solucion inicial
    mejor_vecino = solucion_actual
    contador = 0
    while True:  # Repetir
        solucion_vecina = genera_vecinos(solucion_actual, granularidad)  # Genera vecinos
        while True:  # Repetir
            # Hasta que la funcion de coste del vecino sea mejor que la del mejor vecino
            # TODO: o hasta que se haya generado el espacio de busqueda completo
            if funcion_evaluacion(solucion_vecina) < funcion_evaluacion(mejor_vecino):
                break
            contador += 1

        # Si el coste de la solucion vecina es mejor que el de la solucion actual, se actualiza
        # la solucion
        if funcion_evaluacion(solucion_vecina) > funcion_evaluacion(solucion_actual):
            solucion_actual = solucion_vecina
            mejor_vecino = solucion_actual

        # Hasta que el coste de la solucion vecina sea peor o igual que el coste de la solucion actual.
        if funcion_evaluacion(solucion_vecina) <= funcion_evaluacion(solucion_actual):
            break

    return solucion_actual


# Main
semillas = [123456, 654321, 256413, 345621, 156342]

# Constantes

granularidad = 1
capacidad_bateria = 300  # Capacidad total de la bateria
porcentaje_bateria = 0  # Capacidad actual de la bateria

# Ejemplo Real
precio_compra = [26, 26, 25, 24, 23, 24, 25, 27, 30, 29, 34, 32, 31, 31, 25, 24, 25, 26, 34, 36, 39, 40, 38, 29]
precio_venta = [24, 23, 22, 23, 22, 22, 20, 20, 20, 19, 19, 20, 19, 20, 22, 23, 22, 23, 26, 28, 34, 35, 34, 24]
r = [0, 0, 0, 0, 0, 0, 0, 0, 100, 313, 500, 661, 786, 419, 865, 230, 239, 715, 634, 468, 285, 96, 0, 0]

# Llamamos a la funcion greedy:
dinero_greedy, dinero_acumulado, bateria_hora, energia_disponible_hora = greedy()

# Dinero acumulado en cada hora
plt.plot([i for i in range(24)], dinero_acumulado)
# Capacidad de la bateria en cada hora
plt.plot([i for i in range(24)], [b * max(dinero_acumulado) / capacidad_bateria for b in bateria_hora])

# Linea de hora de venta
plt.plot([precio_venta.index(max(precio_venta)) for _ in [0, round(max(dinero_acumulado))]],
         [i for i in [0, round(max(dinero_acumulado))]], linestyle=':')
plt.legend(["Dinero Acumulado", "Batería", "Hora de Venta"])
plt.show()

print(dinero_greedy / 100)
# print(bateria_hora)
# print(generar_inicial(24, granularidad))
