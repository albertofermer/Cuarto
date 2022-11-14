%=========================================================================%
%%                              PRIMERA PARTE                             %%
%        GENERACIÓN DE IMÁGENES. ANÁLISIS DE PERFILES DE INTENSIDAD       %
%=========================================================================%

addpath('./Funciones_Necesarias\')

%-------------------------------------------------------------------------%
% 1. A partir de la imagen P5 genera y visualiza las siguientes imágenes.
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
%                           IMAGEN DE INTENSIDAD                          %
%-------------------------------------------------------------------------%
P5 = imread("P5.tif");
I = uint8(rgb2gray(P5));
figure,
imshow(uint8(I));

%-------------------------------------------------------------------------%
%                 IMAGEN SUAVIZADA CON FILTRO GAUSSIANO W=5               %
%-------------------------------------------------------------------------%
W = 5;
sigma = W/5;
Igauss = imfilter(I,fspecial('gaussian',W,sigma));

figure,
imshow(Igauss), title('Imagen suavizada')

[N,M] = size(I);
imagenes = cat(3,I,Igauss);
text = ["Imagen Original","Imagen suavizada"];
cortes = [0.25,0.5,0.75];
colores = ["red","green","blue"];
for i=1:size(imagenes,3)
        perfil = [];
        I_aux = imagenes(:,:,i);
        % Visualiza la imagen con la línea del perfil.
        figure,
        subplot(2,1,1), imshow(I_aux), title(text(i));
    
        for j=1:length(cortes)
            perfil = [perfil;I_aux(round(cortes(j)*N),:)];
            line([1,M],[round(cortes(j)*N),round(cortes(j)*N)],'Color',colores(j)),
        end
       
        subplot(2,1,2),
        for j=1:size(perfil,1)
            plot(perfil(j,:),colores(j)), hold on
        end
        legend("25%","50%","75%")
        axis([0,M,0,250])
end


