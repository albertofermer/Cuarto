%-------------------------------------------------------------------------%
%%                      PRIMERA PARTE                                    %%
%-------------------------------------------------------------------------%
addpath('Funciones Necesarias\')
Imagen = imread('ImagenesPractica\PrimeraParte\P6_1.tif');

% Genera la imagen de intensidad de P6_1.tif
I = rgb2gray(Imagen);

% Aplicar un detector de bordes verticales sobre I.
Hx_sobel = [-1 0 1; -2 0 2; -1 0 1];
Hy_sobel = [-1, 2 -1; 0 0 0; 1 2 1];

[Gx,Gy,modG] = funcion_calcula_gradiente(I,Hx_sobel, Hy_sobel);

magnitud_Gx = abs(Gx);
Ib = uint8(abs(Gx)) > 0.3*max(magnitud_Gx(:));
imshow(Ib)


% Aplica a Ib la transformada de Hough

[H,theta, rho] = hough(Ib, 'Theta', -90:89);

% ¿Qué representan theta y rho?
% theta:ángulo de la recta perpendicular que va desde el punto de 
% coordenadas hasta la recta.  
% rho: distancia del origen de coordenadas a la recta

% ¿Cuál es el significado de los valores almacenados en H?
% El número de puntos alineados que se encuentran en cada recta.

% ¿Cómo es la discretización que se realiza del espacio de parámetros en
% esta configuración por defecto?
%  


% Escribe la ecuacion de la recta que pasa por más puntos en la imagen
% binaria con find

[fila,columna] = find(H == max(H(:)));
rho_recta = rho(fila);
theta_recta = theta(columna);
[N,M] = size(I);
[X,~] = meshgrid(1:M);

Y = (rho_recta - X*cosd(theta_recta) ) / sind(theta_recta);

imshow(Ib), hold on
plot(X,Y,'.r');


% Aplica la funcion de matlab houghpeaks para encontrar las 5 rectas ás
% votadas.

NumRectas = 5; Umbral = ceil(0.3*max(H(:)));
P = houghpeaks(H,NumRectas,'threshold', Umbral);

rho_rectas = rho(P(:,1));
theta_rectas = theta(P(:,2));
imshow(Ib), hold on
for j=1:length(rho_rectas)
Y = (rho_rectas(j) - X*cosd(theta_rectas(j)) ) ./ sind(theta_rectas(j));
plot(X,Y,'.r');
end

% P contiene la fila y la columna de H donde se encuentran las NumRectas
% más votadas.

% Umbral: no considera los valores de H menores a dicho umbral para ser rectas.

% ¿ Qué efecto tiene en los resultados finales fijar el umbral con un valor ceil(0.5*max(H(:))?
% Que no considerará rectas aquellas celdas en las que haya un número de
% puntos inferior al de la mitad de la recta más votada.

rmpath('Funciones Necesarias\')