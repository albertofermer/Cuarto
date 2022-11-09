function ImagenNueva = funcion_imfilter(Imagen,H,opcion)

%-------------------------------------------------------------------------%
% La función imfilter realiza una operación lineal sobre el entorno de
% vecindad de cada píxel. Este entorno viene definido por la máscara H
% pasada por parámetro.
%
% Para aplicar la máscara, esta se centra sobre cada píxel de la imagen y
% se genera un nuevo valor de la forma:
%
%       p(x) = w1x1 + w2x2 + ... + wnxn
%
% Siendo n el número de vecinos que se tiene en cuenta y siendo x el píxel
% de la imagen original.
%
% Las opciones de padding se utilizarán cuando la máscara esté centrada en
% uno de los píxeles del borde de la imagen. Dependiendo de qué opción se
% escoja, el resultado podrá variar.
%
%   'zeros':        La imagen se rodea de un marco de píxeles negros.
%   'replicate':    La imagen se replica a cada lado.
%   'symmetric':    La imagen se replica simétricamente a cada lado.
%-------------------------------------------------------------------------%

[N, M] = size(Imagen);
[NH, MH] = size(H);

% EF y EC son el número de filas y de columnas, respectivamente, que se
% añadirán con la opción de padding.

EF = floor(NH/2);
EC = floor(MH/2);

if(~strcmp('zeros',opcion) && ~strcmp('replicate',opcion) && ~strcmp('symmetric',opcion))
    disp('Opcion no válida');
    return;
end

if(strcmp('zeros',opcion))
    % Amplía la imagen con ceros
    Iamp = zeros(NH+N,MH+M);
    Iamp = uint8(Iamp);
    % Incrustamos la imagen en la imagen ampliada.
    Iamp(1+EC:(N+EC), 1+EF:(M+EF)) = Imagen;

else
    Iamp = padarray(Imagen, [EF, EC], opcion);    
end

%-------------------------------------------------------------------------%
% Para aplicar la máscara H a la imagen se recorrerá la imagen ampliada y
% se hará la operación de convolución para cada píxel.
%
% ROI: entorno de vecindad del píxel.
% conv: matriz multiplicada por la máscara.
% ImagenNueva: La imagen con el píxel actualizado.
%-------------------------------------------------------------------------%

for k=(1+EF):(N+EF)
    for j=(1+EC):(M+EC)
    ROI = Iamp(k-EF:k+EF, j-EC:j+EC);
    conv = double(ROI) .* double(H);
    ImagenNueva(k-EF, j-EC) = sum(conv(:));
    end
end

% Convertimos la imagen al mismo tipo de dato que la entrada:
if(isinteger(Imagen))
    ImagenNueva = uint8(ImagenNueva);
else
    ImagenNueva = double(ImagenNueva);
end

end

