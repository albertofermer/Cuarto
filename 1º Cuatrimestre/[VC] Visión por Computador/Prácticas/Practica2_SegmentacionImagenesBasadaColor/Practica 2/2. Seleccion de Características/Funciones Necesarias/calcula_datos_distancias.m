function datosMahalanobis = calcula_datos_distancias(xColor,xFondo)

    Media_R = mean(xColor(:,1));  Media_G = mean(xColor(:,2)); Media_B = mean(xColor(:,3));
    media = [Media_R, Media_G, Media_B];

    datosMahalanobis = media;
    
    mcov_RGB = cov(xColor);
    centroide_RGB = media;
    % Umbral 1. Mayor numero posible de pixeles de color del objeto.
    distancia = [];
    for i = 1:size(xColor,1)
    distancia(i) = sqrt((xColor(i,:)-centroide_RGB)*pinv(mcov_RGB)*(xColor(i,:)-centroide_RGB)');
    end

    [max_distancia,~] = max(distancia);
    datosMahalanobis = [datosMahalanobis, max_distancia];
    
    % Umbral 2. Excluir el 3% de los puntos rojos de la fresa y elegir el que mayor distancia tenga.

     [distancias_ordenadas,~] = sort(distancia,'descend');
     numero_instancias = size(distancias_ordenadas,2);
     distancias_ordenadas(:,1:round(numero_instancias*0.03)) = [];
     [max_distancia_3porc,~] = max(distancias_ordenadas);
     datosMahalanobis = [datosMahalanobis, max_distancia_3porc];

    % Umbral 3. No detectar ruido de fondo.
    distancia = [];
    for i = 1:size(xFondo,1)
     distancia(i) = sqrt((xFondo(i,:)-centroide_RGB)*pinv(mcov_RGB)*(xFondo(i,:)-centroide_RGB)');
    end
    [minima,~] = min(distancia);
    datosMahalanobis = [datosMahalanobis, minima];


    % Umbral 4. Obtener un 0.05% de puntos de otro color 

    [distancias_ordenadas,~] = sort(distancia);
    numero_instancias = size(distancias_ordenadas,2);
    distancias_ordenadas(:,1:round(numero_instancias*0.0005)) = [];
    [min_distancia_005porc,~] = min(distancias_ordenadas);
    datosMahalanobis = [datosMahalanobis, min_distancia_005porc];

    
end