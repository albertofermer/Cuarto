
addpath('./Funciones_Necesarias')

Ic = imread('./Imagenes/14.JPG');
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

figure("Name", "Proyecciones Verticales"), plot(proyecciones_verticales,1:length(proyecciones_verticales))
axis ij

% Genera un vector de proyecciones suavizado por media movil central
% utilizando una ventada de amplitud del 10% del número total de filas
% (impar). 

W = round(0.1*length(proyecciones_verticales));
if (mod(W,2) == 0)
    W = W + 1;
end

% Cada valor se obtiene como la media de todos los valores del vector
% original que abarca la ventana centrada en el valor en cuestión.
% Para los límites del vector utilizar la opción de padding 'symmetric'.
proyecciones_ampliadas = padarray(proyecciones_verticales,floor(W/2),"symmetric");

proyecciones_suavizadas = zeros(size(proyecciones_verticales));

for i=(1+floor(W/2)):length(proyecciones_ampliadas)-floor(W/2)
    ini = i-floor(W/2);
    fin = i+floor(W/2);
    proyecciones_suavizadas(i-floor(W/2)) = mean(proyecciones_ampliadas(ini:fin));
end

figure("Name", "Proyecciones Verticales Suavizadas"), plot(proyecciones_suavizadas,1:length(proyecciones_suavizadas))
axis ij

% Encontrar la posición de las filas correspondientes a los dos máximos lo
% suficientemente separados como para que correspondan a distintas zonas de
% la imagen.

% Para encontrar los dos máximos:

% - Primer máximo fmax1: fila de la imagen o posición del vector en la que se alcanza el 
%   máximo en las proyecciones suavizadas Vector_PSuav.

fmax1 = find(proyecciones_suavizadas == max(proyecciones_suavizadas));

% - Segundo máximo fmax2: fila de la imagen o posición del vector en la que se alcanza el 
%   máximo del vector generado de acuerdo a la siguiente expresión:
%   [(f - fmax1)^2 * Vector_PSuav(f)]
% donde:
%   + f: distintas posiciones del vector: 1 ≤ f ≤ Número de Filas
%   + Vector_PSuav(f): valor en el vector de las proyecciones suavizadas Vector_PSuav
%     en la posición f.

expr = zeros(size(proyecciones_suavizadas));
for f = 1:length(proyecciones_suavizadas)
    expr(f) = ((f - fmax1)^2) * proyecciones_suavizadas(f);
end

fmax2 = find(expr == max(expr));


% Encontrar la posición de la fila que presenta el valor mínimo entre
% estos dos máximos.

fmin = min(fmax1,fmax2);


% De los dos maximos encontrados, seleccionar aquel asociado a la fila
% perteneciente a la placa de la matrícula atendiendo a los criterios:

% - La fila del máximo de la placa no puede estar en los extremos de la
% imagen. Debe tener un valor entre el 10% y el 90% del número total de
% filas.

[NumFilas,NumColumnas] = size(Ifilt);

f_imagen_min = round(0.1*NumFilas);
f_imagen_max = round(0.9*NumFilas);

% La fila del máximo de la placa tiene que tener un valor alto en el vector
% de proyecciones suavizadas, superior al 60% del maximo valor de ese
% vector.

candidatas = [fmax1,fmax2];
fila_matricula = ((candidatas >= f_imagen_min) & (candidatas <= f_imagen_max)) & (candidatas > 0.6*max(proyecciones_suavizadas));


% Si ambos máximos satisfacen las dos condiciones, debe identificarse el
% máximo de la siguiente forma:
if (sum(fila_matricula) == 2)
%   - Extraer de la imagen de intensidad las subiamgenes Imax1 e Imax2 por
%   cada máximo de la siguiente forma:
%       - Altura: La subimagen debe estar centrada en la fila del máximo
%       correspondiente y tener una altura total del 10% del numero de
%       filas de la imagen ( forzar a que sea impar ).
%       - Anchura: La subimagen debe extraerse desde la columna
%       round(0.25*NumColumnas) hasta la columna round(0.75*NumColumnas).

NF = round(0.1*NumFilas);
if (mod(NF,2) == 0)
    NF = NF + 1;
end

cociente_anterior = inf;
for maximo = 1:length(candidatas)
    altura = (candidatas(maximo) - floor(NF/2)):(candidatas(maximo)+floor(NF/2));
    anchura = round(0.25*NumColumnas):round(0.75*NumColumnas);


Imax = I_reescalada(altura,anchura);
%Imax2 = I_reescalada(altura(2,:),anchura(2,:));

%   - Aplicar a cada imagen un filtro de máximos con vecindad dada por una 
% varilla horizontal de grosor 1 y longitud 9 píxeles y padding symmetric.
% (Imax1FM e Imax2FM)

vecindad = ones(1,9);

ImaxFM = ordfilt2(Imax,9,vecindad','symmetric');
%Imax2FM = ordfilt2(Imax2,9,vecindad,'symmetric');

%   - Determinar la std de Imax1, Imax2, Imax1FM e Imax2FM y calcular para
%   cada máximo el cociente entre la std de la subimagen filtrada y sin
%   filtrar.

std_Imax = std(double(Imax(:))); std_ImaxFM = std(double(ImaxFM(:)));
%std_Imax2 = std(double(Imax2(:))); std_Imax2FM = std(double(Imax2FM(:)));

% El máximo de la placa de la matrícula será aquel cuyo cociente entre las
% std calculadas sea menor.

cociente = std_ImaxFM/std_Imax;

if (cociente < cociente_anterior)
    pos_maximo_placa = maximo;
end

cociente_anterior = cociente;
%cociente2 = std_Imax2FM/std_Imax2; 
end

maximo_placa = candidatas(pos_maximo_placa);
posicion_candidatas = zeros(size(candidatas));
posicion_candidatas(pos_maximo_placa) = 1;

elseif (sum(fila_matricula) == 1)
    
    maximo_placa = candidatas(fila_matricula);
    posicion_candidatas = fila_matricula;

else

    % Asumimos que siempre habrá, al menos, una candidata.

end

% Eliminar la contribución del máximo descartado del vector de proyecciones
% suavizadas. Para ello, asigna el valor mínimo del vector suavizado a
% todas aquellas posiciones que vayan desde fmin hasta el principio o final
% del vector, dependiendo si la fila del máximo descartado es inferior o
% superior a la fila del máximo seleccionado.

valor_minimo = min(proyecciones_suavizadas);
maximo_descartado = candidatas(~posicion_candidatas);
proyecciones_suavizadas_recortadas = proyecciones_suavizadas;
if (maximo_descartado < maximo_placa)   
    proyecciones_suavizadas_recortadas(1:fmin) = valor_minimo;
else %if (maximo_descartado >= maximo_placa)
    proyecciones_suavizadas_recortadas(fmin:end) = valor_minimo;
end

figure("Name", "Proyecciones Verticales Suavizadas Reducidas"), plot(proyecciones_suavizadas_recortadas,1:length(proyecciones_suavizadas_recortadas))
axis ij

% Encuentra las filas minima y maxima que delimitan el contorno horizontal
% de la placa considerando que son la primera y última fila de la imagen
% que presentan un valor significativo. Este umbral será fijado como el 60%
% del valor máximo.

umbral = 0.6*max(proyecciones_suavizadas_recortadas);

copia_proyecciones_suavizadas = proyecciones_suavizadas_recortadas;
copia_proyecciones_suavizadas(proyecciones_suavizadas_recortadas >= umbral) = 0;

[~ , fila_min] = max(copia_proyecciones_suavizadas(1:maximo_placa));

copia_proyecciones_suavizadas(1:maximo_placa) = 0;

[~ , fila_max] = max(copia_proyecciones_suavizadas);

fila_min_placa = fila_min - 3;
fila_max_placa = fila_max + 3;


%% Tercera Fase: Detección de contornos verticales de la placa

% A partir de la componente azul de la imagen en color de resolución
% reducida

Ib = Ic_reescalada(:,:,3);

% Genera la subimagen Ired con el rango que abarca las
% fila_min_placa,fila_max_placa.

Ired = Ib(fila_min_placa:fila_max_placa,:);
figure("Name","Imagen Reducida"), imshow(Ib(fila_min_placa:fila_max_placa,:,:))

% Aplica un filtro de máximo con padding symmetric con vecindad dada por
% una varilla de grosor 1 y longitud del numero de filas de la imagen de
% entrada

vecindad = ones(1,size(Ired,1));
Ired_filt = ordfilt2(Ired,size(Ired,1),vecindad',"symmetric");
figure("Name", "Resultado de aplicación de filtro de máximos"), imshow(Ired_filt)

% Aplica la máscara horizontal de bordes de Prewitt para generar la
% magnitud de los bordes verticales de Ired.

prewitt_x = 1/6*[-1 0 1;-1 0 1; -1 0 1];

Gx = imfilter(Ired_filt,prewitt_x,'symmetric');
figure("Name", "Magnitud de bordes verticales"), imshow(mat2gray(Gx))

% Aplicar un filtro de mínimos con vecindad dada por una varilla vertical
% de grosor 1 y longitud el numero de filas de la matriz de entrada.

Ired_filt = ordfilt2(mat2gray(Gx),1,vecindad',"symmetric");
figure("Name", "Resultado de aplicación de filtro de mínimos"), imshow(Ired_filt)

% Calcula las proyecciones horizontales sobre la matriz resultante del
% apartado anterior.

proyecciones_horizontales = zeros(1,size(Ired_filt,2));

for i = 1:length(proyecciones_horizontales)
    proyecciones_horizontales(i) = mean(Ired_filt(:,i));
end

figure("Name","Proyecciones Horizontales"), plot(proyecciones_horizontales)

% Genera un vector de proyecciones suavizado por media movil central
% utilizando una ventana de amplitud 3.

vector_ampliado = padarray(proyecciones_horizontales,1,'symmetric');
proyecciones_hor_suavizadas = zeros(size(proyecciones_horizontales));
for i=1:length(proyecciones_horizontales)
    proyecciones_hor_suavizadas(i) = mean(vector_ampliado(i:i+2));
end

figure("Name","Proyecciones Horizontales Suavizadas"), plot(proyecciones_hor_suavizadas)


% 

rmpath('./Funciones_Necesarias')
