function [Ifiltrada] = funcion_filtroMediana(I,matriz_vecindad,opcion_padding)
%-------------------------------------------------------------------------%
% Es un filtro de orden no lineal. Ordena los píxeles de la ventana de cada
% píxel de la imagen en orden secuencial basándose en el nivel de gris.
%
% Ventajas: Preserva mejor los bordes que los filtros lineales y es eficaz
% para eliminar ruido tipo "sal y pimienta" ya que los valores ruidosos
% estarán situados al principio o al final del vector ordenado.
%
% Desventajas: Sin embargo, puede perder detalles como líneas delgadas, puntos aislados
% y tiende a redondear las esquinas. Además tiene un coste computacional
% alto.
%
% Reemplaza el píxel central de la ventana por la mediana del entorno de
% vecindad del píxel (el valor central).
%
% El resultado es que se homogenizan los píxeles con intensidad diferente a
% la de sus vecinos, de esta forma se eliminan los picos de intensidad
% aislados.
%-------------------------------------------------------------------------%

% opcion = 'zeros', 'replicate', 'symmetric'
[N, M] = size(I);
[NH, MH] = size(matriz_vecindad);
EF = floor(NH/2);
EC = floor(MH/2);

if(~strcmp('zeros',opcion_padding) && ~strcmp('replicate',opcion_padding) && ~strcmp('symmetric',opcion_padding))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',opcion_padding))
    % Amplía la imagen con ceros
    Iamp = zeros(2*EF+N,2*EC+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EF:(N+EF), 1+EC:(M+EC)) = I;
    Ifiltrada = zeros(size(I));

elseif (strcmp('replicate',opcion_padding))
    Iamp = padarray(I,[EF,EC],'replicate');

elseif (strcmp('symmetric',opcion_padding))
    Iamp = padarray(I,[EF,EC],'symmetric');
    
end

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)
    ROI = Iamp(k-EF:k+EF, j-EC:j+EC);
    Ifiltrada(k-EF, j-EC) = median(ROI(logical(matriz_vecindad)));
    end
end

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(I))
    Ifiltrada = uint8(Ifiltrada);
else
    Ifiltrada = double(Ifiltrada);
end

end

