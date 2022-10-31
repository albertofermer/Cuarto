addpath('./Funciones Necesarias\')

%% Función histograma acumulado 
Img = imread('P3.tif');
[N,M] = size(Img);
H = funcion_HistAcum(Img);


%% Función Ecualiza Imagen
tic;
Ieq1 = funcion_ecualizaImagen(Img,1);
toc % 0.006566

[brillo,contraste] = brillo_contraste(Ieq1);
figure, 
subplot(2,1,1), imshow(Ieq1)
title(['B:',num2str(brillo),' // C:', num2str(contraste)])
subplot(2,1,2), imhist(Ieq1)


tic;
Ieq2 = funcion_ecualizaImagen(Img,2);
toc % 0.005445

[brillo,contraste] = brillo_contraste(Ieq2);
figure, subplot(2,1,1), imshow(Ieq2)
title(['B:',num2str(brillo),' // C:', num2str(contraste)])
subplot(2,1,2), imhist(Ieq2)


tic;
Ieq3 = funcion_ecualizaImagen(Img,3);
toc % 0.029426

figure, subplot(2,1,1), imshow(Ieq3)
title(['B:',num2str(brillo),' // C:', num2str(contraste)])
subplot(2,1,2), imhist(Ieq3)


funcion_compara_matrices(Ieq1,Ieq2)
funcion_compara_matrices(Ieq1,Ieq3)


%% Función Ecualización Zonal
tic;
Ieq_zonal = funcion_ecualizaZonal(Img);
toc % 0.075616
figure, subplot(2,1,1), imshow(Ieq_zonal)
title(['B:',num2str(brillo),' // C:', num2str(contraste)])
subplot(2,1,2), imhist(Ieq_zonal)


%% Función Ecualización Local

tic;
Ieq_local = funcion_ecualizacionLocal(Img,round(N/3),round(M/3),'zeros');
toc % 4.061213

[brillo,contraste] = brillo_contraste(Ieq_local);
figure, subplot(2,1,1), imshow(Ieq_local)
title(['B:',num2str(brillo),' // C:', num2str(contraste)])
subplot(2,1,2), imhist(Ieq_local)


%% Función Ecualización Local Modificada
tic;
Ieq_local_mod = funcion_ecualizacionLocalMod(Img);
toc % 0.088515

[brillo,contraste] = brillo_contraste(Ieq_local_mod);
figure, subplot(2,1,1), imshow(Ieq_local_mod)
title(['B:',num2str(brillo),' // C:', num2str(contraste)])
subplot(2,1,2), imhist(Ieq_local_mod)


rmpath('./Funciones Necesarias/')