## PERCEPTRÓN SIMPLE ##

import numpy as np
import pandas as pd
import random


# Se implementará el algoritmo del perceptron umbral para datos
# linealmente separables y el del descenso por gradiente y regla
# delta para datos quasi-separables.

## Ejercicio 1. ##
# Programar el perceptron umbral para el aprendizaje
# de la puerta lógica AND, OR y NOT.

def threshold(value, umbral):
    if value < 0:
        return 0
    else:
        return 1


def perceptron_umbral(x, y, alpha):
    # set initial weights to random
    w_old = np.array([random.random() for _ in range(x.shape[1] + 1)])
    w_new = w_old
    x_amp = np.c_[-1 * np.ones(x.shape[0]), x]  # x_0 = -1
    # Repeat until termination condition:
    for i in range(1000):
        w_aux = w_new
        for x_ in np.c_[x_amp, y]:
            o = threshold(sum(w_old * x_[0:len(x_)-1]), w_old[0])
            # for each weight do:
            for wi in range(len(w_new)):
                # wi = wi +  alpha*(y-o)*xi
                w_new[wi] += (alpha * (x_[-1] - o) * x_[wi])
            w_old = w_new
    return w_old


# DataSet - Puerta AND
X = np.array([[0, 0], [0, 1], [1, 0], [1, 1]])
Y = np.array([0, 0, 0, 1])
print(perceptron_umbral(X, Y, alpha=0.01))

# DataSet - Puerta OR
X = np.array([[0, 0], [0, 1], [1, 0], [1, 1]])
Y = np.array([0, 1, 1, 1])
print(perceptron_umbral(X, Y, alpha=0.01))

# DataSet - Puerta NOT
X = np.array([[0], [1]])
Y = np.array([1, 0])
print(perceptron_umbral(X, Y, alpha=0.01))