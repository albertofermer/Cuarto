addpath('./Funciones Necesarias/')
%% Paso 0 Reducción de la Imagen
I = imread('./P1b.jpg');
ROI = roipoly(I);
[fil,col] = find(ROI);
fmin = min(fil); cmin = min(col);
fmax = max(fil); cmax = max(col);
Ibreducida = I(fmin:fmax,cmin:cmax);
imwrite(Ibreducida,"Imagen_Reducida_P1.tiff");

%% Operaciones Individuales. Brillo y Contraste
Ibreducida = imread('./Imagen_Reducida_P1.tiff');
ImagenBinaria = Ibreducida(:,:,1);
[brillo,contraste] = brillo_contraste(Ibreducida) ;

% Modificar el brillo
    % Aumentar el brillo
    ImagenBinariaMasBrillo = uint8(double(Ibreducida + 100));
    [brillo, contraste] = brillo_contraste(ImagenBinariaMasBrillo);
    % Disminuir el brillo
    ImagenBinariaMenosBrillo = uint8(Ibreducida - 100);
    [brillo, contraste] = brillo_contraste(ImagenBinariaMenosBrillo);
% Modificar el contraste
    % Aumentar el contraste
    ImagenBinariaMasContraste = uint8(double(Ibreducida * 0.5));
    [brillo, contraste] = brillo_contraste(ImagenBinariaMasContraste);
    % Disminuir el contraste
     ImagenBinariaMenosContraste = uint8(double(Ibreducida * 1.5));
    [brillo, contraste] = brillo_contraste(ImagenBinariaMenosContraste);
