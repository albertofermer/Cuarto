addpath('./Funciones Necesarias\')

% Sacamos el histograma de referencia de cada componente de color de la
% imagen patrón.
W = 3;
I_patron = imread('./ColorPatron.bmp');

Ip_R = (I_patron(:,:,1));
Ip_G = (I_patron(:,:,2));
Ip_B = (I_patron(:,:,3));

I_patron_d = double(I_patron);
ROI_patron = (I_patron_d(:,:,1)+I_patron_d(:,:,2)+I_patron_d(:,:,3)) > 150;

%% CIERRE MORFOLÓGICO
% Aplicamos un filtro de máximos para eliminar el ruido tipo 'pimienta'
I_filt_patron = ordfilt2(ROI_patron,W*W,ones(W));
% Aplicamos un filtro de mínimos para devolver la región de interés al
% tamaño original
I_filt_patron = ordfilt2(I_filt_patron,1,ones(W));

%% APERTURA MORFOLÓGICA
% Aplicamos un filtro de mínimos para eliminar el ruido tipo 'sal'
I_filt_patron = ordfilt2(I_filt_patron,1,ones(W));
% Aplicamos un filtro de máximos para devolver la región de interés al
% tamaño original
I_filt_patron = ordfilt2(I_filt_patron,W*W,ones(W));

% Consideramos solamente los píxeles de la zona de interés
h_r_patron = imhist(Ip_R(I_filt_patron));
h_g_patron = imhist(Ip_G(I_filt_patron));
h_b_patron = imhist(Ip_B(I_filt_patron));




% Para cada imagen de fondo de ojo:
numImagenes = 4;
for i=1:numImagenes

nombre = ['Color',num2str(i),'.bmp'];
I = imread(nombre);
Id = double(I);
ROI = (Id(:,:,1)+Id(:,:,2)+Id(:,:,3)) > 150;

%% CIERRE MORFOLÓGICO
% Aplicamos un filtro de máximos para eliminar el ruido tipo 'pimienta'
I_b = ordfilt2(ROI,W*W,ones(W));
% Aplicamos un filtro de mínimos para devolver la región de interés al
% tamaño original
I_b = ordfilt2(I_b,1,ones(W));

%% APERTURA MORFOLÓGICA
% Aplicamos un filtro de mínimos para eliminar el ruido tipo 'sal'
I_b = ordfilt2(I_b,1,ones(W));
% Aplicamos un filtro de máximos para devolver la región de interés al
% tamaño original
I_b = ordfilt2(I_b,W*W,ones(W));


%% I_filt contiene la imagen sin ruido

% Sacamos los componentes RGB de la imagen
I_R = I(:,:,1); I_G = I(:,:,2); I_B = I(:,:,3);

R_ROI = uint8(double(I_R) .* double(I_b));
G_ROI = uint8(double(I_G) .* double(I_b));
B_ROI = uint8(double(I_B) .* double(I_b));

[Ieq_R,T_R] = histeq(R_ROI,h_r_patron);
[Ieq_G,T_G] = histeq(G_ROI,h_g_patron);
[Ieq_B,T_B] = histeq(B_ROI,h_b_patron);

Ieq = cat(3,Ieq_R,Ieq_G,Ieq_B);


figure,
subplot(3,2,1), imshow(uint8(double(I_patron).*double(I_filt_patron)));
title('Imagen Patrón')
subplot(3,2,2), representa_histogramas(I_patron,I_filt_patron)
title('Histograma Patrón')

subplot(3,2,3), imshow(uint8(double(I).*double(I_b)));
title(['Imagen de Entrada: ',nombre])
subplot(3,2,4), representa_histogramas(I,I_b)
title(['Histograma de Entrada: ',nombre])

subplot(3,2,5), imshow(uint8(double(Ieq).*double(I_b)));
title(['Imagen de Salida: ',nombre])
subplot(3,2,6), representa_histogramas(Ieq,I_b)
title(['Imagen de Salida: ',nombre])
end

rmpath('./Funciones Necesarias\')
