%-------------------------------------------------------------------------%
%=========================================================================%
%                       PRIMERA PARTE: SIMULACIÓN DE RUIDOS               %
%=========================================================================%

% Añadimos el directorio de las funciones que vamos a utilizar al path.
addpath('./Funciones_Necesarias\')

% Leemos la imagen del directorio de trabajo.
I = imread('./P4.tif');

% Sacamos el tamaño de la imagen leída.
[N,M] = size(I);

%-------------------------------------------------------------------------%
%       1. Corrompe la imagen anterior con ruido de tipo sal y pimienta   %
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% El ruido sal y pimienta se caracteriza por tener píxeles de nivel alto y%
% píxeles de nivel bajo repartidos por toda la imagen de manera aleatoria.%
% Para generarlos necesitamos dos umbrales para decidir a partir de       %
% qué valor generamos los puntos de nivel alto (sal) y a partir de cuál   %
% generamos los de nivel bajo (pimienta).                                 %
%-------------------------------------------------------------------------%

p = 0.9;
q = 0.95;
A = funcion_genera_ruido_salpimienta(I,p,q);

%-------------------------------------------------------------------------%
%       2. Corrompe la imagen anterior con ruido gaussiano                %
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% El ruido gaussiano se caracteriza por una media y una desviación típica.%
% Por eso la función que genera el ruido gaussiano tendrá estos dos       %
% parámetros como entrada.                                                %
%-------------------------------------------------------------------------%

media = 0;
desv = 10;
G = funcion_genera_ruido_gaussiano(I,media,desv);

%-------------------------------------------------------------------------%
%                   3. Visualiza las imágenes ruidosas                    %
%-------------------------------------------------------------------------%

subplot(2,1,1), imshow(A), title('Imagen con ruido sal y pimienta')
subplot(2,1,2), imshow(G), title('Imagen con ruido gaussiano')

%-------------------------------------------------------------------------%
% Representa en un mismo gráfico la variación de los niveles de gris a los
% largo de la línea horizontal central de la imagen original, de la imagen
% con ruido "sal y pimienta" y de la imagen con ruido gaussiano.
%-------------------------------------------------------------------------%

% Obtenemos la línea horizontal central de cada una de las imágenes.
original = I(round(N/2),:);     % Imagen original.
salpimienta = A(round(N/2),:);  % Imagen con ruido "sal y pimienta".
gaussiana = G(round(N/2),:);    % Imagen con ruido gaussiano.


% Representamos los niveles de gris de cada uno de los píxeles de la línea
% horizontal:
figure,
plot(original,'-r'), hold on
plot(salpimienta,'.k'), hold on
plot(gaussiana,'-b')
legend('Original','Ruido Sal y Pimienta', 'Ruido Gaussiano')

%-------------------------------------------------------------------------%

rmpath("./Funciones_Necesarias\")
clear all
clc