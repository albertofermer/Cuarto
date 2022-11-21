%-------------------------------------------------------------------------%
%%                      PRIMERA PARTE                                    %%
%-------------------------------------------------------------------------%
addpath('Funciones Necesarias\')
Imagen = imread('ImagenesPractica\PrimeraParte\P6_1.tif');

% Genera la imagen de intensidad de P6_1.tif
I = rgb2gray(Imagen);

% Aplicar un detector de bordes verticales sobre I.
Hx_sobel = [-1 0 1; -2 0 2; -1 0 1];
Hy_sobel = [-1, 2 -1; 0 0 0; 1 2 1];
[Gx,Gy,modG] = funcion_calcula_gradiente(I,Hx_sobel, Hy_sobel);
Ib = edge(I,'sobel',0.3*max(abs(Gx(:))>0.3*max(Gx(:))));


% Aplica a Ib la transformada de Hough

[H,theta, rho] = hough(Ib, 'Theta', -90:89);

% ¿Qué representan theta y rho?
% theta:ángulo de la recta perpendicular que va desde el punto de 
% coordenadas hasta la recta.  
% rho: distancia del origen de coordenadas a la recta

% ¿Cuál es el significado de los valores almacenados en H?
% El número de puntos alineados que se encuentran en cada recta.

% ¿Cómo es la discretización que se realiza del espacio de parámetros en
% esta configuración por defecto?



rmpath('Funciones Necesarias\')