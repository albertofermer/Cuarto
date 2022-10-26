addpath('./Funciones Necesarias\')


I = imread('P3.tif');
% Expandir la parte baja del histograma. Función a trozos.
pmin = min(I(:));
pmax = max(I(:));
imhist(I);
% Los valores se ecuentran repartidos por todo el histograma, por lo que
% hay que ampliar la parte baja del histograma utilizando una función a
% trozos.

%% Ejercicio 1: Amplitud de Contraste en la parte que nos interesa y contracción en la que no nos interesa
I = imread('P3.tif');
I_original = I;
qmin = 0; qmax = 200;
pmin = 0; pmax = 100;
I(I <= pmax) = uint8( qmin +  (  (  (qmax-qmin) / (pmax-pmin)  ) * (double(I(I <= pmax)) ) - pmin) );

qmin = qmax ; qmax = 255; 
pmin = pmax; pmax = 255;
I(I > pmin) = uint8( qmin +    (  (  (qmax-qmin) / (pmax-pmin)  ) * (double(I(I > pmin))  ) - pmin)  );
I = uint8(I);

[brillo_original, contraste_original] = brillo_contraste(uint8(I_original));
[brillo_amplitud, contraste_amplitud] = brillo_contraste(uint8(I));

figure,
subplot(2,2,1), imshow(I_original), title(['Imagen Original ', 'brillo: ', ...
    num2str(brillo_original), ' contraste: ', num2str(contraste_original)])

subplot(2,2,2), imhist(uint8(I_original)), title('Histograma Original')
subplot(2,2,3), imshow(I), title(['Imagen Amplitud Contraste ', 'brillo: ', ...
    num2str(brillo_amplitud), ' contraste: ', num2str(contraste_amplitud)])
subplot(2,2,4), imhist(uint8(I)), title('Histograma Amplitud de Contraste')

clear all
clc

%% Ejercicio 2: Funciones Cuadradas y Cúbicas

I_original = imread('./P3.tif');
I_cuadrada = (double(I_original).^2)/255;
I_cubica = (double(I_original).^3)/255^2;

[brillo_original, contraste_original] = brillo_contraste(I_original);
[brillo_cuadrada, contraste_cuadrada] = brillo_contraste(uint8(I_cuadrada));
[brillo_cubica, contraste_cubica] = brillo_contraste(uint8(I_cubica));

figure,
subplot(3,2,1), imshow(I_original), title(['Imagen Original','brillo', num2str(brillo_original), ...
    ' contraste: ', num2str(contraste_original)])

subplot(3,2,2), imhist(uint8(I_original)), title('Histograma Original')

subplot(3,2,3), imshow(uint8(I_cuadrada)), title(['Imagen Cuadrada', ...
    'brillo: ', num2str(brillo_cuadrada), ' contraste:', num2str(contraste_cuadrada)])

subplot(3,2,4), imhist(uint8(I_cuadrada)), title('Histograma Cuadrada')

subplot(3,2,5), imshow(uint8(I_cubica)), title(['Imagen Cúbica', ...
    'brillo: ', num2str(brillo_cubica), ' contraste:', num2str(contraste_cubica)])

subplot(3,2,6), imhist(uint8(I_cubica)), title('Histograma Cúbica')

% La imagen tiende a oscurecerse, los píxeles oscuros de la imagen
% disminuyen su contraste.

clear all
clc
%% Funciones de Raíz Cuadrada / Cúbica
I_original = imread('./P3.tif');
I_rtcuadrada = sqrt(double(I_original)*255);
I_rtcubica = nthroot(double(I_original)*(255^2),3);

[brillo_original,contraste_original] = brillo_contraste(I_original);
[brillo_cuadrada, contraste_cuadrada] = brillo_contraste(uint8(I_rtcuadrada));
[brillo_cubica, contraste_cubica] = brillo_contraste(uint8(I_rtcubica));

figure,
subplot(3,2,1), imshow(I_original), title(['Imagen Original', ...
    ' brillo: ', num2str(brillo_original), ' contraste:', num2str(contraste_original)])
subplot(3,2,2), imhist(uint8(I_original)), title('Histograma Original')
subplot(3,2,3), imshow(uint8(I_rtcuadrada)), title(['Imagen Raiz Cuadrada', ...
    ' brillo: ', num2str(brillo_cuadrada), ' contraste:', num2str(contraste_cuadrada)])
subplot(3,2,4), imhist(uint8(I_rtcuadrada)), title('Histograma Raiz Cuadrada')
subplot(3,2,5), imshow(uint8(I_rtcubica)), title(['Imagen Raíz Cúbica', ...
    ' brillo: ', num2str(brillo_cubica), ' contraste:', num2str(contraste_cubica)])
subplot(3,2,6), imhist(uint8(I_rtcubica)), title('Histograma Raíz Cúbica')

% La imagen tiende a aclararse, los píxeles con nivel de gris bajo tienden
% a disminuir su contraste.

clear all
clc
%% Función Sigmoide
alpha = 0.85;
I_original = imread('./P3.tif');
I_q1 = (255/2) * (1+ sin(alpha*pi*(( (double(I_original)/255) - 0.5)))/ ((sin((alpha*pi)/2))) );
I_q2 = (255/2) * (1+ tan(alpha*pi*(( (double(I_original)/255) - 0.5)))/ ((tan((alpha*pi)/2))) );

[brillo_original,contraste_original] = brillo_contraste(I_original);
[brillo_q1, contraste_q1] = brillo_contraste(uint8(I_q1));
[brillo_q2, contraste_q2] = brillo_contraste(uint8(I_q2));

figure,
plot(I_q1(:),I_original(:),'.r'), hold on
plot(I_q2(:),I_original(:),'.b')
axis([0,255,0,255])
legend('Sigmoide Seno','Sigmoide Tangente')

figure,
subplot(3,2,1), imshow(I_original), title(['Imagen Original', ...
    ' brillo: ', num2str(brillo_original), ' contraste:', num2str(contraste_original)])
subplot(3,2,2), imhist(uint8(I_original)), title('Histograma Original')
subplot(3,2,3), imshow(uint8(I_q1)), title(['Imagen Sigmoide Seno', ...
    ' brillo: ', num2str(brillo_q1), ' contraste:', num2str(contraste_q1)])
subplot(3,2,4), imhist(uint8(I_q1)), title('Histograma Sigmoide Seno')
subplot(3,2,5), imshow(uint8(I_q2)),  title(['Imagen Sigmoide Tangente', ...
    ' brillo: ', num2str(brillo_q2), ' contraste:', num2str(contraste_q2)])
subplot(3,2,6), imhist(uint8(I_q2)), title('Histograma Sigmoide Tangente')

clear all
clc
rmpath('./Funciones Necesarias/')
