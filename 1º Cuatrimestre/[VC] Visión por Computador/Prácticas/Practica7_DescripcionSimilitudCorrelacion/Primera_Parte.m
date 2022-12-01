%% ===================================================================== %%
%                               PRIMERA PARTE
% DESCRIPCIÓN DE SIMILITUDES MEDIANTE CORRELACIÓN BIDIMENSIONAL NORMALIZADA
% ========================================================================%

addpath('./Funciones_Necesarias\')

I = imread('ImagenesPractica\PrimeraParte\Imagen.tif');
T = imread('ImagenesPractica\PrimeraParte\Plantilla.tif'); % (250,250)

subplot(1,2,1), imshow(I), title('Imagen')
subplot(1,2,2), imshow(T), title('Plantilla')

NormCrossCorr = funcion_normcorr2(double(I),double(T));
[fila,columna] = find(NormCrossCorr == max(NormCrossCorr(:)));

imshow(mat2gray(NormCrossCorr)), hold on
plot(columna,fila,'*r')
[NI,MI] = size(I);
[NT,MT] = size(T);
ncc = normxcorr2(T,I);
[Nncc,Mncc] = size(ncc);
% Observar las dimensiones de ncc. Hay que ajustar su tamaño para hacer 
% coincidir la información de sus puntos con los píxeles de la imagen I
ncc=ncc(1+floor(NT/2):Nncc-floor(NT/2),1+floor(MT/2):Mncc-floor(MT/2));

ncc(1:floor(NT/2),:)=0; 
ncc(NI-floor(NT/2)+1:NI,:)=0;
ncc(:,1:floor(MT/2))=0;
ncc(:,MI-floor(MT/2)+1:MI)=0;

error = NormCrossCorr-ncc;
error = sum(error(:))


rmpath('./Funciones_Necesarias\')


