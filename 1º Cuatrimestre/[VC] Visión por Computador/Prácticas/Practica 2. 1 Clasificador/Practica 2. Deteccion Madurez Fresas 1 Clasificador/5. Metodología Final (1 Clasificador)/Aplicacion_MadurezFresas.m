% Aplicación que detecta el número de fresas que hay en una imagen y
% muestra en rojo los píxeles rojos de la fresa y en amarillo los píxeles
% verdes.

% Elimina las regiones rojas que sean menores a un umbral (agrupación
% mínima de píxeles rojos)

% Abre una imagen por cada fresa que haya en la imagen y muestra el grado
% de madurez que tiene dicha fresa junto a la fresa detectada.

%% 1. Carga de Datos.

% Añadimos el directorio del que vamos a leer las imágenes.
addpath('./04_ImagenesFuncionamientoAlgoritmo/');
addpath('./Funciones_Necesarias/');
% Cargamos el dato de la agrupación mínima de píxeles normalizado.
load('./Variables_Necesarias/agrupacion_minima_pixeles_normalizada.mat')

% Cargamos los clasificadores que vamos a utilizar
load('./Variables_Necesarias/clasificador_knn.mat')


%% 2. Bucle de Procesamiento de Imágenes
% Leeremos 11 imágenes en total
numImagenes = 11;
for i = 1:numImagenes

    ValoresColores = [];
    %% 2.1 Leemos la imagen del directorio.
    nombre = ['imagen', num2str(i), '.jpeg'];
    if(i < 10)
        nombre = ['imagen0',num2str(i), '.jpeg'];
    end
    I_color = imread(nombre);

    % Reducimos la resolución de la imagen a la mitad.
    I_reducida = imresize(I_color,0.5); 

    %% 2.2 Extracción de los datos normalizados de la imagen
     R = double(I_reducida(:,:,1))/255;
     G = double(I_reducida(:,:,2))/255; 
     B = double(I_reducida(:,:,3))/255;

    %% 2.3 Creación de las imágenes resultado
        % Sacamos el tamaño de la imagen para crear nuestras imágenes
        % binarias.
        [N, M] = size(R);
        
        % Inicializamos los valores de la imagen a 0.
        KNNrgb = zeros(N,M);

        % Recorremos la imagen por columnas y sacamos los descriptores
        % seleccionados.
        input = []; 
        for k=1:M
            input_temp = [R(:,k), G(:,k), B(:,k)];
            input = [input ; input_temp];
        end

        %% 2.4 Aplicación de los clasificadores
        KNN_RGB_Vector = predict(knn_RGB_rojoyverde,input);

        %% 2.5 Transformamos el vector generado en una imagen.
        ind =1;
        for m=1:M
            KNNrgb(:,m) = KNN_RGB_Vector(ind:ind+N-1);
            ind = ind+N;
        end

            %% 2.5.1 Reescalamos de nuevo la imagen
    I_original = round(imresize(I_reducida,size(I_color,1:2),'nearest'));
    KNNrgb = round(imresize(KNNrgb,size(I_color,1:2),'nearest'));

        %% 2.6 Eliminamos las agrupaciones más pequeñas que el umbral
        %% 2.6.1 Desnormalizamos el valor
        % agrupacion_minima_normalizada
        area_minima = round( agrupacion_minima_normalizada * M * N);
        Img_Binaria = bwareaopen(KNNrgb,area_minima);
        %% 2.6.2 Eliminamos las agrupaciones que no son consideradas fresas.
    % Etiqueto las agrupaciones que contengan pixeles rojos o verdes.
    [Ietiq,numAgrupaciones] = bwlabel(Img_Binaria);
        %Sacamos las contribuciones rojas de la imagen
        Img_roja = KNNrgb == 255;
        Img_roja = bwareaopen(Img_roja,area_minima);
        % Sacamos las contribuciones verdes de la imagen
        Img_verde = KNNrgb == 128;
        Img_verde = bwareaopen(Img_verde,area_minima);
    Iacum = zeros(size(Ietiq));
    % Para cada agrupación compruebo que tenga píxeles rojos.
    for agrupacion=1:numAgrupaciones
        Ibaux = (Ietiq == agrupacion) .* Img_roja;
        pixeles_rojos = sum(sum(Ibaux));
        % Si la suma es distinta de 0, es una fresa
        if ( pixeles_rojos ~= 0 ) 
            % En Iacum se encontrarán todas las fresas de la imagen.
            Iacum = Iacum + (Ietiq == agrupacion);
        end
    end
    
    % Visualiza la imagen completa con todas las fresas detectadas
        funcion_visualizaColores(I_original, ...
    (Iacum == Img_roja & Iacum ~= 0)*255 + ...
    (Iacum == Img_verde & Iacum ~= 0)*128, true);
    [Ietiq,numAgrupaciones] = bwlabel(Iacum);

    %% 2.7 Visualizamos el resultado
    % Mostramos una imagen por cada fresa detectada junto a su grado de
    % madurez.
    for agrupacion=1:numAgrupaciones

        % Añadimos la codificación del color a las agrupaciones.
        Icodif = (Ietiq == agrupacion) .* ((Img_roja*255) + (Img_verde*128));
        
        % Visualizamos la imagen
        funcion_visualizaColores(I_original, ...
            Icodif, true);
        
        % Calculamos el grado de madurez
        num_pix_amarillos = sum((Icodif == 128),'all');
        num_pix_rojos = sum((Icodif == 255),'all');
        madurez = num_pix_rojos/(num_pix_amarillos + num_pix_rojos) * 100;

        % Mostramos la madurez como título de la imagen
        title(['Madurez: ', num2str(madurez), '%' ]);
    end 

    pause;
    close all
end


rmpath('./04_ImagenesFuncionamientoAlgoritmo/');
rmpath('./Funciones_Necesarias/');
clear 
clc



