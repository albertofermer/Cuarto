load('./Variables_Necesarias/ValoresColores.mat')
addpath('./04_ImagenesFuncionamientoAlgoritmo/')
addpath('./Funciones_Necesarias/')
K = 10;
knn_RGB_rojoyverde = fitcknn(ValoresColores(:,1:3),(CodifValoresColores==128)*128 ...
    + (CodifValoresColores==255)*255,"NumNeighbors",K);

save('./Variables_Generadas/clasificador_knn',"knn_RGB_rojoyverde");

rmpath('04_ImagenesFuncionamientoAlgoritmo\')
rmpath('Funciones_Necesarias\')
clear all
clc
