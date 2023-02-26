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
            energia_disponible = bateria + r[hora] * 0.2  # En KWH
            dinero += energia_disponible * precio_venta[hora]
            bateria = 0
        # Si la hora es anterior a la hora de venta, se almacena en la bateria
        else:
            # Guarda toda la energia hasta que se llene
            energia_generada = r[hora] * 0.2  # En KWh

            # Si la bateria no esta llena y, la energia generada cabe en la bateria:
            if bateria < capacidad_bateria and energia_generada <= (capacidad_bateria - bateria):
                # Llenamos la bateria con la energia generada.
                bateria += energia_generada  # En KWh
                energia_generada = 0
            # Si la bateria no esta llena, pero no cabe toda la energia generada en la bateria:
            elif bateria < capacidad_bateria and energia_generada > (capacidad_bateria - bateria):
                # Decrementamos la energia generada con lo que cabe en la bateria hasta llenarla.
                energia_generada = energia_generada - (capacidad_bateria - bateria)
                # Llenamos la bateria
                bateria = capacidad_bateria

            # Si sobra energia, se vende
            dinero += energia_generada * precio_venta[hora]

        # Graficas
        dinero_acumulado[hora] = dinero
        bateria_hora[hora] = bateria

    return dinero, dinero_acumulado, bateria_hora


def grafica_greedy():
    # Inicialización de las variables
    dinero = [0 for _ in range(numero_repeticiones)]
    dinero_acumulado_gr = 0
    bateria_hora_gr = [0 for _ in range(24)]

    # Llamamos a la funcion de búsqueda:
    for i in range(numero_repeticiones):
        dinero_greedy, dinero_acumulado_gr, bateria_hora_gr = greedy()
        dinero[i] = dinero_greedy

    fig, ax = plt.subplots()
    ax.set_xticks(range(0, 23, 1))
    # Dinero acumulado en cada hora
    plt.plot([i for i in range(24)], [cent/100 for cent in dinero_acumulado_gr], label="Dinero Acumulado")
    plt.scatter([i for i in range(24)], [cent/100 for cent in dinero_acumulado_gr])

    # Capacidad de la bateria en cada hora
    plt.plot([i for i in range(24)], bateria_hora_gr, label="Batería")
    plt.scatter([i for i in range(24)], bateria_hora_gr)

    # Linea de hora de venta
    plt.plot([precio_venta.index(max(precio_venta)) for _ in range(2)],
             [i for i in [0, round(max([cent/100 for cent in dinero_acumulado_gr]))]], linestyle=':',
             label="Hora de Venta")

    plt.legend()
    plt.xlabel("Horas")
    plt.ylabel("Euros (€)")
    if not isRandom:
        plt.savefig(f'.\\graficas\\ProblemaReal\\greedy_search_ProblemaReal.png')
    else:
        plt.savefig(f'.\\graficas\\ProblemaAleatorio\\greedy_search_ProblemaAleatorio.png')
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
