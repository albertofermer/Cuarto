function [datosMahalanobis,mcov_RGB] = funcion_Mahalanobis(RGB_Rojo,RGB_NoRojo,flagRepresenta)

datosMahalanobis = calcula_datos_distancias(RGB_Rojo,RGB_NoRojo);
% Calculamos el centroide de la nube de puntos
centroide_RGB = datosMahalanobis(1:3);
% Calculamos la matriz de covarianza de la nube de puntos.
mcov_RGB = cov(RGB_Rojo);

% Creamos la meshgrid para representar la superficie.
[X1,X2,X3] = meshgrid(0:1/40:1); 
Valores = [X1(:) X2(:) X3(:)]; 

[NumValores temp] = size(Valores); 
distancia = [];
% Calculamos la distancia de mahalanobis para cada punto de los datos.
for j=1:NumValores
    X = Valores(j,:); 
    distancia(j,1)=sqrt((X-centroide_RGB)*pinv(mcov_RGB)*(X-centroide_RGB)');
end

if (flagRepresenta)
    titulos = ["Umbral 1. Mayor numero posible de pixeles de color del objeto.";
        "Umbral 2. Excluir el 3% de los puntos rojos de la fresa";
        "Umbral 3. No detectar ruido de fondo";
        "Umbral 4. Obtener un 0.05% de puntos de otro color "];
    % Ver los diferentes umbrales
        for i=4:7
            PosicionesInteres = distancia < datosMahalanobis(i); 
            datosMahal = Valores(PosicionesInteres,:);
            
            % REPRESENTAMOS
            x = datosMahal(:,1); 
            y = datosMahal(:,2); 
            z = datosMahal(:,3);
            figure, plot3(x, y, z, '+g'), hold on, grid on
            
            R = RGB_Rojo(:,1);
            G = RGB_Rojo(:,2);
            B = RGB_Rojo(:,3);
            
            plot3(R,G,B,'.r'), hold on
            plot3(RGB_NoRojo(:,1),RGB_NoRojo(:,2),RGB_NoRojo(:,3),'.b'), hold on
            title(titulos(i-3))
        end

end
end