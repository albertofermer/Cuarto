
addpath('./Funciones_Necesarias')

Ic = imread('./Imagenes/01.JPG');
imshow(Ic)

%% Primera Fase: Obtención de la Imagen de Trabajo
% Para trabajar con 120 columnas se hace un reescalado.
figure('Name',"Obtención de Imágenes de Trabajo"),
Ic_reescalada =imresize(Ic,[NaN,120]);
I_reescalada = rgb2gray(Ic_reescalada);
Ilog = 100 + 20*log(double(I_reescalada)+1);

subplot(2,2,1), imshow(Ic), title("Imagen Original")
subplot(2,2,2), imshow(Ic_reescalada), title("Imagen Reescalada")
subplot(2,2,3), imshow(I_reescalada), title("Imagen de Intensidad")
subplot(2,2,4), imshow(uint8(Ilog)), title("Imagen logarítmica")

%% Segunda Fase. Detección de Contornos Horizontales

% Aplicar una máscara de bordes horizontales de Prewitt con la constante de
% proporcionalidad = 1/6.
prewitt_x = 1/6*[-1 0 1;-1 0 1; -1 0 1];
Gx = imfilter(Ilog,prewitt_x,'symmetric');
Ibordes = mat2gray(abs(Gx));

% Aplicar un filtro de orden que seleccione el valor del percentil 80 de
% una vecindad de 3 filas y 24 columnas.
Ifilt = filtro_orden_percentil(Ibordes,ones(3,24),80,'symmetric');

figure("Name","Aplicación de Máscara de Prewitt y Filtro de percentil-80")
subplot(1,2,1),imshow(Ibordes),title("Filtro Horizontal de Prewitt")
subplot(1,2,2), imshow(Ifilt), title("Imagen filtrada con percentil-80")


% Calcular las proyecciones verticales sobre la matriz Ifilt. Para cada
% fila, obtener la media de todos los valores de las columnas.

proyecciones_verticales = zeros(size(Ifilt,1),1);

for i=1:length(proyecciones_verticales)
    proyecciones_verticales(i) = mean(Ifilt(i,:));
end

figure("Name", "Proyecciones Verticales"), plot(proyecciones_verticales)


rmpath('./Funciones_Necesarias')
