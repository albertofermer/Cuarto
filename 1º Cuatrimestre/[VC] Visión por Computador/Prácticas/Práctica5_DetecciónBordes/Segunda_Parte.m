    %=========================================================================%
%                          SEGUNDA PARTE                                  %
%         IMPLEMENTACIÓN DE ALGORITMO DE DETECCIÓN DE BORDES              %
%                   BASADO EN SEGUNDA DERIVADA                            %
%=========================================================================%

addpath('./Funciones_Necesarias\')

% Implementar un detector de bordes basado en primera derivada a través de
% la función  [Gx, Gy, modG] = funcion_calcula_gradiente(I,Hx,Hy)

%-------------------------------------------------------------------------%
%                           IMAGEN DE INTENSIDAD                          %
%-------------------------------------------------------------------------%
P5 = imread("P5.tif");
I = uint8(rgb2gray(P5));
%-------------------------------------------------------------------------%
%                 IMAGEN SUAVIZADA CON FILTRO GAUSSIANO W=5               %
%-------------------------------------------------------------------------%
W = 5;
sigma = W/5;
Igauss = imfilter(I,fspecial('gaussian',W,sigma));

Hx_sobel = [ -1 0 1; -2 0 2; -1 0 1];
Hy_sobel = [ -1 -2 -1; 0 0 0; 1 2 1];
[Gx_original,Gy_original,modG_original] = funcion_calcula_gradiente(I,Hx_sobel,Hy_sobel);
[Gx_gauss,Gy_gauss,modG_gauss] = funcion_calcula_gradiente(Igauss,Hx_sobel,Hy_sobel);

maximo_modG = max(modG_original(:));
minimo_modG = min(modG_original(:));
umbrales = [0.1*maximo_modG,0.25*maximo_modG,0.5*maximo_modG,0.75*maximo_modG];


magnitudes_original = cat(3,Gx_original,Gy_original,modG_original);
magnitudes_gauss = cat(3,Gx_gauss,Gy_gauss,modG_gauss);

magnitudes_original_title = ["Gx\_original","Gy\_original","modG\_original"];
magnitudes_gauss_title = ["Gx\_Gauss","Gy\_Gauss","modG\_Gauss"];


%% Original
for k=1:length(umbrales) % 4 umbrales
    figure,
    sgtitle(['Umbral_',num2str(k),': ', num2str(umbrales(k))])
    z = 1;
    for j = 1:3 % 3 filas (Gx, Gy y modG)
        subplot(3,2,(z)), imshow(mat2gray(abs(magnitudes_original(:,:,j)),[minimo_modG,maximo_modG])),
        title(magnitudes_original_title(j))
        subplot(3,2,(z+1)), imshow(abs(magnitudes_original(:,:,j))>umbrales(k)),
        title([magnitudes_original_title(j), "binarizada"])
        z = z + 2;
    end
end

%% Gauss
for k=1:length(umbrales) % 4 umbrales
    figure,
    sgtitle(['Umbral_',num2str(k),': ', num2str(umbrales(k))])
    z = 1;
    for j = 1:3 % 3 filas (Gx, Gy y modG)
        subplot(3,2,(z)), imshow(mat2gray(abs(magnitudes_gauss(:,:,j)),[minimo_modG,maximo_modG])),
        title(magnitudes_gauss_title(j))
        subplot(3,2,(z+1)), imshow(abs(magnitudes_gauss(:,:,j))>umbrales(k)),
        title([magnitudes_gauss_title(j), "binarizada"])
        z = z + 2;
    end
end

figure,
imshow(abs(Gx_gauss)>max(abs(Gx_original(:))*0.1)), title("Sin línea horizontal")


rmpath('./Funciones_Necesarias\')



