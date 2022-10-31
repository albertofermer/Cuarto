function [Ieq] = funcion_ecualizaZonal(Imagen)
% Dividir la imagen en 9 subimagenes de las mismas dimensiones y aplicar
% ecualiza_imagen en cada una de esas subimagenes.

[M,N] = size(Imagen);   % Sacamos las dimensiones de la imagen que queremos ecualizar
Ieq = zeros(M,N);       % Inicializamos la imagen resultado


filas = [floor(M/3),floor(M/3)*2,M];    % Calculamos los cortes de las filas
columnas = [floor(N/3),floor(N/3)*2,N]; % Calculamos los cortes de las columnas

inicio_columnas = 1; % Iniciamos el contador de las columnas a 1

for j = 1:3
    inicio_filas = 1; % reiniciamos el contador de las filas a 1
    for i = 1:3
        sub_img = Imagen(inicio_filas:filas(i),inicio_columnas:columnas(j));    % Recortamos la imagen original desde el inicio hasta la marca (i,j)
        sub_img_eq = funcion_ecualizaImagen(sub_img,3);                         % Ecualizamos la sub imagen
        Ieq(inicio_filas:filas(i),inicio_columnas:columnas(j)) = sub_img_eq;    % Incrustamos la sub imagen en la imagen resultado 
        inicio_filas = 1 + filas(i);                                            % Actualizamos el inicio de las filas para la siguiente sub imagen
    end
    inicio_columnas = 1 + columnas(j);                                          % Actualizamos el inicio de las columnas para la siguiente sub imagen
end

Ieq = uint8(Ieq);   % Convertimos la imagen a uint8 para poder representarla.

end

