function [brillo,contraste] = brillo_contraste(ImagenBinaria)
h = imhist(ImagenBinaria);
% Brillo:  Nivel de gris medio en una imagen
P = zeros(256,1);

%% --------------------------- PROBABILIDAD ----------------------------- %%
% ---------------------------------------------------------------------- %
% Probabilidad de que un nivel de gris aparezca en la imagen.
% ---------------------------------------------------------------------- %
for g = 1:256
    P(g) = h(g)/sum(h);
end

%% --------------------------- BRILLO ----------------------------- %%
% ---------------------------------------------------------------------- %
% El brillo es el nivel medio de todos los niveles de gris de un
% histograma. 
% 
% Se calcula como el sumatorio del producto de cada nivel de gris por la
% probabilidad que hay de encontrarlo en la imagen.
% ---------------------------------------------------------------------- %

brillo = 0;
for g = 1:256
    brillo = brillo + (g - 1)*P(g);
end

%% --------------------------- CONTRASTE ----------------------------- %%
% ---------------------------------------------------------------------- %
% El contraste se define como la varianza de los niveles de gris que hay en
% una imagen.
% 
% Se calcula como el sumatorio de cada nivel de gris menos el brillo al
% cuadrado multiplicado por la probabilidad que hay de encontrar ese nivel
% de gris en la imagen.
% ---------------------------------------------------------------------- %
suma = 0;
for g=1:256
    suma = suma + (  ((g-1)-brillo)^2 )*P(g); % nivel de gris menos la media por el porcentaje.
end

contraste = sqrt(suma);
end

