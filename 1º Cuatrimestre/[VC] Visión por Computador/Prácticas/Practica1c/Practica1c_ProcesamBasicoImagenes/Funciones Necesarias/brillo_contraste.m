function [brillo,contraste] = brillo_contraste(ImagenBinaria)
h = imhist(ImagenBinaria);
% Brillo:  Nivel de gris medio en una imagen
P = zeros(256,1);

for g = 1:256
    P(g) = h(g)/sum(h);
end

brillo = 0;
for g = 1:256
    brillo = brillo + (g - 1)*P(g);
end

suma = 0;
for g=1:256
    suma = suma + (  ((g-1)-brillo)^2 )*P(g); % nivel de gris menos la media por el porcentaje.
end

contraste = sqrt(suma);
end

