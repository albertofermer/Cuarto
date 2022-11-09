%% Segunda Parte. Implementación de filtros gaussiano, mediana y adapatativo.

addpath("./Funciones_Necesarias\")
% Leemos la imagen
Img = imread('P4.tif');
% Corrompemos la imagen con ruido sal y pimienta.
A = funcion_genera_ruido_salpimienta(Img,0.9,0.95);
G = funcion_genera_ruido_gaussiano(Img,0,10);

    % a. Un filtro gaussiano con W = 5*sigma, (W = 5)
    w = 5;
    sigma = 1;
    H = funcion_calcula_mascara_gaussiana(w,sigma,true);
    H_matricial = funcion_calcula_mascara_gaussiana_eficiente(w,sigma,false);
    H2 = fspecial('gaussian',w,sigma);
    funcion_compara_matrices(H,H2) % error 10^(-17) -- asumible.
    funcion_compara_matrices(H,H_matricial); % Iguales.

    % Aplicar la máscara
    I_filtrada_gaussiana = imfilter(A,H);
    subplot(2,1,1), imshow(A)
    subplot(2,1,2), imshow(I_filtrada_gaussiana)
    % b. Un filtro de la mediana considerando un entorno de vecindad 5x5.

    A_filt = funcion_filtroMediana(A,ones(5),'zeros');
    A_filt2 = medfilt2(A,[5,5],'zeros');

    % Comparamos las imágenes para comprobar que son iguales.
    
    funcion_compara_matrices(A_filt,A_filt2)  % 1 = Iguales
    subplot(2,1,1), imshow(A)
    subplot(2,1,2), imshow(A_filt)

    % Comprobamos que el filtro de la mediana es una buena opción para
    % eliminar el ruido tipo sal y pimienta. Fallará solamente en aquellas
    % agrupaciones de píxeles corrompidos que tengan un tamaño igual o
    % mayor a la matriz de vecindad de la función. Podemos arreglar ese
    % fallo si aumentamos las dimensiones de la matriz de vecindad, pero
    % como consecuencia perderíamos información de la imagen.
    
    % c. Un fitro adaptativo que actúe en un entorno de vecindad 7x7

    error = double(A) - double(Img);
    VarRuido = var(error(:));
    Ifilt = funcion_filtadapt(A,ones(7),VarRuido,'symmetric');
    figure,
    imshow(Ifilt)

    Ifilt_matriz = funcion_filtadapt_matricial(A,ones(7),VarRuido,'symmetric');
    figure,imshow(Ifilt_matriz)

    funcion_compara_matrices(Ifilt,Ifilt_matriz)
    

rmpath("./Funciones_Necesarias/")