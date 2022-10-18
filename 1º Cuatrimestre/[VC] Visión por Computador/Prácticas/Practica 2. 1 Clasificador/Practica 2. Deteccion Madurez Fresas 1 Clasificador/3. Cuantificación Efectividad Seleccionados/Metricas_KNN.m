% Importamos los modelos
load('./Clasificadores/clasificador_KNNRGB_RojoVerde.mat')
load('./Clasificadores/modeloKNNRGB_Rojo.mat')
load('./Clasificadores/modelo_KNNRGB_Verde.mat')
load('./Variables_Necesarias/agrupacion_minima_pixeles_normalizada.mat')
% Añadimos la carpeta de imágenes y funciones al path
addpath('../../Material_Imagenes/03_MuestrasFresas/')
addpath('./Funciones/')
addpath('./Variables_Necesarias/')
% Aplicamos la función de knn para crear un modelo

K = 5; % Utilizaremos los 5 vecinos más cercanos.

addpath('../2. Seleccion de Características/Funciones Necesarias/')

% Aplicamos el clasificador
    % 1. Sacamos los descriptores de la imagen que queremos clasificar.

numImagenes = 3;
metricas_knnrgb_2clasificadores = [];
metricas_knnrgb_1clasificador = [];
% Para cada imagen del directorio...
for i = 1:numImagenes

    ValoresColores = [];
    %nombre = ['imagen', num2str(i)];
    nombre = ['SegFresas', num2str(i)];
    nombre_gold = ['SegFresas', num2str(i), '_Gold'];
    I_color = imread([nombre, '.tif']);
    I_gold = imread([nombre_gold, '.tif']);
    I_reducida = imresize(I_color,0.5);   
    % MODELO RGB:
    R = double(I_reducida(:,:,1))/255;
    G = double(I_reducida(:,:,2))/255;
    B = double(I_reducida(:,:,3))/255;
         
        %% Aplicamos el modelo
        [N, M] = size(R); % Sacamos el tamaño de la imagen

        KNNrgb_rojo = zeros(N,M); % Inicializamos matriz de resultados
        KNNrgb_verde = zeros(N,M); % Inicializamos matriz de resultados
        KNNrgb_1Clasificador = zeros(N,M); % Inicializamos matriz de resultados
                    
         
        % PARA HACER EFICIENTE EL CLASIFICADOR -
        % SOLO LO LLAMAMOS UNA VEZ CON TODOS LOS DATOS. 
        % Recorremos por columna la matriz, y vamos poniendo la
        %información de cada punto ( R G B ) en filas
        input = []; 
        for k=1:M
            input_temp = [R(:,k), G(:,k), B(:,k)];
            input = [input ; input_temp];
        end

        % Aplicamos los clasificadores
        KNNrgbRojo_vector = predict(knn_RGB , input(:,1:3));
        KNNrgbVerde_vector = predict(knn_RGB_verdefresa, input(:,1:3));
        KNNrgbRojoVerde_vector = predict(knn_RGB_rojoyverde,input(:,1:3));

        ind =1;
        for m=1:M
            KNNrgb_rojo(:,m) = KNNrgbRojo_vector(ind:ind+N-1);
            KNNrgb_verde(:,m) = KNNrgbVerde_vector(ind:ind+N-1);
            KNNrgb_1Clasificador(:,m) = KNNrgbRojoVerde_vector(ind:ind+N-1);
            ind = ind+N;
        end

        %% Limpiamos las imágenes
        Img_Binaria_Rojo = bwareaopen(KNNrgb_rojo,round( agrupacion_minima_normalizada * M * N));
        Img_Binaria_Verde = bwareaopen(KNNrgb_verde,round( agrupacion_minima_normalizada * M * N));
             % Etiqueto las agrupaciones que contengan pixeles rojos o verdes.
            [Ietiq,numAgrupaciones] = bwlabel(Img_Binaria_Rojo | Img_Binaria_Verde);
            Iacum_2clasificadores = zeros(size(Ietiq));
            % Para cada agrupación compruebo que tenga píxeles rojos.
            for agrupacion=1:numAgrupaciones
                Ibaux = (Ietiq == agrupacion) .* Img_Binaria_Rojo;
                pixeles_rojos = sum(sum(Ibaux));
                if ( pixeles_rojos ~= 0 ) % Si la suma es distinta de 0, es una fresa
                    Iacum_2clasificadores = Iacum_2clasificadores + (Ietiq == agrupacion); %% Iacum es una imagen binaria con todas las fresas.
                end
            end
            % Le ponemos la codificacion a los píxeles
    Iacum_2clasificadores = (Iacum_2clasificadores == Img_Binaria_Rojo & Iacum_2clasificadores ~= 0)*255 + ...
    (Iacum_2clasificadores == Img_Binaria_Verde & Iacum_2clasificadores ~= 0)*128;

            %% Limpiamos la imagen
      Img_Binaria_RojoVerde = bwareaopen(KNNrgb_1Clasificador,round( agrupacion_minima_normalizada * M * N));
      % Etiqueto las agrupaciones que contengan pixeles rojos o verdes.
    [Ietiq,numAgrupaciones] = bwlabel(Img_Binaria_RojoVerde);
    % Saco las imagenes que componen los pixeles rojos y los verdes
        Img_roja = KNNrgb_1Clasificador == 255;
        Img_roja = bwareaopen(Img_roja,round( agrupacion_minima_normalizada * M * N));

        Img_verde = KNNrgb_1Clasificador == 128;
        Img_verde = bwareaopen(Img_verde,round( agrupacion_minima_normalizada * M * N));
     
         Iacum_1clasificador = zeros(size(Ietiq));
    % Para cada agrupación compruebo que tenga píxeles rojos.
    for agrupacion=1:numAgrupaciones
        Ibaux = (Ietiq == agrupacion) .* Img_roja;
        pixeles_rojos = sum(sum(Ibaux));
        if ( pixeles_rojos ~= 0 ) % Si la suma es distinta de 0, es una fresa
            Iacum_1clasificador = Iacum_1clasificador + (Ietiq == agrupacion);
        end
    end
    % Le ponemos la codificación
        Iacum_1clasificador = (Iacum_1clasificador == Img_roja & Iacum_1clasificador ~= 0)*255 + ...
    (Iacum_1clasificador == Img_verde & Iacum_1clasificador ~= 0)*128;

    % Volvemos a reescalar al tamaño normal de la imagen
    I_original = round(imresize(I_reducida,size(I_color,1:2),'nearest'));
    KNNrgb_2Clasificadores = round(imresize(Iacum_2clasificadores,size(I_color,1:2),'nearest'));

    KNNrgb_1Clasificador = round(imresize(Iacum_1clasificador,size(I_color,1:2),'nearest'));
   
    imagenes = cat(3,KNNrgb_2Clasificadores,KNNrgb_1Clasificador);
    %% Extracción de Métricas
        % Elegimos la imagen gold

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(KNNrgb_2Clasificadores,I_gold);
    metricas_knnrgb_2clasificadores = [metricas_knnrgb_2clasificadores;Sens, Esp, Prec, FalsosPositivos];

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(KNNrgb_1Clasificador,I_gold);
    metricas_knnrgb_1clasificador = [metricas_knnrgb_1clasificador;Sens, Esp, Prec, FalsosPositivos];

    metricas = [metricas_knnrgb_2clasificadores;metricas_knnrgb_1clasificador];

    %% Visualización de la imagen detectada
    

    for j=1:2
         funcion_visualizaColores(I_original,imagenes(:,:,j),true);
         if (j == 1) % Imagen 2 Clasificadores
         title(['2Clasificadores_{KNN_{RGB}}: Sens: ', num2str(metricas_knnrgb_2clasificadores(i,1)), ...
             '  //  Esp:', num2str(metricas_knnrgb_2clasificadores(i,2)),'  //  Prec:', num2str(metricas_knnrgb_2clasificadores(i,3)), ...
             '  //  F+:', num2str(metricas_knnrgb_2clasificadores(i,4))])
         elseif (j == 2) % Imagen 1 Clasificador
          title(['1Clasificador_{KNN_{RGB}}: Sens: ', num2str(metricas_knnrgb_1clasificador(i,1)), ...
             '  //  Esp:', num2str(metricas_knnrgb_1clasificador(i,2)),'  //  Prec:', num2str(metricas_knnrgb_1clasificador(i,3)), ...
             '  //  F+:', num2str(metricas_knnrgb_1clasificador(i,4))])
         end

    end

end

%% Calculo la media de cada clasificador
metricas = [mean(metricas_knnrgb_2clasificadores);mean(metricas_knnrgb_1clasificador)];

%% Construyo la tabla de métricas
% Métrica: Sensibilidad
tabla = [metricas(1,:); metricas(2,:)];
Tabla_Sensibilidad = array2table(tabla,"VariableNames", ...
    ["Sensibilidad","Especificidad","Precisión","Falsos Positivos"], ...
    "RowNames",["2 Clasificadores","1 Clasificador"])

save('./Variables_Generadas/metricas',"metricas");
 clear
rmpath('../../Material_Imagenes/03_MuestrasFresas/')
rmpath('./Variables_Necesarias/')