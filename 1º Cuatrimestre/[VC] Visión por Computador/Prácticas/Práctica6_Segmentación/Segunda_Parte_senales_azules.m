%-------------------------------------------------------------------------%
%%                      SEGUNDA  PARTE                                   %%
%               Segmentación de señales de tráfico (azules)                %
%-------------------------------------------------------------------------%
addpath('Funciones Necesarias\')
addpath('Funciones_THCircular\')

%-------------------------------------------------------------------------%
% 8. Lee la imagen, genera y visualiza una imagen binaria (Ib) de puntos
% "rojos" de la imagen en color candidatos a pertenecer al contorno de la
% región que se pretende segmentar. Para ello utiliza un metodo de
% umbralización global.
%-------------------------------------------------------------------------%

nombre = "ImagenesPractica\SegundaParte\Signal4";
numImagenes = 2;
for imagenes=1:numImagenes

    nombre_imagen = nombre + "_" + num2str(imagenes) + ".tif";
    Imagen = imread(nombre_imagen);

    factor = 0.35;
    % Sacamos las componentes de color de la imagen.
    R = Imagen(:,:,1); G = Imagen(:,:,2); B = Imagen(:,:,3);

    % Sacamos el mínimo y el máximo de cada componente de color.
    Rmin = min(R(:)); Rmax = max(R(:));
    Gmin = min(G(:)); Gmax = max(G(:));
    Bmin = min(B(:)); Bmax = max(B(:));

    % Calculamos el umbral de cada componente de color.
    UmbralRojo = factor*(Rmin+Rmax);
    UmbralVerde = factor*(Gmin+Gmax);
    UmbralAzul = factor*(Bmin+Bmax);

    % Buscamos los píxeles rojos, por lo que las imágenes binarias son:
    Rbin = R<UmbralRojo;
    Gbin = G<UmbralVerde;
    Bbin = B>UmbralAzul;

    % Multiplicamos las tres imagenes para segmentar las tres componentes.
    Ib = and(Rbin,and(Gbin,Bbin));
    figure, imshow(Ib)

    %-----------------------------------------------------------------%
    % 9. Aplica a Ib la transformada de Hough para detectar contornos
    % circulares utilizando la función circle_hough.
    %-----------------------------------------------------------------%

    radii = 5:2:35; % Radios posibles de las circunferencias buscadas

    % Todas las imágenes tienen la misma resolución. Por lo tanto todos los
    % objetos circulares tienen su radio limitado a este rango.

    H = circle_hough(Ib,radii,'same');

    % La opción 'same' hace que los centros de la circunferencia sean centros
    % pertenecientes a píxeles de la imagen.

    % ====================== PREGUNTAS ===============================%
    % Interpreta las dimensiones de H. ¿Cuál es el significado de los
    % valores almacenados en la matriz?
    %
    % RESP:
    % H tiene esas dimensiones (360x480x16) porque 360x480 son los píxeles de
    % la imagen y, por tanto, cada píxel se convierte en un posible centro de
    % cada círculo. La tercera dimensión (16) corresponde con cada uno de los
    % radios que le hemos pasado a la función.
    %
    % Los valores almacenados en H son, por tanto, todos los puntos por
    % los que pasa una circunferencia de centro NxM y de radio R.
    %
    %
    % Calcula manualmente H(180,90,6) e interpreta el resultado.
    H(180,90,6); % = 0
    %
    % RESP:
    % En el píxel (180,90) no hay una circunferencia de radio 6.
    %
    % Ayudándote de la funcion find escribe la ecuacion de la circunferencia
    % que pasa por más puntos en Ib. Especificando su radio y coordenadas del
    % centro. ¿Cuántos puntos de Ib contiene esta cirunferencia?

    % Inicializamos la matriz de resultados.
    matCirc = [];


    for r = 1:length(radii) % por cada uno de los radios discretizados:

        % Sacamos la matriz H correspondiente al radio.
        Hr = H(:,:,r);

        % Sacamos la fila y la columna del máximo.
        [fil,col] = find(H(:,:,r) == max(Hr(:)));

        % Lo añadimos a la matriz de resultados.
        matCirc = [matCirc; r, radii(r), fil(1), col(1), H(fil(1),col(1),r)];
    end


    % La circunferencia que contiene más puntos será:
    [~,pos] = max(matCirc(:,5));
    % Por tanto, la circunferencia más probable:
    circ_mas_probable = matCirc(pos,:);

    % Escribir la ecuación de la circunferencia:
    % r^2 = (x-a)^2 + (y-b)^2
    disp(num2str(circ_mas_probable(2))+"^2 = (x - " + num2str(circ_mas_probable(3)) + ")^2 + ( y - " + num2str(circ_mas_probable(4)) + ")^2")
    disp(" La circunferencia contiene " + num2str(circ_mas_probable(end)) + " puntos.")
    % =================================================================%

    %-----------------------------------------------------------------%
    % 10. Encuentra los parámetros representativos de la circunferencia
    % más votada aplicando la funcion circle_houghpeaks con la
    % siguiente configuracion:
    %-----------------------------------------------------------------%

    P = circle_houghpeaks(H,radii,'npeaks',1);

    % ====================== PREGUNTAS ============================== %
    % ¿Qué información tiene el parámetro de salida P?
    %
    % RESP:
    % El parámetro P contiene el radio, las coordenadas del centro y el
    % número de puntos por los que pasa la circunferencia.
    % ================================================================%

    %-----------------------------------------------------------------%
    % 11. A partir de la información contenida en P, genera una imagen
    % binaria de las mismas dimensiones que las matrices que componen
    % la imagen original que especifique los píxeles correspondientes
    % a la circunferencia detectada.
    % Para ello, usa la función circlepoints.
    %-----------------------------------------------------------------%

    % Generar una imagen binaria de las mismas dimensiones que la
    % imagen binaria original.
    Ib_circunf = false(size(R));

    % Obtenemos los puntos de la circunferencia más probable.
    [x,y] = circlepoints(P(3));

    % Por cada punto de la circunferencia
    for i = 1:length(x)
        % Le sumamos la coordenada del centro para centrar la
        % circunferencia en esas coordenadas. Porque la función
        % circlepoints da los puntos centrados en el (0,0).
        Ib_circunf(y(i)+P(2),x(i)+P(1)) = true;
    end

    figure, imshow(Ib_circunf)

    %-----------------------------------------------------------------%
    % 12. Genera y visualiza la imagen binaria que representa la
    % segmentación de la señal de tráfico Ib_circle utilizando la
    % funcion imfill:
    %-----------------------------------------------------------------%
    Ib_circulo = imfill(Ib_circunf,'holes');

    % Multiplico la imagen original por la imagen binaria. De esta
    % forma podemos mostrar la zona de la imagen que nos interesa.
    figure, imshow(Imagen .* uint8(Ib_circulo))
    pause;
    close all
end
rmpath('Funciones Necesarias\')
rmpath('Funciones_THCircular\')
clear all