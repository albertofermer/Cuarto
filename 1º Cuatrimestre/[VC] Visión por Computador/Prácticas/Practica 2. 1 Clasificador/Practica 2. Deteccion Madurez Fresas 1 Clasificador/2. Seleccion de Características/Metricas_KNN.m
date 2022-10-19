% Sacamos los datos de Entrenamiento
load('./Variables Necesarias/ValoresColores.mat')
load('./Variables Generadas/variablesGeneradas.mat')

% Aplicamos la función de knn para crear un modelo

K = 5; % Utilizaremos los K vecinos más cercanos.

% Sacamos los datos de Entrenamiento
load('./Variables Necesarias/ValoresColores.mat')
load('./Variables Generadas/variablesGeneradas.mat')


addpath('../2. Seleccion de Características/Funciones Necesarias/')

% Aplicamos la función de matlab para entrenar los clasificadores
knn_RGB = fitcknn(RGB,(CodifValoresColores==255)*255,"NumNeighbors",K);
knn_Lab = fitcknn(Lab,(CodifValoresColores==255)*255,"NumNeighbors",K);
knn_RSL = fitcknn(RSL,(CodifValoresColores==255)*255,"NumNeighbors",K);
knn_HSIUab = fitcknn(HSIUab,(CodifValoresColores==255)*255,"NumNeighbors",K);

% Aplicamos el clasificador
    % 1. Sacamos los descriptores de la imagen que queremos clasificar.
    addpath('../../Material_Imagenes\02_MuestrasRojo\');

numImagenes = 2;
metricas_rgb = [];
metricas_lab = [];
metricas_rsl = [];
metricas_HSIUab = [];
espacios_ccas = ["RGB", "Lab", "RSL", "HSIUab"];
% Para cada imagen del directorio...
for i = 1:numImagenes

    ValoresColores = [];
    %nombre = ['imagen', num2str(i)];
    nombre = ['EvRojo', num2str(i)];
    nombre_gold = ['EvRojo', num2str(i), '_Gold'];
%     if(i< 10)
%         nombre = ['imagen0',num2str(i)];
%     end
    I_color = imread([nombre, '.tif']);
    I_gold = imread([nombre_gold, '.tif']);
    I_reducida = imresize(I_color,0.5);   
    % Para cada imagen a color, tendremos que sacar las matrices
    % R,G,B,H,S,I,Y,U,V,L,a y b

    % MODELO RGB:
    R_i = double(I_reducida(:,:,1))/255;
    G_i = double(I_reducida(:,:,2))/255;
    Bl_i = double(I_reducida(:,:,3))/255;

    I_gris = uint8(mean(I_reducida,3));
    % MODELO HSI:

    I_hsv = rgb2hsv(I_reducida);
    H_i = I_hsv(:,:,1);
    S_i = I_hsv(:,:,2);

    % El nivel de iluminación se calcula como la media de intensidad de
    % los canales R, G y B.

    I_i = uint8(mean(I_reducida,3)); % (R + G + B) / 3

        % Normalización:

        I_i = double(I_i)/255;

    % MODELO YUV:
    
        % Las componentes deben implementarse a partir de las
        % transformaciones facilitadas en el apartado 2.2 del Tema 2.


     Y = double(R_i) * 0.299 + 0.587*double(G_i) + 0.114*double(Bl_i); 
     U = 0.492 * (double(Bl_i) - Y); 
     V = 0.877 * (double(R_i) - Y);

     U = mat2gray(U,[-0.6,0.6]);
     V = mat2gray(V,[-0.6,0.6]);

    % imshow(cat(3,Y,U,V));

    % MODELO Lab:

    I_lab = rgb2lab(I_reducida);
    L_i = I_lab(:,:,1);
    A_i = I_lab(:,:,2);
    B_i = I_lab(:,:,3);

        % Normalización:
        L_i = L_i/100;
        A_i = mat2gray(A_i,[-128,127]);
        B_i = mat2gray(B_i,[-128,127]);

         
        %% Aplicamos el modelo
        [N, M] = size(R_i); % Sacamos el tamaño de la imagen
        KNNrgb = zeros(N,M); % Inicializamos matriz de resultados
        KNNlab = zeros(N,M); % Inicializamos matriz de resultados
        KNNrsl = zeros(N,M); % Inicializamos matriz de resultados
        KNNHSIUab = zeros(N,M); % Inicializamos matriz de resultados
                    
         
        % PARA HACER EFICIENTE EL CLASIFICADOR -
        % SOLO LO LLAMAMOS UNA VEZ CON TODOS LOS DATOS. 
        % Recorremos por columna la matriz, y vamos poniendo la
        %información de cada punto ( R G B ) en filas
        input = []; 
        for k=1:M
            input_temp = [R_i(:,k), G_i(:,k), Bl_i(:,k), ...
                          H_i(:,k), S_i(:,k), I_i(:,k), ...
                          Y(:,k), U(:,k), V(:,k),...
                          L_i(:,k),A_i(:,k), B_i(:,k)];

            input = [input ; input_temp];
        end

        % Aplicamos los clasificadores
        KNNrgb_vector = predict(knn_RGB , input(:,1:3));
        KNNlab_vector = predict(knn_Lab, input(:,10:12));
        KNNrsl_vector = predict(knn_RSL,input(:,[1,5,10]));
        KNNHSIUab_vector = predict(knn_HSIUab, input(:,[4 5 6 8 11 12]));
        ind =1;
        for m=1:M
            KNNrgb(:,m) = KNNrgb_vector(ind:ind+N-1);
            KNNlab(:,m) = KNNlab_vector(ind:ind+N-1);
            KNNrsl(:,m) = KNNrsl_vector(ind:ind+N-1);
            KNNHSIUab(:,m) = KNNHSIUab_vector(ind:ind+N-1);
            ind = ind+N;
        end


    % Volvemos a reescalar al tamaño normal de la imagen
    I_original = round(imresize(I_reducida,size(I_color,1:2),'nearest'));
    KNNrgb = round(imresize(KNNrgb,size(I_color,1:2),'nearest'));
    KNNlab = round(imresize(KNNlab,size(I_color,1:2),'nearest'));
    KNNrsl = round(imresize(KNNrsl,size(I_color,1:2),'nearest'));
    KNNHSIUab = round(imresize(KNNHSIUab,size(I_color,1:2),'nearest'));
   
    imagenes = cat(3,KNNrgb,KNNlab,KNNrsl,KNNHSIUab);
    %% Extracción de Métricas
        % Elegimos la imagen gold

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(KNNrgb,I_gold);
    metricas_rgb = [metricas_rgb;Sens, Esp, Prec, FalsosPositivos];

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(KNNlab,I_gold);
    metricas_lab = [metricas_lab;Sens, Esp, Prec, FalsosPositivos]; 

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(KNNrsl,I_gold);
    metricas_rsl = [metricas_rsl;Sens, Esp, Prec, FalsosPositivos];

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(KNNHSIUab,I_gold);
    metricas_HSIUab = [metricas_HSIUab;Sens, Esp, Prec, FalsosPositivos];

    metricas = [metricas_rgb;metricas_lab;metricas_rsl;metricas_HSIUab];

    %% Visualización de la imagen detectada
    
    for j=1:4
         funcion_visualizaColores(I_original,imagenes(:,:,j),true);
%         % [Sens, Esp, Prec, FalsosPositivos]
         title(['KNN_{', char(espacios_ccas(j)) , '}: Sens: ', num2str(metricas(j*i,1)), '  //  Esp:', num2str(metricas(j*i,2)), ...
             '  //  Prec:', num2str(metricas(j*i,3)), '  //  F+:', num2str(metricas(j*i,4))])
    end

end

%Hacemos la media de cada clasificador (RGB;RGB;LAB;LAB;RSL;RSL;HSIUab;HSIUab)
metricas_media_KNN = [];
for i=1:2:size(metricas,1)
    metricas_media_KNN = [metricas_media_KNN;mean(metricas(i:i+1,:))];
end
%% Guardar las métricas.
save('Variables Generadas\metricasKNN.mat',"metricas_media_KNN")
save('./Variables Generadas/modeloKNN+RGB.mat',"knn_RGB")
clear all
rmpath('./Funciones Necesarias/')
rmpath('../../Material_Imagenes\02_MuestrasRojo\');