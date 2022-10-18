% Sacamos los datos de Entrenamiento
load('./Variables Necesarias/ValoresColores.mat')
load('./Variables Generadas/variablesGeneradas.mat')

% Sacamos los datos de Entrenamiento
%load('./Variables Necesarias/ValoresColores.mat')
%load('./Variables Generadas/variablesGeneradas.mat')
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

centroide_RGB = datosMahalanobis(1,1:3);
centroide_Lab = datosMahalanobis(2,1:3);
centroide_RSL = datosMahalanobis(3,1:3);

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
        RGB = cat(3,R_i,G_i,Bl_i);
        Lab = cat(3,L_i,A_i,B_i);
        RSL = cat(3,R_i,S_i,L_i);
        for filas = 1:N
            for columnas = 1:M

                % Sacar la componente correspondiente a cada modelo del
                % píxel(fila,columna) y calcular su distancia de
                % mahalanobis al centroide de la nube de puntos.

                % Si la distancia es menor al umbral, entonces
                % MahallRGB(fila,columna) = 255;
                % Si no, se deja como está.

                pixelRGB = [RGB(filas,columnas,1), RGB(filas,columnas,2), RGB(filas,columnas,3)];
                pixelLab = [Lab(filas,columnas,1), Lab(filas,columnas,2), Lab(filas,columnas,3)];
                pixelRSL = [RSL(filas,columnas,1), RSL(filas,columnas,2), RSL(filas,columnas,3)];
                % distancia(j,1)=sqrt((X-centroide_RGB)*pinv(mcov_RGB)*(X-centroide_RGB)');
                distanciaRGB = sqrt((pixelRGB-centroide_RGB)*pinv(mcovRGB)*(pixelRGB-centroide_RGB)');
                distanciaLab = sqrt((pixelLab-centroide_Lab)*pinv(mcovLab)*(pixelLab-centroide_Lab)');
                distanciaRSL = sqrt((pixelRSL-centroide_RSL)*pinv(mcovRSL)*(pixelRSL-centroide_RSL)');
                
                if(distanciaRGB < datosMahalanobis(1,5))
                    Mahalrgb(filas,columnas) = 255;
                end

                if(distanciaLab < datosMahalanobis(2,5))
                    Mahallab(filas,columnas) = 255;
                end

                if(distanciaRSL < datosMahalanobis(3,5))
                    Mahalrsl(filas,columnas) = 255;
                end
            end
        end

    % Volvemos a reescalar al tamaño normal de la imagen
    I_original = round(imresize(I_reducida,size(I_color,1:2),'nearest'));
    Mahalrgb = round(imresize(Mahalrgb,size(I_color,1:2),'nearest'));
    Mahallab = round(imresize(Mahallab,size(I_color,1:2),'nearest'));
    Mahalrsl = round(imresize(Mahalrsl,size(I_color,1:2),'nearest'));
    %MahalHSIUab = round(imresize(MahalHSIUab,size(I_color,1:2),'nearest'));
   
    imagenes = cat(3,Mahalrgb,Mahallab,Mahalrsl);
    %% Extracción de Métricas
        % Elegimos la imagen gold

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(Mahalrgb,I_gold);
    metricas_rgb = [metricas_rgb;Sens, Esp, Prec, FalsosPositivos];

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(Mahallab,I_gold);
    metricas_lab = [metricas_lab;Sens, Esp, Prec, FalsosPositivos]; 

    [Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(Mahalrsl,I_gold);
    metricas_rsl = [metricas_rsl;Sens, Esp, Prec, FalsosPositivos];

    %[Sens, Esp, Prec, FalsosPositivos] = funcion_metricas(MahalHSIUab,I_gold);
    metricas_HSIUab = [metricas_HSIUab; 0, 0, 0, 0];

    metricas = [metricas_rgb;metricas_lab;metricas_rsl; metricas_HSIUab];

    %% Visualización de la imagen detectada

    for j=1:4
            if(j < 4)
             funcion_visualizaColores(I_original,imagenes(:,:,j),true);
            end
             % [Sens, Esp, Prec, FalsosPositivos]
             title(['MAHAL_{', char(espacios_ccas(j)) , '}: Sens: ', num2str(metricas(j*i,1)), '  //  Esp:', num2str(metricas(j*i,2)), ...
                 '  //  Prec:', num2str(metricas(j*i,3)), '  //  F+:', num2str(metricas(j*i,4))])
    end

end

%Hacemos la media de cada clasificador (RGB;RGB;LAB;LAB;RSL;RSL;HSIUab;HSIUab)
metricas_media_Mahalanobis = [];
for i=1:2:size(metricas,1)
    metricas_media_Mahalanobis = [metricas_media_Mahalanobis;mean(metricas(i:i+1,:))];
end
%% Guardar las métricas.
save('Variables Generadas\metricasMahalanobis.mat',"metricas_media_Mahalanobis")