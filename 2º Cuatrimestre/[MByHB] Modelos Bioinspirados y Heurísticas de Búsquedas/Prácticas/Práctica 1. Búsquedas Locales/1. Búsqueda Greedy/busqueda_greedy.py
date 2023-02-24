import constantes
import matplotlib.pyplot as plt
import pandas as pd
import statistics

isRandom = False
numero_repeticiones = 5

# Constantes
capacidad_bateria = constantes.capacidad_bateria
granularidad = constantes.granularidad

if isRandom:
    precio_venta = constantes.precio_venta_random
    precio_compra = constantes.precio_compra_random
    r = constantes.r_random
else:
    precio_venta = constantes.precio_venta
    precio_compra = constantes.precio_compra
    r = constantes.r


evaluaciones = [0 for _ in range(numero_repeticiones)]
media_evaluaciones = statistics.mean(evaluaciones)
mejor_evaluacion = evaluaciones.index(min(evaluaciones))
desviacion_evaluaciones = statistics.stdev(evaluaciones)


# Algoritmo greedy:
# Para efectuar la comparativa de resultados entre los distintos algoritmos de búsqueda, se debe
# implementar como algoritmo básico, un Greedy, siguiendo la heurística de guardar desde el principio
# hasta que se llene y luego vender en el pico de precio del dia toda la energia. A partir de ese momento vender toda
# la energia.
def greedy():
    hora_venta = precio_venta.index(max(precio_venta))  # Obtiene la hora con el precio mas alto.
    bateria = 0
    dinero = 0

    # Listas para sacar los datos de las graficas
    dinero_acumulado = [0 for _ in range(24)]
    bateria_hora = [0 for _ in range(24)]
    energia_disponible_hora = [0 for _ in range(24)]

    for hora in range(24):
        # Cuando llegue la hora en la que hay que vender, se vende toda la energia que hay en la bateria + la que se
        # haya generado. A partir de dicha hora se vende toda la energia que genere.
        if hora >= hora_venta:
            energia_disponible = bateria + r[hora_venta] * 0.2  # KwH
            dinero += energia_disponible * precio_venta[hora_venta]
            bateria = 0
        # Si la hora es anterior a la hora de venta, se almacena en la bateria
        else:
            # Guarda toda la energia hasta que se llene
            energia_disponible = bateria + r[hora] * 0.2

            if bateria < capacidad_bateria and energia_disponible <= (capacidad_bateria - bateria):
                bateria += energia_disponible - bateria
                energia_disponible = 0
            elif bateria < capacidad_bateria and energia_disponible > (capacidad_bateria - bateria):
                energia_disponible = energia_disponible - (capacidad_bateria - bateria)
                bateria = capacidad_bateria

            # Si sobra energia, se vende
            dinero += energia_disponible * precio_venta[hora]

        # Graficas
        energia_disponible_hora[hora] = energia_disponible
        dinero_acumulado[hora] = dinero
        bateria_hora[hora] = bateria

    return dinero, dinero_acumulado, bateria_hora, energia_disponible_hora


def grafica_greedy():

    # Inicialización de las variables
    dinero = [0 for _ in range(numero_repeticiones)]
    dinero_acumulado_gr = 0
    bateria_hora_gr = [0 for _ in range(24)]

    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        dinero_greedy, dinero_acumulado_gr, bateria_hora_gr, energia_disponible_hora_gr = greedy()
        dinero[i] = dinero_greedy

    # Dinero acumulado en cada hora
    plt.plot([i for i in range(24)], dinero_acumulado_gr)

    # Capacidad de la bateria en cada hora
    plt.plot([i for i in range(24)], [b * max(dinero_acumulado_gr) / capacidad_bateria for b in bateria_hora_gr])

    # Linea de hora de venta
    plt.plot([precio_venta.index(max(precio_venta)) for _ in [0, round(max(dinero_acumulado_gr))]],
             [i for i in [0, round(max(dinero_acumulado_gr))]], linestyle=':')

    plt.legend(["Dinero Acumulado", "Batería", "Hora de Venta"])    # La leyenda
    plt.show()  # Mostramos la gráfica

    # Generamos los datos obtenidos de la búsqueda
    data = {
        'Media Evaluaciones': [media_evaluaciones],
        'Mejor Evaluación': [mejor_evaluacion],
        'Desviación Evaluaciones': [desviacion_evaluaciones],
        'Media Dinero (€)': [round(statistics.mean(dinero) / 100, 2)],
        'Mejor Dinero (€)': [round(max(dinero) / 100, 2)],
        'Desviación Dinero (€)': [round(statistics.stdev(dinero) / 100, 2)]
    }

    # Opciones de Pandas para mostrar la tabla completa en la consola
    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_columns', None)
    pd.set_option('display.width', None)
    pd.set_option('display.max_colwidth', None)

    # Mostramos los datos obtenidos
    print(pd.DataFrame(data))


# main
grafica_greedy()

