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
Ib = uint8(magnitud_Gx) > 0.3*max(magnitud_Gx(:));
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
%   Angulo desde -90 hasta 89
%   La distancia: desde -taumax hasta +taumax


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

lines = houghlines(Ib,theta,rho,P,'FillGap',5,'MinLength',7);
figure, imshow(Ib), hold on
max_len = 0; 
for k = 1:length(lines)
    xy = [lines(k).point1; 
    lines(k).point2];
    plot(xy(:,1),xy(:,2),'LineWidth',2,'Color','green');
% Plot beginnings and ends of lines 
    plot(xy(1,1),xy(1,2),'x','LineWidth',2,'Color','yellow'); 
    plot(xy(2,1),xy(2,2),'x','LineWidth',2,'Color','red');
% Determine the endpoints of the longest line segment 
    len = norm(lines(k).point1 - lines(k).point2);
    if ( len > max_len)
        max_len = len; 
        xy_long = xy;
    end
end
% highlight the longest line segment
plot(xy_long(:,1),xy_long(:,2),'LineWidth',2,'Color','red');

% ¿Qué información contiene la variable lines?

% Contiene una estructura por cada línea que hay en la imagen
% point1 y point2 son dos puntos contenidos en la recta
% theta es el ángulo que tiene la recta que va desde el origen a la 
% recta de la imagen perpendicularmente
% rho es la distancia del origen de coordenadas.

% ¿Qué significado tienen las opciones elegidas en la llamada de la función
% ('FillGap',5,'MinLength',7)

% 'FillGap',5 : When houghlines finds two line segments associated
%                 with the same Hough transform bin that are separated
%                 by less than 'FillGap' distance, houghlines merges
%                 them into a single line segment.

% 'MinLength':  Merged line segments shorter than 'MinLength'
%                are discarded.

Ibinaria = ones(N,M);
% rho_rectas = rho(P(:,1));
% theta_rectas = theta(P(:,2));
figure,
imshow(Ibinaria), hold on

for j=1:length(P)
    Y = (rho_rectas(j) - X*cosd(theta_rectas(j)) ) / sind(theta_rectas(j));
    YoI = Y(Y>0 & Y<M);
    XoI = X(Y>0 & Y<M);
    Ibinaria(unique(YoI),unique(XoI)) = 0;
end


rmpath('Funciones Necesarias\')