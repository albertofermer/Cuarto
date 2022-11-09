%% Paso 0. Reducción de la Imagen
I = imread('./P1b.jpg');
ROI = roipoly(I);
[fil,col] = find(ROI);
fmin = min(fil); cmin = min(col);
fmax = max(fil); cmax = max(col);
Ibreducida = I(fmin:fmax,cmin:cmax);
imwrite(Ibreducida,"Imagen_Reducida_P1.tiff");