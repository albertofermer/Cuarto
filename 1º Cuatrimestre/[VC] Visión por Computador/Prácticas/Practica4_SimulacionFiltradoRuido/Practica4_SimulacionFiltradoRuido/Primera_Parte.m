addpath('./Funciones_Necesarias\')

% SIMULACIÓN DE RUIDOS  
    % Lee la imagen P4.tif
I = imread('./P4.tif');
[N,M] = size(I);
    % Corrompe la imagen anterior con ruido de tipo sal y pimienta

    p = 0.9;
    q = 0.95;
    A = funcion_genera_ruido_salpimienta(I,p,q);

    % Corrompe la imagen con ruido tipo gaussiano.
    media = 0;
    desv = 10;
    G = funcion_genera_ruido_gaussiano(I,media,desv);

    % Visualiza las imágenes ruidosas. Representa en un mismo gráfico la
    % variación de los niveles de gris a los largo de la línea horizontal
    % central para la imagen original, la imagen con ruido sal y pimienta y
    % para la imagen con ruido gaussiano.

    subplot(2,1,1), imshow(A), title('Imagen con ruido salypimienta')
    subplot(2,1,2), imshow(G), title('Imagen con ruido gaussiano')

    original = I(round(N/2),:);
    salpimienta = A(round(N/2),:);
    gaussiana = G(round(N/2),:);
    
    figure,
    plot(original,'-r'), hold on
    plot(salpimienta,'.k'), hold on
    plot(gaussiana,'.b')

    legend('Original','Ruido Sal y Pimienta', 'Ruido Gaussiano')