% Problema de Clasificación binaria de píxeles.
%   Clase '0' :- Píxel de otro color.
%   Clase '1' :- Píxel de color rojo-fresa.

% Cargamos los datos de la etapa anterior.
    load('./Variables Necesarias/ValoresColores.mat');
    RGB = cat(3,ValoresColores(:,:,1),ValoresColores(:,:,2),ValoresColores(:,:,3));
    HSI = cat(3,ValoresColores(:,:,4),ValoresColores(:,:,5),ValoresColores(:,:,6));
    YUV = cat(3,ValoresColores(:,:,7),ValoresColores(:,:,8),ValoresColores(:,:,9));
    Lab = cat(3,ValoresColores(:,:,10),ValoresColores(:,:,11),ValoresColores(:,:,12));

    % Cuantificar la separabilidad que tienen los espacios de color
    % anteriores mediante CSM ( Class Scattered Matrix ).

    % calcula_varianza_entre_clases ?
    % funcion_calcula_descriptores ?
    %
    % ¿¿ indiceJ ??

