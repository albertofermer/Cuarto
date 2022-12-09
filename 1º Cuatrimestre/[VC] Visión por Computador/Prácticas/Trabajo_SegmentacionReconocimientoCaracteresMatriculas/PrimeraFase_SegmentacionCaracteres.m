% -----------------------------------------------------------------------%
%%                            Primera Fase
%                   Segmentación de Caracteres
% -----------------------------------------------------------------------%
clear all; clc; close all;
addpath('./Funciones_Necesarias\')
ruta = "./Material_Imagenes_Plantillas/01_Training/";
numImagenes = 5;

W = 11; sigma = round(W/5); % Parámetros de suavizado gaussiano
load('./Variables_Generadas/num_pix_min_normalizado.mat');
for imagen=1:numImagenes
    nombre = ruta + "Training_" + num2str(imagen,'%02d') + ".jpg";
    I = imread(nombre);
    % Seleccionamos la componente de color roja porque queremos detectar
    % también el logo de la UE para poder descartarlo posteriormente.
    R = I(:,:,1);
    figure('Name', 'Imagen Ruidosa y Suavizada'),
    subplot(2,1,1),imshow(R), title("Imagen Ruidosa")
    % Suavizamos la imagen con un filtro gaussiano para limpiarla.
    % Se utiliza el padding='replicate' para evitar falsos bordes alrededor de
    % la imagen.
    Rsuavizada = imfilter(R,fspecial('gaussian',W,sigma),'replicate');
    subplot(2,1,2), imshow(Rsuavizada), title("Imagen Suavizada")
    
    %% RESTA DE IMAGEN DE FONDO - IMAGEN RUIDOSA.
    % Con este método no tenemos que preocuparnos por el criterio de los
    % parámetros de otras técnicas como la de MEDIA-C.

    figure('Name',"Binarización de la Imagen de Entrada")
    Wdif = round(size(R,1)/3);                          % Tamaño de la máscara de medias.
    H = (1/(Wdif*Wdif)*ones(Wdif));                     % Máscara de medias para difuminar la imagen.
    Fondo = imfilter(double(Rsuavizada),H,'replicate'); % Aplicamos el filtro de medias para obtener el fondo.
    diferencia = double(Rsuavizada)-Fondo;              % Restamos el fondo a la imagen suavizada.
    salida = uint8(255*mat2gray(diferencia));           % Obtenemos la imagen con la iluminación corregida.
    subplot(3,1,1), imshow(uint8(Fondo)), title("Imagen de Fondo")
    subplot(3,1,2), imshow(salida), title("Iluminacion Coregida")

    % Restandole la imagen ruidosa al fondo hemos conseguido corregir la
    % iluminación. Ahora se le puede aplicar un método de umbralización
    % global para binarizar la imagen.

    Ib = salida < graythresh(salida)*255;
    subplot(3,1,3),imshow(Ib),title("Matricula Binarizada OTSU = " + graythresh(salida)*255);
    %figure('Name',"Matricula Binarizada OTSU " + imagen),imshow(Ib),title("Matricula Binarizada OTSU = " + graythresh(salida)*255);

    %% CIERRE MORFOLÓGICO
    % Aplicamos esta técnica para eliminar posibles imperfecciones que
    % hayan quedado tras binarizar la imagen.

    figure('Name',"Aplicación Cierre y Apertura Morfológica")
    subplot(3,1,1), imshow(Ib), title("Imagen antes de aplicar la técnica")
    Wclose = 7;
    Icierre = ordfilt2(Ib,Wclose*Wclose,ones(Wclose));
    Icierre = ordfilt2(Icierre,1,ones(Wclose));
    subplot(3,1,2), imshow(Icierre), title("Cierre Morfológico")

    %% APERTURA MORFOLÓGICA
    % Aplicamos esta técnica para eliminar posibles efectos secundarios del
    % cierre morfológico y eliminar pequeños falsos positivos del fondo.

    Iapertura = ordfilt2(Icierre,1,ones(Wclose));
    Iapertura = ordfilt2(Iapertura,Wclose*Wclose,ones(Wclose));
    subplot(3,1,3), imshow(Iapertura), title("Apertura Morfológica")

    % Nuestra zona de interés se encuentra en los objetos que están en la
    % línea central de la imagen.

    [N,M] = size(Rsuavizada);
    figure('Name',"Línea Central IMG-" + imagen), imshow(Iapertura), title("Línea Central tras Cierre y Apertura Morfológicos"), hold on,
    line([1,M],[round(N/2),round(N/2)], 'LineWidth', 2, 'Color', 'red'), hold off

    %% Etiquetado de la Imagen
    % Etiquetamos la imagen para saber qué etiquetas corresponden con la
    % línea anterior.

    [Ietiq,NumEtiquetas] = bwlabel(Iapertura);
    % Labels Of Interest: Las que se encuentren en la línea central.
    LoI = unique(Ietiq(round(N/2),:));

    % Eliminamos las dos primeras etiquetas (Fondo y logo de la UE)
    LoI = LoI(3:end);

    % Creamos una imagen donde se acumularán los objetos reconocidos.
    Ib = zeros(size(Iapertura));
    for etiqueta = 1:length(LoI)
        %figure, imshow(Ietiq == LoI(etiqueta))
        Ib = Ib + (Ietiq == LoI(etiqueta));
    end

    %% Eliminación de Posible Ruido
    % En el caso de que exista agrupaciones de píxeles en la línea de
    % interés menores a cierto umbral, debemos eliminarlas.

    % Desnormalizamos el número de píxeles mínimo.
    num_pix = floor(num_pix_min*(N*M));

    % Eliminamos las agrupaciones de píxeles menores al umbral.
    IbFinal = bwareaopen(Ib,num_pix);
    figure('Name',"Imagen Segmentada - " + imagen),imshow(Ib), title("Imagen Segmentada - " + imagen), hold on

    %% Detección de Bounding Boxes
    LineWidth = 2;
    Color = 'red';
    for k = 1 : length(LoI)
        I_k = (Ietiq == LoI(k));
        [filas,columnas] = find(I_k);
        fila_min = min(filas); columna_min = min(columnas);
        fila_max = max(filas); columna_max = max(columnas);
        medidas = [columna_min,fila_min;  columna_max,fila_min;   columna_min,fila_max;  columna_max,fila_max];

        line([medidas(1,1),medidas(2,1)],[medidas(1,2),medidas(2,2)], 'Color', Color, 'LineWidth', LineWidth)
        line([medidas(2,1),medidas(4,1)],[medidas(2,2),medidas(4,2)], 'Color', Color, 'LineWidth', LineWidth)
        line([medidas(3,1),medidas(4,1)],[medidas(3,2),medidas(4,2)], 'Color', Color, 'LineWidth', LineWidth)
        line([medidas(1,1),medidas(3,1)],[medidas(1,2),medidas(3,2)], 'Color', Color, 'LineWidth', LineWidth)

    end
    hold off
    pause; close all;

end

rmpath('./Funciones_Necesarias/')