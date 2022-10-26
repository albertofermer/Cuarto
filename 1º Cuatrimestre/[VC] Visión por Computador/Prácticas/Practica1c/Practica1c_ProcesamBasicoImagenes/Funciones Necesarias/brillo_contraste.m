function [brillo,contraste] = brillo_contraste(ImagenBinaria)
h = imhist(ImagenBinaria);
brillo = 0;
contraste = 0;
[N,M] = size(ImagenBinaria(:,:,1));
% Brillo:  Nivel de gris medio en una imagen
brillo = sum(ImagenBinaria(:))/(N*M);

% Contraste: 
contraste = sqrt(sum(( ImagenBinaria(:) - brillo ).^2)/(N*M) );
end

