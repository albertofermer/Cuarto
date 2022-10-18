function [brillo,contraste] = brillo_contraste(ImagenBinaria)
h = imhist(ImagenBinaria);

% Brillo:  Nivel de gris medio en una imagen
for g=1:255
    brillo = (sum(g*h(g)))/(size(ImagenBinaria,1)*size(ImagenBinaria,2));
end
% Contraste: 
contraste = sqrt(((sum(h) - brillo )^2) / (size(ImagenBinaria,1)*size(ImagenBinaria,2)));
end

