function [A] = funcion_genera_ruido_salpimienta(I,p,q)
%-------------------------------------------------------------------------%
% El ruido tipo "sal y pimienta" se caracteriza porque el píxel toma un
% valor máximo o mínimo. En imágenes de 256 niveles de gris el máximo suele
% ser 255 y el mínimo 0.
% Se utilizan dos parámetros (p y q) para modificar la imagen siendo:
%
% q-p  :  porcentaje de píxeles con ruido tipo pimienta.
% 1-q  :  porcentaje de píxeles con ruido tipo sal.
%
%-------------------------------------------------------------------------%

A = (rand(size(I)));

A(A<p) = I(A<p);
A(A<q & A>=p) = 0;
A(A>=q & A<1) = 255;

A = uint8(A);
end

