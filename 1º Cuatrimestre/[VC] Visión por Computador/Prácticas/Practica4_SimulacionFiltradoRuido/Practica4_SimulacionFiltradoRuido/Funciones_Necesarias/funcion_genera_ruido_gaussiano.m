function [G] = funcion_genera_ruido_gaussiano(I,media,desv)
%-------------------------------------------------------------------------%
% El ruido gaussiano produce pequeñas variaciones en los niveles de gris
% originales de una imagen.
% El valor final del píxel será el valor original más una cantidad
% correspondiente al error que puede describirse con una variable
% gaussiana.
%
%   G(x,y) = I(x,y) + error
%
% La distribución del ruido (error) viene dada por una gaussiana de media
% y varianza desv^2.
%-------------------------------------------------------------------------%

[N,M] = size(I);
G = double(I) + double(media+desv*randn(N,M)); % G = I + error
G = uint8(G);

end

