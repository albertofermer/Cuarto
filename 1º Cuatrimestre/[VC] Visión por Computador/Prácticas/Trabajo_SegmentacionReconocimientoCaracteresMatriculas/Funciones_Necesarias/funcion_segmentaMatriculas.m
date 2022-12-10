function [IetiqFinal, numFiguras, centroides, contornos] = funcion_segmentaMatriculas(Ic,num_pix_min,mostrar)

I = Ic;
figure('Name',"Imagen Original"),imshow(Ic), title("Imagen Original")
W = 11; sigma = round(W/5); % Parámetros de suavizado gaussiano
% Seleccionamos la componente de color roja porque queremos detectar
% también el logo de la UE para poder descartarlo posteriormente.
R = I(:,:,1);
if (mostrar)
    figure('Name', 'Imagen Ruidosa y Suavizada'),
    subplot(2,1,1),imshow(R), title("Imagen Ruidosa")
end
% Suavizamos la imagen con un filtro gaussiano para limpiarla.
% Se utiliza el padding='replicate' para evitar falsos bordes alrededor de
% la imagen.
Rsuavizada = imfilter(R,fspecial('gaussian',W,sigma),'replicate');
if(mostrar)
    subplot(2,1,2), imshow(Rsuavizada), title("Imagen Suavizada")
end
%% RESTA DE IMAGEN DE FONDO - IMAGEN RUIDOSA.
% Con este método no tenemos que preocuparnos por el criterio de los
% parámetros de otras técnicas como la de MEDIA-C.

Wdif = round(size(R,1)/3);                          % Tamaño de la máscara de medias.
H = (1/(Wdif*Wdif)*ones(Wdif));                     % Máscara de medias para difuminar la imagen.
Fondo = imfilter(double(Rsuavizada),H,'replicate'); % Aplicamos el filtro de medias para obtener el fondo.
diferencia = double(Rsuavizada)-Fondo;              % Restamos el fondo a la imagen suavizada.
salida = uint8(255*mat2gray(diferencia));           % Obtenemos la imagen con la iluminación corregida.
% Restandole la imagen ruidosa al fondo hemos conseguido corregir la
% iluminación. Ahora se le puede aplicar un método de umbralización
% global para binarizar la imagen.

Ib = salida < graythresh(salida)*255;

if  (mostrar)
    figure('Name',"Binarización de la Imagen de Entrada")
    subplot(3,1,1), imshow(uint8(Fondo)), title("Imagen de Fondo")
    subplot(3,1,2), imshow(salida), title("Iluminacion Coregida")
    subplot(3,1,3),imshow(Ib),title("Matricula Binarizada OTSU = " + graythresh(salida)*255);
end

%figure('Name',"Matricula Binarizada OTSU " + imagen),imshow(Ib),title("Matricula Binarizada OTSU = " + graythresh(salida)*255);

%% CIERRE MORFOLÓGICO
% Aplicamos esta técnica para eliminar posibles imperfecciones que
% hayan quedado tras binarizar la imagen.


Wclose = 7;
Icierre = ordfilt2(Ib,Wclose*Wclose,ones(Wclose));
Icierre = ordfilt2(Icierre,1,ones(Wclose));

%% APERTURA MORFOLÓGICA
% Aplicamos esta técnica para eliminar posibles efectos secundarios del
% cierre morfológico y eliminar pequeños falsos positivos del fondo.

Iapertura = ordfilt2(Icierre,1,ones(Wclose));
Iapertura = ordfilt2(Iapertura,Wclose*Wclose,ones(Wclose));
if (mostrar)
    figure('Name',"Aplicación Cierre y Apertura Morfológica")
    subplot(3,1,1), imshow(Ib), title("Imagen antes de aplicar la técnica")
    subplot(3,1,2), imshow(Icierre), title("Cierre Morfológico")
    subplot(3,1,3), imshow(Iapertura), title("Apertura Morfológica")
end


% Nuestra zona de interés se encuentra en los objetos que están en la
% línea central de la imagen.

[N,M] = size(Rsuavizada);
if(mostrar)
    figure('Name',"Línea Central"), imshow(Iapertura), title("Línea Central tras Cierre y Apertura Morfológicos"), hold on,
    line([1,M],[round(N/2),round(N/2)], 'LineWidth', 2, 'Color', 'red'), hold off
end
%% Etiquetado de la Imagen
% Etiquetamos la imagen para saber qué etiquetas corresponden con la
% línea anterior.

[ISegEtiq,~] = bwlabel(Iapertura);
% Labels Of Interest: Las que se encuentren en la línea central.
LoI = unique(ISegEtiq(round(N/2),:));

% Eliminamos las dos primeras etiquetas (Fondo y logo de la UE)
LoI = LoI(3:end);

% Creamos una imagen donde se acumularán los objetos reconocidos.
Ib = zeros(size(Iapertura));
for etiqueta = 1:length(LoI)
    %figure, imshow(Ietiq == LoI(etiqueta))
    Ib = Ib + (ISegEtiq == LoI(etiqueta));
end

%% Eliminación de Posible Ruido
% En el caso de que exista agrupaciones de píxeles en la línea de
% interés menores a cierto umbral, debemos eliminarlas.

% Desnormalizamos el número de píxeles mínimo.
num_pix = floor(num_pix_min*(N*M));

% Eliminamos las agrupaciones de píxeles menores al umbral.
IbFinal = bwareaopen(Ib,num_pix);

[IetiqFinal, numFiguras] = bwlabel(IbFinal);
LoI = unique(IetiqFinal(round(N/2),:));
LoI = LoI(2:end);
if(mostrar)
    figure('Name',"Imagen Segmentada"),imshow(IbFinal), title("Imagen Segmentada")
end

figure('Name', "Imagen Final Segmentada"), imshow(Ic), title("Imagen Final Segmentada"), hold on

%% Detección de Bounding Boxes
LineWidth = 2;
Color = 'red';
contornos = [];
for k = 1 : length(LoI)
    I_k = (IetiqFinal == LoI(k));
    [filas,columnas] = find(I_k);
    fila_min = min(filas); columna_min = min(columnas);
    fila_max = max(filas); columna_max = max(columnas);

    contornos_representa = [columna_min,fila_min;  columna_max,fila_min;   columna_min,fila_max;  columna_max,fila_max];
    contornos = [contornos;columna_min,fila_min;  columna_max,fila_min;   columna_min,fila_max;  columna_max,fila_max];

    line([contornos_representa(1,1),contornos_representa(2,1)],[contornos_representa(1,2),contornos_representa(2,2)], 'Color', Color, 'LineWidth', LineWidth)
    line([contornos_representa(2,1),contornos_representa(4,1)],[contornos_representa(2,2),contornos_representa(4,2)], 'Color', Color, 'LineWidth', LineWidth)
    line([contornos_representa(3,1),contornos_representa(4,1)],[contornos_representa(3,2),contornos_representa(4,2)], 'Color', Color, 'LineWidth', LineWidth)
    line([contornos_representa(1,1),contornos_representa(3,1)],[contornos_representa(1,2),contornos_representa(3,2)], 'Color', Color, 'LineWidth', LineWidth)


    centroides = regionprops(IetiqFinal,'Centroid');
    centroides = cat(1,centroides.Centroid);
    for c = 1:length(centroides)
        plot(centroides(c,1),centroides(c,2),'*r')
    end
end


end