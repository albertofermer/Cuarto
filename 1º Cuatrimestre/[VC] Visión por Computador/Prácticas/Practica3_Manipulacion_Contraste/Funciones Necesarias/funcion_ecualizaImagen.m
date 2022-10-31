function [Ieq] = funcion_ecualizaImagen(I, criterio)
% Ecualiza de forma uniforme la imagen pasada por parámetro.
% Puede utilizarse con diferentes criterios:
%   1 - Recorrido píxel a píxel de la imagen
%   2 - Realiza el cálculo de la función de transformación por cada nivel
%   de gris
%   3 - Realiza el cálculo de la función de transformción por cada nivel de
%   gris posible y, haciendo un barrido en los 256 niveles de gris,
%   aplicarla para generar la imagen de salida.

Ieq = uint8(zeros(size(I)));
[N, M] = size(I);
H = funcion_HistAcum(imhist(I));

if(criterio == 1)
% Recorrido píxel a píxel de la imagen. Calcula la función de
% transformación para cada píxel.
for i=1:N % Para cada fila
    for j=1:M % Para cada columna
        g = double(I(i,j)); % Saco el nivel de gris del píxel
        g_prima =  256/(M*N) * double(H(g+1))-1; % Le aplico la función de transformación
        F = max(0,round(g_prima)); % Si es menor que 0, lo dejo en 0.
        Ieq(i,j) = F; % Le aplico el nivel de gris a la nueva imagen.
    end
end

elseif (criterio == 2)
% Realiza la función de transformación para cada nivel de gris, antes del
% bucle.
g_prima = (256/(M*N) * double(H)) - 1; % Calculo g' para todos los niveles del histograma acumulado
F = max(0,round(g_prima)); % Calculo la función de transformacion
    for i=1:N % Para cada fila
        for j=1:M % Para cada columna
            Ieq(i,j) = F( double(I(i,j)) + 1 ); % Aplico la función de transformación para el nivel de gris del píxel (i,j)
        end
    end

elseif (criterio == 3)
% Realiza el cálculo de la función de transformación para cada nivel de
% gris posible. Haciendo un barrido de 1 a 256 se aplica a la imagen de
% salida.
g_prima = (256/(M*N) * double(H)) - 1; % Calculo g' para todos los niveles del histograma acumulado
F = max(0,round(g_prima)); % Calculo la función de transformacion
    for g=1:256 % para cada nivel de gris
        Ieq(I == (g-1)) = F(g);  % Actualizo en la nueva imagen donde los píxeles con nivel de gris g-1 en la imagen original, sean F(g)
    end

end

Ieq = uint8(Ieq);


end

