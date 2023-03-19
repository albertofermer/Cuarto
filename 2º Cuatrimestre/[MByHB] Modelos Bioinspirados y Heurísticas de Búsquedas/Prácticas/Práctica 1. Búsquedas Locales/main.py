import _1_Búsqueda_Greedy.busqueda_greedy as greedy
import _2_Búsqueda_Aleatoria.busqueda_aleatoria as random
import _3_Búsqueda_del_Mejor.busqueda_elmejor as best
import _4_Búsqueda_Primero_el_Mejor.busqueda_elprimero as first
import _5_Búsqueda_VND.busqueda_vnd as vnd
import _6_Búsqueda_Enfriamiento_Simulado.busqueda_ES as simulated
import _7_Búsqueda_Tabú.busqueda_tabu as tabu

import sys

if __name__ == "__main__":
    name_to_value = {"greedy": 0, "random": 1, "best": 2, "first": 3, "vnd": 4, "es": 5, "tabu": 6}
    if len(sys.argv) != 3:
        raise Exception("Debe escribir tres argumentos en la llamada.\n"
                        f"\tEl primero corresponde con el nombre del algoritmo. "
                        f"['greedy', 'random', 'best', 'first', 'vnd', 'es', 'tabu']\n"
                        "\tEl segundo corresponde con el tipo de problema a resolver. [0, 1]\n"
                        "\tEl tercero corresponde si desea mostrar las gráficas o solo el resultado. [0, 1]\n")

    name = sys.argv[1]
    problema = int(sys.argv[2]) % 2

    if name in name_to_value and name_to_value[name] == 0:
        print(f"Seleccionado Algoritmo Greedy - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print(f"{round(greedy.greedy(problema != 0)[0] / 100, 2)} euros.")

    elif name in name_to_value and name_to_value[name] == 1:
        print(
            f"Seleccionado Algoritmo Búsqueda Aleatoria - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print("Se necesita que introduzca la semilla y la granularidad para realizar la búsqueda. "
              "En caso de que no introduzca ningún número, se asumirá semilla = 123456 y granularidad = 1")

        semilla = input("Introduzca la semilla: ")
        granularidad = input("Introduzca la granularidad: ")
        print(
            f"{round(random.busqueda_aleatoria(problema != 0, int(semilla) if semilla.isnumeric() else 123456, int(granularidad) if granularidad.isnumeric() else 1)[0] / 100, 2)} euros.")

    elif name in name_to_value and name_to_value[name] == 2:
        print(
            f"Seleccionado Algoritmo Búsqueda El Mejor Vecino - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print("Se necesita que introduzca la semilla y la granularidad para realizar la búsqueda. "
              "En caso de que no introduzca ningún número, se asumirá semilla = 123456 y granularidad = 1")

        semilla = input("Introduzca la semilla: ")
        granularidad = input("Introduzca la granularidad: ")
        print(
            f"{round(best.busqueda_elmejor(problema != 0, int(semilla) if semilla.isnumeric() else 123456, int(granularidad) if granularidad.isnumeric() else 1)[0] / 100, 2)} euros.")

    elif name in name_to_value and name_to_value[name] == 3:
        print("Seleccionado Algoritmo First - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print("Se necesita que introduzca la semilla y la granularidad para realizar la búsqueda. "
              "En caso de que no introduzca ningún número, se asumirá semilla = 123456 y granularidad = 1")

        semilla = input("Introduzca la semilla: ")
        granularidad = input("Introduzca la granularidad: ")
        print(
            f"{round(first.busqueda_primero(problema != 0, int(semilla) if semilla.isnumeric() else 123456, int(granularidad) if granularidad.isnumeric() else 1)[0][0] / 100, 2)} euros.")

    elif name in name_to_value and name_to_value[name] == 4:
        print(f"Seleccionado Algoritmo VND - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print("Se necesita que introduzca la semilla para realizar la búsqueda. "
              "En caso de que no introduzca ningún número, se asumirá semilla = 123456")
        semilla = input("Introduzca la semilla: ")
        print(
            f"{round(vnd.busqueda_elmejor_vnd(problema != 0, int(semilla) if semilla.isnumeric() else 123456, [1, 5, 10, 15, 20])[0] / 100, 2)} euros.")

    elif name in name_to_value and name_to_value[name] == 5:
        print(f"Seleccionado Algoritmo ES - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print("Se necesita que introduzca la semilla, la granularidad, el número de vecinos "
              " a generar, el parámetro mu y el parámetro phi para realizar la búsqueda. "
              "En caso de que no introduzca ningún número, se asumirá semilla = 123456, granularidad = 1, "
              "numero de vecinos = 15, mu = 0.3 y phi = 0.25")

        semilla = input("Introduzca la semilla: ")
        granularidad = input("Introduzca la granularidad: ")
        num_vecinos = input("Introduzca el número de vecinos: ")
        mu = input("Introduzca el parámetro mu [0-1]: ")
        phi = input("Introduzca el parámetro phi [0-1]: ")

        print(
            f"{round(simulated.enfriamiento_simulado(problema != 0, int(semilla) if semilla.isnumeric() else 123456, int(granularidad) if granularidad.isnumeric() else 1, int(num_vecinos) if num_vecinos.isnumeric() else 15, int(mu) if mu.isnumeric() else 0.3, int(phi) if phi.isnumeric() else 0.25)[0] / 100, 2)} euros.")

    elif name in name_to_value and name_to_value[name] == 6:
        print(f"Seleccionado Algoritmo Búsqueda Tabú - {'Problema Real' if problema == 0 else 'Problema Aleatorio'}")
        print("Se necesita que introduzca la semilla, iteraciones máximas, numero de vecinos "
              "para realizar la búsqueda."
              "En caso de que no introduzca ningún número, se asumirá semilla = 123456, iteraciones = 1000 "
              "numero de vecinos = 40 y granularidad = 10")

        semilla = input("Introduzca la semilla: ")
        iteraciones = input("Introduzca el número de iteraciones máximas: ")
        num_vecinos = input("Introduzca el número de vecinos que quiere generar: ")
        print(
            f"{round(tabu.BusquedaTabu(problema != 0, int(semilla) if semilla.isnumeric() else 123456, int(iteraciones) if iteraciones.isnumeric() else 1000, int(num_vecinos) if num_vecinos.isnumeric() else 40, 10)[0] / 100, 2)} euros.")

    else:
        print("Algoritmo no encontrado")
