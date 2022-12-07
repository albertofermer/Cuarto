% -----------------------------------------------------------------------%
%%                            Primera Fase
%                   Segmentación de Caracteres
% -----------------------------------------------------------------------%
addpath('./Funciones_Necesarias\')
ruta = "./Material_Imagenes_Plantillas/01_Training/";
numImagenes = 5;

W = 11; sigma = round(W/5); % Parámetros de suavizado
for imagen=1:numImagenes
    nombre = ruta + "Training_" + num2str(imagen,'%02d') + ".jpg";
    I = imread(nombre);
    R = I(:,:,1);

%     % Suavizamos la imagen con un filtro gaussiano para limpiarla.
%     % Se utiliza el padding='replicate' para evitar falsos bordes alrededor de
%     % la imagen.
     Rsuavizada = imfilter(R,fspecial('gaussian',W,sigma),'replicate');

     %% OTSU - No se debe usar
%     umbral = graythresh(Rsuavizada);
%     figure, imshow(Rsuavizada<255*umbral)

    %% OPCION-1: MEDIA-C. Umbralizacion Local para corregir defectos de iluminacion 
     Ifilt = (funcion_umbralizacion_local(Rsuavizada,ones(25),'zeros'));
     figure('Name',"Imagen Umbralizacion local  " + imagen)
     Ifilt3 = ordfilt2(Ifilt,8*8,ones(8),'zeros'); %% Justificar la eleccion del umbral
     Ifilt4 = ordfilt2(Ifilt3,1,ones(8),'zeros');
     imshow(Ifilt4)

    %% OPCION-2: RESTA DE IMAGEN DE FONDO - IMAGEN RUIDOSA.

%     [Ietiq] = bwlabel(Ifilt3);
%     imshow(Ietiq == 10)

    % Me quedo con las etiquetas que haya en la línea central de la imagen.

%     Word = 5;
%     figure('Name',"Imagen Final"), imshow(Ifiltrada ~= 0)

    % Mostramos la imagen segmentada con el umbral de otsu.
    % No usar umbralizacion global. Es mejor utilizar umbralización local para
    % evitar defectos de iluminación.
    %figure('Name',"Imagen DesvTipica " + imagen)
    %imshow((mat2gray(Ifilt)))


    %figure, imshow(Ifilt1)



end

rmpath('./Funciones_Necesarias/')