% Sacamos los datos de Entrenamiento
load('./Variables Necesarias/ValoresColores.mat')
load('./Variables Generadas/variablesGeneradas.mat')

% Aplicamos la función de knn para crear un modelo

K = 5; % Utilizaremos los K vecinos más cercanos.

% Sacamos los datos de Entrenamiento
load('./Variables Necesarias/ValoresColores.mat')
load('./Variables Generadas/variablesGeneradas.mat')
load('./Clasificador_Mahalanobis/datosMahalanobis_RGB_Lab_RSL.mat')

addpath('../2. Seleccion de Características/Funciones Necesarias/')


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
    R_i = I_reducida(:,:,1);
    G_i = I_reducida(:,:,2);
    Bl_i = I_reducida(:,:,3);
    
        % Normalización:
        R_i = double(R_i) / 255;
        G_i = double(G_i) / 255;
        Bl_i = double(Bl_i) / 255;

        RGB = cat(3,R_i,G_i,Bl_i);
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


     Y = double(R_i) * 0.299 + 0.587*double(G_i) + 0.114*double(Bl_i); 
     U = 0.492 * (double(Bl_i) - Y); 
     V = 0.877 * (double(R_i) - Y);

     U = mat2gray(U,[-0.6,0.6]);
     V = mat2gray(V,[-0.6,0.6]);

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
        Mahalrgb = zeros(N,M); % Inicializamos matriz de resultados
        Mahallab = zeros(N,M); % Inicializamos matriz de resultados
        Mahalrsl = zeros(N,M); % Inicializamos matriz de resultados
        %MahalHSIUab = zeros(N,M); % Inicializamos matriz de resultados

        % La aplicación del clasificador basado en distancia de Mahalanobis 
        % debe realizarse recorriendo cada píxel de la imagen.

        for filas = 1:N
            for columnas = 1:M

            

            end
        end

    % Volvemos a reescalar al tamaño normal de la imagen
    I_original = round(imresize(I_reducida,size(I_color,1:2),'nearest'));
    Mahalrgb = round(imresize(Mahalrgb,size(I_color,1:2),'nearest'));
    Mahallab = round(imresize(Mahallab,size(I_color,1:2),'nearest'));
    Mahalrsl = round(imresize(Mahalrsl,size(I_color,1:2),'nearest'));
    %MahalHSIUab = round(imresize(MahalHSIUab,size(I_color,1:2),'nearest'));
   
    imagenes = cat(3,Mahalrgb,Mahallab,Mahalrsl,MahalHSIUab);
    %% Extracción de Métricas
        % Elegimos la imagen gold

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(Mahalrgb,I_gold);
    metricas_rgb = [metricas_rgb;Sens, Esp, Prec, FalsosPositivos];

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(Mahallab,I_gold);
    metricas_lab = [metricas_lab;Sens, Esp, Prec, FalsosPositivos]; 

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(Mahalrsl,I_gold);
    metricas_rsl = [metricas_rsl;Sens, Esp, Prec, FalsosPositivos];

    %[Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(MahalHSIUab,I_gold);
    %metricas_HSIUab = [metricas_HSIUab;Sens, Esp, Prec, FalsosPositivos];

    metricas = [metricas_rgb;metricas_lab;metricas_rsl; 0 0 0 0];

    %% Visualización de la imagen detectada
    
    for j=1:4
         funcion_visualizaColores(I_original,imagenes(:,:,j),true);
%         % [Sens, Esp, Prec, FalsosPositivos]
         title(['MAHAL_{', char(espacios_ccas(j)) , '}: Sens: ', num2str(metricas(j*i,1)), '  //  Esp:', num2str(metricas(j*i,2)), ...
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
clear all