addpath('./Funciones Necesarias/')

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
    figure,
    subplot(3,2,1), imshow(ImagenGrisMasBrillo), title(['brillo: ', num2str(mas_brillo)])
    subplot(3,2,2), imhist(ImagenGrisMasBrillo), title('Histograma más brillo')
    subplot(3,2,3), imshow(ImagenGris),title(['brillo', num2str(original_brillo)])
    subplot(3,2,4), imhist(ImagenGris), title('Histograma original')
    subplot(3,2,5), imshow(ImagenGrisMenosBrillo), title(['brillo: ', num2str(menos_brillo)])
    subplot(3,2,6), imhist(ImagenGrisMenosBrillo), title('Histograma menos brillo')
    
    %
    %   Podemos observar como el histograma se ha desplazado a la derecha
    %   (más brillo) saturando la imagen y haciendo que se vea más
    %   brillante.

    %   Al restarle un valor, desplazamos el hsitograma hacia la izquierda,
    %   es decir, hacia niveles de gris más bajos. Esto provoca que el
    %   brillo disminuya haciendo que la imagen se vea más oscura.
    %

% Modificar el contraste (Varianza)
    
    ImagenGris = double(ImagenGris);
    pmax = max(ImagenGris(:)); % 206
    pmin = min(ImagenGris(:)); % 78
    
    % Aumentar contraste
    ImagenGrisMasContraste = uint8 ((0 +  (255-0)/(pmax-pmin) )*uint8( (ImagenGris - double(pmin) )));

    % Disminuir contraste
    ImagenGris = double(ImagenGris);
    pmin = min(ImagenGris(:));
    pmax= max(ImagenGris(:));
    qmin = pmin+(pmax-pmin)/3; 
    qmax = pmax-(pmax-pmin)/3;
    
    ImagenGrisMenosContraste = uint8( qmin +  (((qmax-qmin) / (pmax-pmin)) * (ImagenGris - pmin)));

    [~,mas_contraste] = brillo_contraste(ImagenGrisMasContraste);
    [~,menos_contraste] = brillo_contraste(ImagenGrisMenosContraste);
    figure
    subplot(3,2,1), imshow(ImagenGrisMasContraste), title(['contraste: ', num2str(mas_contraste)])
    subplot(3,2,2), imhist(ImagenGrisMasContraste), title('histograma más contraste')
    subplot(3,2,3), imshow(uint8(ImagenGris)),title(['contraste: ', num2str(original_contraste)])
    subplot(3,2,4), imhist(uint8(ImagenGris)), title('histograma original')
    subplot(3,2,5), imshow(ImagenGrisMenosContraste), title(['contraste: ', num2str(menos_contraste)])
    subplot(3,2,6), imhist(ImagenGrisMenosContraste), title('histograma menos contraste')

    %
    % Podemos observar como el hsitograma de la imagen con más contraste se
    % amplía, permitiendo diferenciar mejor los distintos niveles de gris.
    % En la imagen donde hay menos contraste vemos que los valores de gris
    % están muy concentrados en una misma zona del histograma, lo que
    % provoca que no podamos ver demasiado bien la imagen
    %

 %% Parte 2. Operaciones de Vecindad

HP = ones(5,5)/25;
HP2 = ones(9,9)/81;
HL = [-1,-1,-1;-1,8,-1;-1,-1,-1];
ImagenGris = uint8(ImagenGris);
figure,
subplot(1,3,1), imshow(imfilter(ImagenGris,HP)), title('Convolución 5x5 (HP)')
subplot(1,3,2), imshow(imfilter(ImagenGris,HL)), title('Detección de bordes (HL)')
subplot(1,3,3), imshow(imfilter(ImagenGris,HP2)), title('Convolución 9x9 (HP2)')

figure,
subplot(1,3,1), imshow(funcion_imfilter(ImagenGris,HP,'zeros')), title('funcion\_imfilter: Convolución 5x5 (HP)')
subplot(1,3,2), imshow(funcion_imfilter(ImagenGris,HL,'zeros')), title('funcion\_imfilter: Detección de bordes (HL)')
subplot(1,3,3), imshow(funcion_imfilter(ImagenGris,HP2,'zeros')), title('funcion\_imfilter: Convolución 9x9 (HP2)')

funcion_compara_matrices(imfilter(ImagenGris,HP), funcion_imfilter(ImagenGris,HP,'zeros'))
funcion_compara_matrices(imfilter(ImagenGris,HL), funcion_imfilter(ImagenGris,HL,'zeros'))

I1 = imfilter(ImagenGris,HP,'replicate');
I2 = funcion_imfilter(ImagenGris,HP,'replicate');

funcion_compara_matrices(imfilter(ImagenGris,HP,'symmetric'), funcion_imfilter(ImagenGris,HP,'symmetric'))
funcion_compara_matrices(imfilter(ImagenGris,HL,'symmetric'), funcion_imfilter(ImagenGris,HL,'symmetric'))

I1 = imfilter(ImagenGris,HP,'symmetric');
I2 = funcion_imfilter(ImagenGris,HP,'symmetric');

funcion_compara_matrices(imfilter(ImagenGris,HP,'replicate'), funcion_imfilter(ImagenGris,HP,'replicate'))
funcion_compara_matrices(imfilter(ImagenGris,HL,'replicate'), funcion_imfilter(ImagenGris,HL,'replicate'))


%   En la primera máscara podemos observar como la imagen se difumina, esto
%   es debido a que se está aplicando un filtro de convolución que promedia
%   el entorno de vecindad de cada píxel haciendo que se se pierda parte de
%   la transición de los bordes que hay en la imagen.
%
%   En la segunda máscara podemos observar como se resaltan los bordes de
%   la imagen. Esta máscara es sensible a la variación del color en la
%   imagen, por lo que hace que resalten más los bordes.
%
%   En la tercera mácara vemos como la imagen se difumina más aún que la
%   primera, esto es porque el entorno de vecindad que utiliza es mayor que
%   en la primera. Esta máscara es más agresiva ante el ruido y produce un
%   mayor difuminado que la primera.



   