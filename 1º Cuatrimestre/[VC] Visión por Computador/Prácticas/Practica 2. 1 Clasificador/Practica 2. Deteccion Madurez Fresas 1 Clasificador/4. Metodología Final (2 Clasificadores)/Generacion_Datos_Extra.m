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
rmpath('../../Material_Imagenes/03_MuestrasFresas/')

