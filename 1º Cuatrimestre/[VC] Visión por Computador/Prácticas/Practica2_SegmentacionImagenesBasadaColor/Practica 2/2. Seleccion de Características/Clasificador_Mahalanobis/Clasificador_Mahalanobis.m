% Cargamos los datos obtenidos en la etapa anterior
load('../Variables Necesarias/ValoresColores.mat')
load('../Variables Generadas/espacioccasYseparabilidad.mat')
load('../Variables Generadas/variablesGeneradas.mat')

addpath('../Funciones Necesarias/')

flagRepresenta = false;
%% RGB
XoI_RGB = ValoresColores(:,1:3);
YoI_RGB = CodifValoresColores;
% Etapa de Eliminación de Outliers (Los que tengan en la componente roja un
% valor menor al 0.95)
XoI_RGB(CodifValoresColores == 255 & ValoresColores(:,1) < 0.95,:) = [];
YoI_RGB(CodifValoresColores == 255 & ValoresColores(:,1) < 0.95,:) = [];

RGB_Rojo = XoI_RGB(YoI_RGB == 255,:);
RGB_NoRojo = XoI_RGB(YoI_RGB ~= 255,:);

datosMahalanobis = funcion_Mahalanobis(RGB_Rojo,RGB_NoRojo,flagRepresenta);

%% Lab
XoI_Lab = ValoresColores(:,10:12);
YoI_Lab = CodifValoresColores;

% Etapa de Eliminación de Outliers (Los que tengan en la componente roja un
% valor menor al 0.95)

XoI_Lab(CodifValoresColores == 255 & ValoresColores(:,1) < 0.95,:) = [];
YoI_Lab(CodifValoresColores == 255 & ValoresColores(:,1) < 0.95,:) = [];

Lab_Rojo = XoI_Lab(YoI_Lab == 255,:);
Lab_NoRojo = XoI_Lab(YoI_Lab ~= 255,:);

datosMahalanobis = [datosMahalanobis; funcion_Mahalanobis(Lab_Rojo,Lab_NoRojo,flagRepresenta)];

%% RSL

XoI_RSL = ValoresColores(:,cell2mat(espacios_ccas(1)));
YoI_RSL = CodifValoresColores;

% Etapa de Eliminación de Outliers (Los que tengan en la componente roja un
% valor menor al 0.95)

XoI_RSL(CodifValoresColores == 255 & ValoresColores(:,1) < 0.95,:) = [];
YoI_RSL(CodifValoresColores == 255 & ValoresColores(:,1) < 0.95,:) = [];

RSL_Rojo = XoI_RSL(YoI_RSL == 255,:);
RSL_NoRojo = XoI_RSL(YoI_RSL ~= 255,:);

datosMahalanobis = [datosMahalanobis; funcion_Mahalanobis(RSL_Rojo,RSL_NoRojo,flagRepresenta)];

save('../Clasificador_Mahalanobis/datosMahalanobis_RGB_Lab_RSL',"datosMahalanobis");
