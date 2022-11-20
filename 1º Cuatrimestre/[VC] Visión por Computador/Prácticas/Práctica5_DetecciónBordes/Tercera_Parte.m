%=========================================================================%
%%                              TERCERA PARTE                             %%
%          DETECCIÓN DE BORDES MEDIANTE LA FUNCIÓN EDGE DE MATLAB         %
%=========================================================================%

addpath('./Funciones_Necesarias\')

% Implementar un detector de bordes basado en primera derivada a través de
% la función  [Gx, Gy, modG] = funcion_calcula_gradiente(I,Hx,Hy)

%-------------------------------------------------------------------------%
%                           IMAGEN DE INTENSIDAD                          %
%-------------------------------------------------------------------------%
P5 = imread("P5.tif");
I = uint8(rgb2gray(P5));
%-------------------------------------------------------------------------%
%                 IMAGEN SUAVIZADA CON FILTRO GAUSSIANO W=5               %
%-------------------------------------------------------------------------%
W = 5;
sigma = W/5;
Igauss = imfilter(I,fspecial('gaussian',W,sigma));


%-------------------------------------------------------------------------%
% Aplicar sobre I y Igauss un detector de Sobel con un umbral (umbral) y
% con otro umbral de valor 0.4*umbral
%-------------------------------------------------------------------------%

Imagenes = cat(3,I,I,Igauss, Igauss);
titulo = ["Imagen de Intensidad","Imagen de Intensidad","Imagen Suavizada","Imagen Suavizada"];

% El umbral que se utiliza será el que genera la función edge con la iamgen
% de Intensidad por defecto.
[~, umbral] = edge(I);

for i=1:2:size(Imagenes,3)
[Isobel, ~] = edge(Imagenes(:,:,i),"sobel","nothinning", umbral);
subplot(2,2,i), imshow(Isobel), title([titulo(i), " umbral: " + num2str(umbral), "Adelgazamiento: No"]);
[Isobel_umbralmenor, ~] = edge(Imagenes(:,:,i+1),"sobel","nothinning", 0.4*umbral);
subplot(2,2,i+1), imshow(Isobel_umbralmenor), title([titulo(i+1), " umbral: " + num2str(umbral*0.4), "Adelgazamiento: No"]);
end


% Aplicar la función edge sin la opción nothinning
figure,
for i=1:2:size(Imagenes,3)
[Isobel, ~] = edge(Imagenes(:,:,i),"sobel",umbral);
subplot(2,2,i), imshow(Isobel), title([titulo(i), " umbral: " + num2str(umbral), "Adelgazamiento: Sí"]);
[Isobel_umbralmenor, ~] = edge(Imagenes(:,:,i+1),"sobel", 0.4*umbral);
subplot(2,2,i+1), imshow(Isobel_umbralmenor), title([titulo(i+1), " umbral: " + num2str(umbral*0.4), "Adelgazamiento: Sí"]);
end


%-------------------------------------------------------------------------%
% Aplicar sobre I un detector de Canny con un umbral superior de umbral y uno
% inferior de 0.4*umbral
%-------------------------------------------------------------------------%

[Icanny, ~] = edge(I,"canny",[0.4*umbral,umbral]);
figure, imshow(Icanny), title(["Detector de bordes de Canny", "umbrales: [" + num2str(0.4*umbral) + "," + num2str(umbral) + "]"])


%-------------------------------------------------------------------------%
% Aplicar sobre I un detector de bordes Laplaciana de la gaussiana
%-------------------------------------------------------------------------%

% El operador Laplaciana es muy sensible al ruido, por lo que es
% imprescindible utilizar un filtrado gaussiano:
I_filt = imfilter(I,fspecial('gaussian'),'replicate');

[ILoG, ~] = edge(mat2gray(I_filt),'log');
figure, imshow(ILoG), title("Detector de bordes Laplaciana de la Gaussiana")

