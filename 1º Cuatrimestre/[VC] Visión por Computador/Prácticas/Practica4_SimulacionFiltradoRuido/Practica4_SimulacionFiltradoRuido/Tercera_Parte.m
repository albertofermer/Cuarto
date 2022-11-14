%=========================================================================%
%                    TERCERA PARTE: EVALUACIÓN DE LA EFICIENCIA           %
%                    DE FILTROS GAUSSIANO, MEDIANA Y ADAPTATIVO           %
%=========================================================================%

%-------------------------------------------------------------------------%
%           Esta práctica se debe realizar sobre la imagen P4.
%-------------------------------------------------------------------------%
addpath("./Funciones_Necesarias\")
Ioriginal = imread("P4.tif");



%-------------------------------------------------------------------------%
% 1. Genera 3 imágenes con ruido gaussiano de media nula y desviación
% típica 5, 10 y 35.
%-------------------------------------------------------------------------%

desv = [5,10,35];   % Vector de sigmas
W = [3,7];          % Vector de tamaño de ventana
tabla = [];         % Tabal comparativa


for i=1:size(desv,2)   % Para cada sigma
    
    %---------------------------------------------------------------------%
    % Genero la imagen con ruido gaussiano de media 0 y desviación típica
    % desv(i).
    %---------------------------------------------------------------------%
    Imagen_Ruidosa = funcion_genera_ruido_gaussiano(Ioriginal,0,desv(i));

    for j=1:size(W,2)   % Para cada tamaño de ventana
        
        %------------------------------------------------------------------%
        %                        APLICACIÓN DE FILTROS                     %
        %------------------------------------------------------------------%

        % Aplico el filtro gaussiano con una máscara de tamaño WxW y de
        % desviación típica W/5 utilizando la función imfilter.
        filtro_gaussiano = imfilter(Imagen_Ruidosa,funcion_calcula_mascara_gaussiana_eficiente(W(j),W(j)/5,false));

        % Aplico el filtro de la mediana con vecindad 9x9
        filtro_mediana = funcion_filtroMediana(Imagen_Ruidosa,ones(W(j)),'zeros');

        % Aplico el filtro adaptativo con tamaño de ventana WxW.
        VarRuido = var( double(Imagen_Ruidosa)  -  double(Ioriginal));
        filtro_adaptativo = funcion_filtadapt_matricial(Imagen_Ruidosa,ones(W(j)),VarRuido);

        %-----------------------------------------------------------------%
        %                          CÁLCULO DE ISNR                        %
        %-----------------------------------------------------------------%

        %-----------------------------------------------------------------%
        % La relación señal-ruido es una medida logarítmica que mide el
        % ruido antes de filtrar la imagen respecto al ruido residual que
        % queda después del filtrado.
        %
        % Si el valor que queda es >0 significa que se ha corregido el
        % ruido.
        %
        % Si el valor que queda es <0 significa que ha aumentado el ruido
        % en la iamgen filtrada.
        %
        % Si el valor que queda es 0, significa que no ha filtrado
        % prácticamente nada.
        %-----------------------------------------------------------------%

        ISNR_gaussiana = funcion_evalua_ruido(Ioriginal,Imagen_Ruidosa,filtro_gaussiano);
       
        ISNR_mediana = funcion_evalua_ruido(Ioriginal,Imagen_Ruidosa,filtro_mediana);

        ISNR_adaptativo = funcion_evalua_ruido(Ioriginal,Imagen_Ruidosa,filtro_adaptativo);

        %-----------------------------------------------------------------%
        %             REPRESENTACIÓN DE LAS IMÁGENES FILTRADAS            %
        %-----------------------------------------------------------------%

        figure,
        subplot(2,2,[1,2]), imshow(Imagen_Ruidosa),  title(['Imagen Ruidosa ( \sigma^2 = ',num2str(desv(i)), ')'])
        subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')
        subplot(2,2,4), imshow(filtro_gaussiano), title(['Filtro Gaussiano ( W = ',num2str(W(j)), ', \sigma = ', num2str(W(j)/5), ')  ISNR = ', num2str(ISNR_gaussiana)])


        figure,
        subplot(2,2,[1,2]), imshow(Imagen_Ruidosa),  title(['Imagen Ruidosa ( \sigma = ',num2str(desv(i)), ')'])
        subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')        
        subplot(2,2,4), imshow(filtro_mediana), title(['Filtro de la mediana ( W = ',num2str(W(j)), ') ISNR = ', num2str(ISNR_mediana)])


        figure,
        subplot(2,2,[1,2]), imshow(Imagen_Ruidosa),  title(['Imagen Ruidosa ( \sigma^2 = ',num2str(desv(i)), ')'])
        subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')
        subplot(2,2,4), imshow(filtro_adaptativo), title(['Filtro Adaptativo ( W = ',num2str(W(j)), ')  ISNR = ', num2str(ISNR_adaptativo)])
        
        
        %pause;
        close all
        
        tabla = [tabla;desv(i),W(j),ISNR_gaussiana, ISNR_mediana, ISNR_adaptativo];
        
    end
end

%-------------------------------------------------------------------------%
%                               TABLA COMPARATIVA                         %
%-------------------------------------------------------------------------%
tabla = array2table(tabla,"VariableNames",["SigmaRuido", "TamagnoVentana", ...
    "FiltroGauss","FiltroMediana","FiltroAdaptativo"])


%-------------------------------------------------------------------------%
%                               FILTRO TEMPORAL                           %
%-------------------------------------------------------------------------%

%-------------------------------------------------------------------------%
% Un filtro temporal es un promediado de varias imágenes ruidosas.
% El ruido cambia con cada imagen, sin embargo, la escena de la imagen no
% lo hace.
%-------------------------------------------------------------------------%

Iruidosas = []; % Matriz de imágenes ruidosas
sigma = 35;     % Desviación típica del ruido gaussiano
media = 0;      % Media del ruido gaussiano

% Genera 10 imágenes con ruido gaussiano desv = 35 a partir de la imagen inicial
for i=1:10
    Iruidosas = cat(3,Iruidosas,funcion_genera_ruido_gaussiano(Ioriginal,media,sigma));
end

% Visualiza una de ellas.
Iruidosa = Iruidosas(:,:,1);

%-------------------------------------------------------------------------%
%                        APLICAMOS EL PROMEDIO                            %
%-------------------------------------------------------------------------%
Ifiltrada = uint8(mean(Iruidosas,3));


ISNR_temporal = funcion_evalua_ruido(Ioriginal,Iruidosa,Ifiltrada);

figure,
subplot(2,2,[1,2]), imshow(Iruidosa),  title(['Imagen Ruidosa ( \sigma = ',num2str(sigma), ')'])
subplot(2,2,3), imshow(Ioriginal), title('Imagen Original')        
subplot(2,2,4), imshow(Ifiltrada), title(['Filtro temporal ISNR = ', num2str(ISNR_temporal)])


clear all
rmpath("./Funciones_Necesarias/")