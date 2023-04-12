import statistics
from functools import partial

import numpy as np
import pandas as pd
import math
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

import PSO_GLOBAL.Global_PSO
import PSO_LOCAL.Local_PSO

'''
Valores que se utilizarán en el algoritmo PSO
'''
SEEDS = [147852, 234561, 345612, 456123, 789456]
NUM_IT_SINMEJORA = [20, 50, 80, 100, 150, 200, 300, 350, 400]
VEL = [0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45]
DIMENSION = 2

# Constantes para PSO
NUM_PARTICLES = 10
VECINDAD = 2
OMEGA = 0.729
PHI_1 = 1.49445
PHI_2 = 1.49445
MAX_VEL = 0.1
# Rosenbrock
MAX_POS_ROS_X = 2
MIN_POS_ROS_X = -2
MAX_POS_ROS_Y = 3
MIN_POS_ROS_Y = -1

# Rastrigin
MAX_POS_RAS = 5
MIN_POS_RAS = -5
# Constantes para Busqueda Local0
GRANULARIDAD = 0.1
NUM_VECINOS = 10
MAX_POS_BL = 10
MIN_POS_BL = -10


def RosenbrockFunction(x):
    # print(x)
    return (1 - x[0]) ** 2 + 100 * (x[1] - x[0] ** 2) ** 2


def RastriginFunction(x):
    return 20 + x[0] ** 2 + x[1] ** 2 - 10 * (np.cos(2 * math.pi * x[0]) + np.cos(2 * math.pi * x[1]))


def RepresentarFuncion(funcion, ax, semilla, iteracion):
    # Cada función de evaluación tiene unos límites diferentes.
    if funcion == RosenbrockFunction:
        x = np.linspace(MIN_POS_ROS_X, MAX_POS_ROS_X)
        y = np.linspace(MIN_POS_ROS_Y, MAX_POS_ROS_Y)
    else:
        x = np.linspace(MIN_POS_RAS, MAX_POS_RAS)
        y = np.linspace(MIN_POS_RAS, MAX_POS_RAS)

    # Generamos el espacio de la función
    X1, X2 = np.meshgrid(x, y)
    # Añadimos el título de la gráfica
    plt.title(f"{'Función Rosenbrock' if funcion == RosenbrockFunction else 'Funcion Rastrigin'}\n"
              f"Semilla: {semilla}\n"
              f"Iteración: {iteracion}\n")
    # Graficamos la superficie de la gráfica
    ax.plot_surface(X1, X2, funcion([X1, X2]), cmap='jet', alpha=0.5)
    # Le damos orientación para verla desde arriba
    ax.view_init(elev=90, azim=180)
    # Añadimos las etiquetas a los ejes
    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    ax.set_zlabel(f'{"ROS" if funcion == RosenbrockFunction else "RAS"}(X, Y)')


'''
Calcula la siguiente posición de una partícula.
'''


def GetNextPositionAndSpeed(omega, v, phi_1, pbest, phi_2, g, pos, vel, function):
    # Inicializamos los vectores de la siguiente posición y la siguiente velocidad.
    next_pos = np.zeros(len(pos))
    next_v = np.zeros(len(pos))
    # Para cada dimensión del vector posición
    for i in range(len(pos)):
        # Calculamos la siguiente velocidad aplicando la fórmula
        next_v[i] = omega * v[i] + phi_1 * np.random.random() * (pbest[i] - pos[i]) + phi_2 * np.random.random() * (
                g[i] - pos[i])
        # Controlamos que la componente de cada velocidad no supere la velocidad máxima
        if next_v[i] >= vel[1]:
            next_v[i] = vel[1]
        elif next_v[i] <= vel[0]:
            next_v[i] = vel[0]

        # La siguiente posición será la suma
        next_pos[i] = pos[i] + next_v[i]

        # Controlamos que no se salga de los límites de las gráficas
        if function == 0:  # Rosenbrock
            if next_pos[i] >= MAX_POS_ROS_X and i == 0:
                next_pos[i] = MAX_POS_ROS_X
            elif next_pos[i] <= MIN_POS_ROS_X and i == 0:
                next_pos[i] = MIN_POS_ROS_X

            if next_pos[i] >= MAX_POS_ROS_Y and i == 1:
                next_pos[i] = MAX_POS_ROS_Y
            elif next_pos[i] <= MIN_POS_ROS_Y and i == 1:
                next_pos[i] = MIN_POS_ROS_Y

        else:  # Rastrigin
            if next_pos[i] >= MAX_POS_RAS:
                next_pos[i] = MAX_POS_RAS
            elif next_pos[i] <= MIN_POS_RAS:
                next_pos[i] = MIN_POS_RAS

    return next_pos, next_v


def GetEntorno(indice_particula):
    # Obtenemos el entorno de cada partícula obteniendo el módulo del índice de la partícula:
    # i - 2 % NUM_PARTICULAS
    # i - 1 % NUM_PARTICULAS
    # i + 1 % NUM_PARTICULAS
    # i + 2 % NUM_PARTICULAS
    return [(indice_particula + i) % NUM_PARTICLES for i in range(-VECINDAD, VECINDAD + 1) if i != 0]

'''
Esta función se llama a la hora de realizar la animación del movimiento de las partículas en 
FuncAnimation. Se ejecuta una vez por cada frame que se especifique y se encarga de dibujar
cada partícula sobre la función de evaluación.
'''
def func(i, lista, funcion, semilla):
    coord = np.zeros(shape=(NUM_PARTICLES, 2), dtype=float)

    # Recorremos la matriz lista de 10 en 10 posiciones. En cada
    # frame se recorren las 10 partículas para ir generando
    # la animación
    for j in range(0 + (NUM_PARTICLES * i), NUM_PARTICLES * (i + 1)):
        print(len(lista) - j) # Contador para saber cuánto le queda a la animación para realizarse.
        if j < len(lista): # Si se supera la longitud de la lista no hace nada (para evitar errores)
            coord[j % NUM_PARTICLES, 0] = lista[j, 0]
            coord[j % NUM_PARTICLES, 1] = lista[j, 1]
    # Limpiamos la gráfica
    plt.cla()
    # Generamos la gráfica con la función de evaluación
    RepresentarFuncion(funcion, ax, semilla, int(i))
    # Pintamos las partículas correspondientes al frame i.
    ax.scatter(coord[:, 0], coord[:, 1], funcion([coord[:, 0], coord[:, 1]]), s=10, c="black", alpha=1)

'''
Función para generar la animación de las partículas en la función pasada por parámetro.
'''
def Dibujar(fun, x, nombre, semilla):
    # Generamos la figura
    fig = plt.figure(figsize=[12, 8])
    # Inicializamos el eje como proyección 3D y lo hacemos una variable global.
    global ax
    ax = plt.axes(projection='3d')
    # Empezamos la animación
    ani = FuncAnimation(fig, partial(func, lista=x, funcion=fun, semilla=semilla), frames=int(len(x) / 10), interval=1,
                        blit=False)
    # Guardamos la animación.
    ani.save(nombre, fps=15)


'''
Función que genera los vídeos de la animación de cada función para todas las semillas.
'''
def generarVideos(pso):
    for f in [RosenbrockFunction, RastriginFunction]:
        for s in range(len(SEEDS)):
            if pso.__name__ == PSO_GLOBAL.Global_PSO.Global_PSO.__name__ and f == RosenbrockFunction:
                x = pso(SEEDS[s], f, (-0.2, 0.2), 400)[5]
            elif pso.__name__ == PSO_GLOBAL.Global_PSO.Global_PSO.__name__ and f == RastriginFunction:
                x = pso(SEEDS[s], f, (-0.1, 0.1), 400)[5]
            elif pso.__name__ == PSO_LOCAL.Local_PSO.Local_PSO.__name__ and f == RosenbrockFunction:
                x = pso(SEEDS[s], f, (-0.3, 0.3), 200)[5]
            else: # pso == PSO_LOCAL.Local_PSO.Local_PSO and f == RastriginFunction:
                x = pso(SEEDS[s], f, (-0.25, 0.25), 400)[5]
            Dibujar(f, x, f".\\Videos\\{'ROS' if f == RosenbrockFunction else 'RAS'}_S{SEEDS[s]}.mp4", SEEDS[s])

'''
Función que realiza una experimentación de algunos parámetros para encontrar los mejores
para cada situación.
'''
def experimentacion(pso):
    mejores_parametros_ros = np.array([(-1, -1), (-1, -1), (-1, -1), (-1, -1), (-1, -1)], dtype=float)
    mejores_parametros_ras = np.array([(-1, -1), (-1, -1), (-1, -1), (-1, -1), (-1, -1)], dtype=float)
    for s in range(len(SEEDS)):
        for f in [RosenbrockFunction, RastriginFunction]:
            mejor_valor = float('inf')
            for nvsm in range(len(NUM_IT_SINMEJORA)):
                for v in range(len(VEL)):
                    valor = pso(SEEDS[s], f, (-VEL[v], VEL[v]), nvsm)[1]

                    if valor < mejor_valor:
                        mejor_valor = valor
                        if f == RosenbrockFunction:
                            mejores_parametros_ros[s] = (VEL[v], NUM_IT_SINMEJORA[nvsm])
                        else:
                            mejores_parametros_ras[s] = (VEL[v], NUM_IT_SINMEJORA[nvsm])
    return mejores_parametros_ros, mejores_parametros_ras


'''
Función para generar las gráficas de fitness de los algoritmos de PSO.
'''
def graficas_resultados(pso):
    for f in [RosenbrockFunction, RastriginFunction]:   # Para cada función
        evaluaciones = np.zeros(len(SEEDS)) # Inicializamos la lista de evaluaciones a 0
        mejores_valores = np.zeros(len(SEEDS))  # Inicializamos la lista de evaluaciones a 0
        for s in range(len(SEEDS)): # Para cada semilla
            # Dependiendo de la función y del algoritmo, es mejor utilizar unos parámetros u otros.
            if pso.__name__ == PSO_GLOBAL.Global_PSO.Global_PSO.__name__ and f == RosenbrockFunction:
                x = pso(SEEDS[s], f, (-0.2, 0.2), 400)
            elif pso.__name__ == PSO_GLOBAL.Global_PSO.Global_PSO.__name__ and f == RastriginFunction:
                x = pso(SEEDS[s], f, (-0.1, 0.1), 400)
            elif pso.__name__ == PSO_LOCAL.Local_PSO.Local_PSO.__name__ and f == RosenbrockFunction:
                x = pso(SEEDS[s], f, (-0.3, 0.3), 200)
            else: # pso == PSO_LOCAL.Local_PSO.Local_PSO and f == RastriginFunction:
                x = pso(SEEDS[s], f, (-0.25, 0.25), 400)

            # Guardamos los mejores valores y las evaluaciones
            mejores_valores[s] = x[1]
            evaluaciones[s] = x[4]
            # Dibujamos las gráficas de fitness:
            fig, ax = plt.subplots()
            ax.plot([i for i in range(len(x[3]))], x[3],
                    label=f"{'Rosenbrock(x,y)' if f == RosenbrockFunction else 'Rastrigin(x,y)'}")
            ax.plot([i for i in range(len(x[3]))], [0 for _ in range(len(x[3]))], linestyle="--", label="Mínimo Global")
            plt.legend()
            plt.xlabel("Iteraciones")
            plt.ylabel("Valor")
            plt.title(
                f"Semilla: {SEEDS[s]}. Función: {'Rosenbrock' if f == RosenbrockFunction else 'Rastrigin'}\n"
                f"Valor Mínimo: {x[1]}")
            plt.savefig(f"./Graficas/{'ROS' if f == RosenbrockFunction else 'RAS'}_S{SEEDS[s]}")
            plt.show()

        # END-FOR
        #print(mejores_valores)
        # Opciones de Pandas para mostrar la tabla completa en la consola
        pd.set_option('display.max_rows', None)
        pd.set_option('display.max_columns', None)
        pd.set_option('display.width', None)
        pd.set_option('display.max_colwidth', None)

        # Tabla de las métricas de cada algoritmo.
        data = {
            'Media Evaluaciones': [statistics.mean(evaluaciones)],
            'Mejor Evaluación': [min(evaluaciones)],
            'Desviación Evaluaciones': [statistics.stdev(evaluaciones)],
            'Media Valor': [statistics.mean(mejores_valores)],
            'Mejor Valor': [min(mejores_valores)],
            'Desviación Valor': [statistics.stdev(mejores_valores)]
        }

        print(pd.DataFrame(data))


if __name__ == "__main__":
    print()
