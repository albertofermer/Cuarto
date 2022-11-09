function [G] = funcion_genera_ruido_gaussiano(I,media,desv)

[N,M] = size(I);
G = double(I) + double(media+desv*randn(N,M)); % G = I + error
G = uint8(G);

end

