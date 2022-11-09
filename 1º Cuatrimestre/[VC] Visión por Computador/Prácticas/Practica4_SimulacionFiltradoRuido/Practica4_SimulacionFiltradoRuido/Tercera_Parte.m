%% Tercera Parte. Evaluación de eficiencia de filtros gaussiano, mediana y adaptativo.

addpath("./Funciones_Necesarias\")

% 1. Genera 3 imágenes con ruido gaussiano de media nula y desviación
% típica 5, 10 y 35.

Ioriginal = imread("P4.tif");
desv = [5,10,35];
W = [3,7];
for i=1:size(desv,2)
    for j=1:size(W,2)
        Imagen_Ruidosa = funcion_genera_ruido_gaussiano(Ioriginal,0,desv(i));
        filtro_gaussiano = imfilter(Imagen_Ruidosa,funcion_calcula_mascara_gaussiana_eficiente(W(j),desv(i)^2,false));
        filtro_mediana = funcion_filtroMediana(Imagen_Ruidosa,ones(W(j)),'zeros');
        VarRuido = var( double(Imagen_Ruidosa)  -  double(Ioriginal));
        filtro_adaptativo = funcion_filtadapt_matricial(Imagen_Ruidosa,ones(W(j)),VarRuido,'symmetric');

        ISNR_gaussiana = funcion_evalua_ruido(Ioriginal,Imagen_Ruidosa,filtro_gaussiano)
        figure,
        subplot(2,2,[1,2]), imshow(Imagen_Ruidosa),  title(['Imagen Ruidosa ( \sigma^2 = ',num2str(desv(i)), ')'])
        subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')
        subplot(2,2,4), imshow(filtro_gaussiano), title(['Filtro Gaussiano ( W = ',num2str(W(j)), ')  ISNR = ', num2str(ISNR_gaussiana)])
        
        ISNR_mediana = funcion_evalua_ruido(Ioriginal,Imagen_Ruidosa,filtro_mediana)
        figure,
        subplot(2,2,[1,2]), imshow(Imagen_Ruidosa),  title(['Imagen Ruidosa ( \sigma^2 = ',num2str(desv(i)), ')'])
        subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')        
        subplot(2,2,4), imshow(filtro_mediana), title(['Filtro de la mediana ( W = ',num2str(W(j)), ') ISNR = ', num2str(ISNR_mediana)])

        ISNR_adaptativo = funcion_evalua_ruido(Ioriginal,Imagen_Ruidosa,filtro_adaptativo)
        figure,
        subplot(2,2,[1,2]), imshow(Imagen_Ruidosa),  title(['Imagen Ruidosa ( \sigma^2 = ',num2str(desv(i)), ')'])
        subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')
        subplot(2,2,4), imshow(filtro_adaptativo), title(['Filtro Adaptativo ( W = ',num2str(W(j)), ')  ISNR = ', num2str(ISNR_adaptativo)])
        pause;
        close all

        
    end
end



rmpath("./Funciones_Necesarias/")