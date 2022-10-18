% En este script se generará el modelo de clasificación de píxeles Verdes
% utilizando un KNN.
% También se obtendrá el número de píxeles mínimos que componen una fresa y
% se normalizará su valor.

%% Primero: Añadimos los path de las imágenes que vamos a utilizar.
addpath('../../Material_Imagenes/03_MuestrasFresas/')

%% Recorremos las imágenes de las fresas con roipoly 
% Para ir sacando los valores mínimos de las fresas en cada imagen.
numImagenes = 3;
pixeles = [];
for i=1:numImagenes
    nombre = ['SegFresas' num2str(i) '.tif'];    
    ROI = roipoly(imread(nombre));
    pixeles_aux = sum(ROI(:));
    [N,M] = size(imread(nombre));
    pixeles_norm = pixeles_aux / (M*N);
    pixeles = [pixeles; pixeles_norm]; % 0.0011
end

agrupacion_minima_normalizada = min(pixeles);

%% Creamos también el modelo KNN para detectar los píxeles verdes de las fresas.
load('./Variables_Necesarias/ValoresColores.mat')
K = 10;
knn_RGB_verdefresa = fitcknn(ValoresColores(:,1:3),(CodifValoresColores==128)*128,"NumNeighbors",K);

save("./Variables_Necesarias/agrupacion_minima_pixeles_normalizada.mat","agrupacion_minima_normalizada");
save("./Variables_Necesarias/modelo_knn_RGB_Verde.mat","knn_RGB_verdefresa");
%

% for i = 1:numImagenes
% 
%     ValoresColores = [];
%     %nombre = ['imagen', num2str(i)];
%     nombre = ['SegFresas' num2str(i) '.tif']; 
%     
% %     if(i< 10)
% %         nombre = ['imagen0',num2str(i)];
% %     end
%     I_color = imread([nombre]);
%     I_reducida = imresize(I_color,0.5);   
%     % Para cada imagen a color, tendremos que sacar las matrices
%     % R,G,B,H,S,I,Y,U,V,L,a y b
% 
%     % MODELO RGB:
%     R_i = I_reducida(:,:,1);
%     G_i = I_reducida(:,:,2);
%     Bl_i = I_reducida(:,:,3);
%     
%         % Normalización:
%         R_i = double(R_i) / 255;
%         G_i = double(G_i) / 255;
%         Bl_i = double(Bl_i) / 255;
% 
%     I_gris = uint8(mean(I_reducida,3));
%     % MODELO HSI:
% 
%     I_hsv = rgb2hsv(I_reducida);
%     H_i = I_hsv(:,:,1);
%     S_i = I_hsv(:,:,2);
% 
%     % El nivel de iluminación se calcula como la media de intensidad de
%     % los canales R, G y B.
% 
%     I_i = uint8(mean(I_reducida,3)); % (R + G + B) / 3
% 
%         % Normalización:
% 
%         I_i = double(I_i)/255;
% 
%     % MODELO YUV:
%     
%         % Las componentes deben implementarse a partir de las
%         % transformaciones facilitadas en el apartado 2.2 del Tema 2.
% 
% 
%      Y = double(R_i) * 0.299 + 0.587*double(G_i) + 0.114*double(Bl_i); 
%      U = 0.492 * (double(Bl_i) - Y); 
%      V = 0.877 * (double(R_i) - Y);
% 
%      U = mat2gray(U,[-0.6,0.6]);
%      V = mat2gray(V,[-0.6,0.6]);
% 
%     % imshow(cat(3,Y,U,V));
% 
%     % MODELO Lab:
% 
%     I_lab = rgb2lab(I_reducida);
%     L_i = I_lab(:,:,1);
%     A_i = I_lab(:,:,2);
%     B_i = I_lab(:,:,3);
% 
%         % Normalización:
%         L_i = L_i/100;
%         A_i = mat2gray(A_i,[-128,127]);
%         B_i = mat2gray(B_i,[-128,127]);
% 
%          
%         %% Aplicamos el modelo
%         [N, M] = size(R_i); % Sacamos el tamaño de la imagen
%         KNNrgb = zeros(N,M); % Inicializamos matriz de resultados
%         KNNlab = zeros(N,M); % Inicializamos matriz de resultados
%         KNNrsl = zeros(N,M); % Inicializamos matriz de resultados
%         KNNHSIUab = zeros(N,M); % Inicializamos matriz de resultados
%                     
%          
%         % PARA HACER EFICIENTE EL CLASIFICADOR -
%         % SOLO LO LLAMAMOS UNA VEZ CON TODOS LOS DATOS. 
%         % Recorremos por columna la matriz, y vamos poniendo la
%         %información de cada punto ( R G B ) en filas
%         input = []; 
%         for k=1:M
%             input_temp = [R_i(:,k), G_i(:,k), Bl_i(:,k), ...
%                           H_i(:,k), S_i(:,k), I_i(:,k), ...
%                           Y(:,k), U(:,k), V(:,k),...
%                           L_i(:,k),A_i(:,k), B_i(:,k)];
% 
%             input = [input ; input_temp];
%         end
% 
%         % Aplicamos los clasificadores
%         KNNrgb_vector = predict(knn_RGB , input(:,1:3));
% 
%         ind =1;
%         for m=1:M
%             KNNrgb(:,m) = KNNrgb_vector(ind:ind+N-1);
%             ind = ind+N;
%         end
% 
% 
%     % Volvemos a reescalar al tamaño normal de la imagen
%     I_original = round(imresize(I_reducida,size(I_color,1:2),'nearest'));
%     KNNrgb = round(imresize(KNNrgb,size(I_color,1:2),'nearest'));
% end
% 
% funcion_visualizaColores(I_original,KNNrgb ,true);


