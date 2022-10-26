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
ImagenGris = Ibreducida(:,:,1);
[original_brillo,original_contraste] = brillo_contraste(Ibreducida) ;

% Modificar el brillo (Nivel de gris medio)

    % Aumentar el brillo
    ImagenGrisMasBrillo = uint8(Ibreducida + 100);
    [mas_brillo, ~] = brillo_contraste(ImagenGrisMasBrillo);
    % Disminuir el brillo
    ImagenGrisMenosBrillo = uint8(Ibreducida - 100);
    [menos_brillo, ~] = brillo_contraste(ImagenGrisMenosBrillo);
    imshow([ImagenGrisMasBrillo, ImagenGris, ImagenGrisMenosBrillo])
% Modificar el contraste (Varianza)
    
    pmax = max(ImagenGris(:)); % 206
    pmin = min(ImagenGris(:)); % 78
    qmax = pmax-(pmax-pmin)/3;
    qmin = pmin+(pmax-pmin)/3;
    ImagenGris = double(ImagenGris);
    % Aumentar contraste
    ImagenGrisMasContraste = uint8 ((0 +  (255-0)/(pmax-pmin) )*(ImagenGris - pmin ));
    imhist(ImagenGrisMasContraste)
    % Disminuir contraste
    ImagenGrisMenosContraste = uint8( qmin +  (((qmax-qmin) / (pmax-pmin)) * (ImagenGris - pmin)));
    figure, imhist(ImagenGrisMenosContraste), figure
    imshow([ImagenGrisMasContraste, ImagenGris, ImagenGrisMenosContraste])

 %% Parte 2. Operaciones de Vecindad
 % help imfilter
HP = ones(5,5)/25;
HP2 = ones(9,9)/81;
HL = [-1,-1,-1;-1,8,-1;-1,-1,-1];

imshow([imfilter(ImagenGris,HP), imfilter(ImagenGris,HL), imfilter(ImagenGris,HP2)])
% HP2 difumina más que HP.



   