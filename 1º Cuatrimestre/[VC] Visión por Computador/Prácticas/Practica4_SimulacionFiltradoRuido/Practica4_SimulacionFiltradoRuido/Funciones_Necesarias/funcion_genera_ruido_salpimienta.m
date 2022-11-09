function [A] = funcion_genera_ruido_salpimienta(I,p,q)

A = (rand(size(I)));

A(A<p) = I(A<p);
A(A<q & A>=p) = 0;
A(A>q & A<1) = 255;

A = uint8(A);
end

