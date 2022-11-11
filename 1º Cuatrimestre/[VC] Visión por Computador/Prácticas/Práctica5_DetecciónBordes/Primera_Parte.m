%=========================================================================%
%%                              PRIMERA PARTE                             %%
%        GENERACIÓN DE IMÁGENES. ANÁLISIS DE PERFILES DE INTENSIDAD       %
%=========================================================================%

addpath('./Funciones_Necesarias\')

%-------------------------------------------------------------------------%
% 1. A partir de la imagen P5 genera y visualiza las siguientes imágenes.
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
%                           IMAGEN DE INTENSIDAD                          %
%-------------------------------------------------------------------------%
P5 = imread("P5.tif");
I = uint8(mean(P5,3));
figure,
imshow(uint8(I));

%-------------------------------------------------------------------------%
%                 IMAGEN SUAVIZADA CON FILTRO GAUSSIANO W=5               %
%-------------------------------------------------------------------------%
W = 5;
sigma = W/5;
Igauss = imfilter(I,funcion_calcula_mascara_gaussiana_eficiente(W,sigma,false));

figure,
imshow(Igauss)

