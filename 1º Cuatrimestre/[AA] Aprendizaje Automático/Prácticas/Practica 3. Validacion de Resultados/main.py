## Importamos las bibliotecas necesarias
# Bibliotecas Numéricas
import numpy as np
import pandas as pd

# Bibliotecas Gráficas
import matplotlib.pyplot as plt
import seaborn as sns; sns.set()
# Bibliotecas de Aprendizaje Automático
from sklearn import svm, datasets
from sklearn.inspection import DecisionBoundaryDisplay

# Importamos el dataset Iris
iris = datasets.load_iris()

# Escogemos dos de los atributos del dataset.
X = iris.data[:, :2]  # 150 instancias
y = iris.target
clases = np.unique(y)

fig, ax = plt.subplots()
ax.scatter(X[:, 0], X[:, 1])
plt.show()
# print(y.size) # 150
# print(clases) # [0 1 2]

X_0 = X[y == 0]  # Instancias de la clase 1
X_1 = X[y == 1]  # Instancias de la clase 2
X_2 = X[y == 2]  # Instancias de la clase 3
