%-------------------------------------------------------------------------%
%=========================================================================%
%                  SEGUNDA PARTE: IMPLEMENTACIÓN DE FILTROS               %
%                      GAUSSIANO, MEDIANA Y ADAPTATIVO                    %
%=========================================================================%

%-------------------------------------------------------------------------%
% Esta práctica se realizará sobre la imagen corrompida con ruido sal y
% pimienta de la parte anterior
%-------------------------------------------------------------------------%

% Añadimos el directorio de las funciones que vamos a utilizar al path.
addpath("./Funciones_Necesarias\")

% Leemos la imagen
Img = imread('P4.tif');

% Corrompemos la imagen con ruido sal y pimienta.
A = funcion_genera_ruido_salpimienta(Img,0.9,0.95);



%-------------------------------------------------------------------------%
%                   a) Filtro gaussiano con W=5 y sigma W/5               %
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% Los filtros lineales ponderados son filtros que utilizan la operación de
% convolución para suavizar la imagen. Las máscaras que utilizan estos
% filtros tienen las siguientes características:
%
%   - La suma de todos sus componentes es 1.
%   - Solo utilizan valores positivos.
%   - El valor central es mayor que el de los extremos.
%
% 
% Un filtro gaussiano se caracteriza por el tamaño de ventana que utiliza
% el filtro (W) y la desviación típica que caracteriza la campana de Gauss
% de media 0.
%
% El tamaño de ventana del filtro gaussiano debe ser 5 veces la desviación
% típica. De esta forma se cubre la mayor parte del área delimitada por la
% función gaussiana.
%-------------------------------------------------------------------------%


W = 5;      % Tamaño de ventana
sigma = W/5;  % Desviación típica de la campana de Gauss


%-------------------------------------------------------------------------%
% La máscara de filtro gaussiano se puede calcular de tres formas:
%       1. Se construye la máscara elemento a elemento.
%       2. Se construye la máscara con meshgrid más eficientemente.
%       3. Utilizando la función de Matlab fspecial('gaussian',W,sigma)
%-------------------------------------------------------------------------%

% 1. Se construye la máscara elemento a elemento.
H = funcion_calcula_mascara_gaussiana(W,sigma,true);

% 2. Se construye la máscara con meshgrid más eficientemente.
H_matricial = funcion_calcula_mascara_gaussiana_eficiente(W,sigma,false);

% 3. Utilizando la función de Matlab fspecial('gaussian',W,sigma)
H_matlab = fspecial('gaussian',W,sigma);

%-------------------------------------------------------------------------%
%                       COMPARACIÓN MÁSCARAS GAUSSIANAS                   %
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% Al comparar la máscara generada por las funciones hechas a mano y la
% generada por la función de Matlab encontramos que existe un error de
% 10^-17. Este error debe corresponder a la forma que tiene esta versión de
% Matlab de computar los números en coma flotante.
%
% Podemos asumir este error ya que es un error muy pequeño.
%-------------------------------------------------------------------------%

funcion_compara_matrices(H,H_matlab)
disp('Asumible')

%-------------------------------------------------------------------------%
% Las máscaras gaussianas hechas tanto de la forma 1. como de la forma 2.
% son exactamente iguales.
%-------------------------------------------------------------------------%

funcion_compara_matrices(H,H_matricial);

%-------------------------------------------------------------------------%
%                           APLICACIÓN DE LA MÁSCARA                      %
%-------------------------------------------------------------------------%
I_filtrada_gaussiana = imfilter(A,H);
figure,
subplot(2,1,1), imshow(A), title('Imagen Original')
subplot(2,1,2), imshow(I_filtrada_gaussiana), title('Imagen Filtrada con Filtro gaussiano')

%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
%                   b) Filtro de la mediana con W = 5                     %
%-------------------------------------------------------------------------%
%-------------------------------------------------------------------------%
% El filtro de la mediana es un filtro no lineal ya que ordena la matriz de
% vecindad de cada píxel de menor a mayor y escoge el valor que se
% encuentra en medio.
%-------------------------------------------------------------------------%


% Filtro de la mediana programada a mano.
A_filt = funcion_filtroMediana(A,ones(5),'zeros');

% Filtro de la mediana de Matlab.
A_filt2 = medfilt2(A,[5,5],'zeros');

%-------------------------------------------------------------------------%
%                       COMPARACIÓN FILTROS DE LA MEDIANA                 %
%-------------------------------------------------------------------------%
funcion_compara_matrices(A_filt,A_filt2)

figure,
subplot(2,1,1), imshow(A), title('Imagen Original')
subplot(2,1,2), imshow(A_filt), title('Imagen Filtrada con el filtro de la mediana')

%-------------------------------------------------------------------------%
% Las imágenes filtradas son exactamente iguales, por lo que podemos
% afirmar que nuestro filtro funciona igual que el de Matlab.
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% Comprobamos que el filtro de la mediana es una buena opción para
% eliminar el ruido tipo sal y pimienta. Fallará solamente en aquellas
% agrupaciones de píxeles corrompidos que tengan un tamaño igual o
% mayor a la matriz de vecindad de la función. Podemos arreglar ese
% fallo si aumentamos las dimensiones de la matriz de vecindad, pero
% como consecuencia perderíamos información de la imagen.
%-------------------------------------------------------------------------%
%-------------------------------------------------------------------------%



%-------------------------------------------------------------------------%
%                   c) Filtro adaptativo con W = 7                        %
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% Este tipo de filtros adaptan su comportamiento en función de las
% características locales de la imagen. Intenta conservar los detalles de
% la imagen mientras elimina el ruido en las zonas constantes.
%
% En las zonas constantes ( variación pequeña de los niveles de gris) el
% filtro considera que no existen bordes y aplica un filtro de medias.
%
% En las zonas de bordes ( variación grande de los niveles de gris) el
% filtro considera que es un borde y no aplica ningún filtro.
%-------------------------------------------------------------------------%

% Tenemos que obtener la varianza del ruido global de la imagen.
error = double(A) - double(Img);
VarRuido = var(error(:));

%-------------------------------------------------------------------------%
% La función funcion_filtadapt se puede programar de dos formas:
%
%   1. Recorriendo la imagen píxel a píxel y haciendo los cálculos píxel a
%   píxel.
%
%   2. Utilizando operaciones matriciales junto con filtros de medias y de
%   desviación típica.
%-------------------------------------------------------------------------%

% 1. Recorriendo la imagen píxel a píxel y haciendo los cálculos píxel a píxel.
Ifilt = funcion_filtadapt(A,ones(7),VarRuido,'symmetric');

% 2. Utilizando operaciones matriciales junto con filtros de medias y de desviación típica.
Ifilt_matriz = funcion_filtadapt_matricial(A,ones(7),VarRuido);

%-------------------------------------------------------------------------%
%                       COMPARACIÓN FILTROS ADAPTATIVOS                   %
%-------------------------------------------------------------------------%

figure, imshow(Ifilt), 
title('Imagen filtrada con filtro adaptativo píxel a píxel')

figure, imshow(Ifilt_matriz), 
title('Imagen filtrada con filtro adaptativo matricial')

funcion_compara_matrices(Ifilt,Ifilt_matriz)

%-------------------------------------------------------------------------%
% Ambas imágenes filtradas son iguales. Sin embargo, sigue existiendo ruido
% en la imagen. Esto se debe a que el filtro adaptativo detecta los píxeles
% con nivel alto y nivel bajo como bordes ya que tienen una varianza muy
% grande respecto al ruido general de la imagen.
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%

rmpath("./Funciones_Necesarias/")
clear