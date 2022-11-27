%-------------------------------------------------------------------------%
%%                           PRIMERA PARTE                               %%
%                       Segmentación de carreteras                        %
%            mediante la aplicación de la Transformada de Hough           %
%                       para detectar líneas rectas                       %
%-------------------------------------------------------------------------%
addpath('Funciones Necesarias\')

%-------------------------------------------------------------------------%
% 1. Lee la imagen y obtener su imagen de intensidad (I) utilizando la
% función de matlab rgb2gray.
%-------------------------------------------------------------------------%
nombre = "ImagenesPractica\PrimeraParte\";
numImagenes = 3;

for imagenes=1:numImagenes
    nombre_imagen = nombre + "P6_" + imagenes + ".tif";
    Imagen = imread(nombre_imagen);
    I = rgb2gray(Imagen);

    %-------------------------------------------------------------------------%
    % 2. A partir de I, generar una imagen de bordes (Ib) aplicando un detector
    % de bordes verticales dado por una máscara correspondiente de Sobel.
    % Considerando como umbral el 30% del valor máximo de la matriz magnitud
    % del vector gradiente obtenida por el detector.
    %-------------------------------------------------------------------------%

    % Máscaras de Sobel
    Hx_sobel = [-1 0 1; -2 0 2; -1 0 1];
    Hy_sobel = [-1, 2 -1; 0 0 0; 1 2 1];
    [Gx,Gy,modG] = funcion_calcula_gradiente(I,Hx_sobel, Hy_sobel);

    %-------------------------------------------------------------------------%
    % Gx contiene la magnitud de los píxeles de borde verticales, por lo que
    % deberemos binarizar dicha imagen.
    %-------------------------------------------------------------------------%

    magnitud_Gx = abs(Gx);
    Ib = uint8(magnitud_Gx) > 0.3*max(magnitud_Gx(:));

    %-------------------------------------------------------------------------%
    % 3. Aplica a Ib la Transformada de Hough para detectar líneas rectas
    % utilizando la función de matlab hough.
    %-------------------------------------------------------------------------%
    [H,theta, rho] = hough(Ib, 'Theta', -90:89);

    % ======================== PREGUNTAS =====================================%
    % ¿Qué representan theta y rho?

    % RESP:
    % theta:ángulo de la recta perpendicular que va desde el punto de
    % coordenadas hasta la recta.
    % rho: distancia del origen de coordenadas a la recta

    % ¿Cuál es el significado de los valores almacenados en H?
    %
    % RESP:
    % El número de puntos alineados que se encuentran en cada recta.

    % ¿Cómo es la discretización que se realiza del espacio de parámetros en
    % esta configuración por defecto?
    %
    % RESP:
    % Angulo desde -90 hasta 89
    % La distancia: desde -taumax hasta +taumax
    %               donde taumax = round(sqrt(M^2+N^2))
    % ========================================================================%

    %-------------------------------------------------------------------------%
    % Ayudándote de la función de matlab find, escribe la ecuacion de la recta
    % que pasa por más puntos en la imagen binaria Ib.
    %-------------------------------------------------------------------------%

    % Buscamos la fila y la columna donde H es máxima.
    [fila,columna] = find(H == max(H(:)));

    % Nos dará la fila en el vector de distancias y la columna en el vector de
    % ángulos, por lo que deberemos encontrar el valor correspondiente a esas
    % posiciones.

    rho_recta = rho(fila); theta_recta = theta(columna);

    % Discretizamos el espacio de coordenadas para representar la recta.
    [N,M] = size(I);
    % Utilizamos M porque como mucho la recta llegará a esa posición
    [X,~] = meshgrid(1:M);

    % Calculamos la Y correspondiente a cada X
    Y = (rho_recta - X*cosd(theta_recta) ) / sind(theta_recta);

    % Mostramos la recta más probable.
    %imshow(Ib), hold on
    %plot(X,Y,'.r');

    % Escribimos la ecuación de la recta que pasa por más puntos.
    disp("La recta que pasa por más puntos es: " + (rho_recta) + " = " + "xcos(" + theta_recta + ") + " + "ysen(" + (theta_recta) + ")");

    %-------------------------------------------------------------------------%
    % 4. Encuentra los parámetros representativos de las 5 rectas más votadas.
    % Para ello aplica la funcion de matlab houghpeaks
    %-------------------------------------------------------------------------%

    NumRectas = 5; Umbral = ceil(0.3*max(H(:)));
    P = houghpeaks(H,NumRectas,'threshold', Umbral);

    % Sacamos los valores de los parámetros de cada recta:

    rho_rectas = rho(P(:,1)); % Distancias

    theta_rectas = theta(P(:,2)); % Ángulos

    % Representamos las rectas sobre la imagen binaria.
    figure, imshow(Ib), title("5 rectas más probables"),hold on
    for j=1:length(rho_rectas)
        Y = (rho_rectas(j) - X*cosd(theta_rectas(j)) ) ./ sind(theta_rectas(j));

        % No es necesario cambiar el orden de las coordenadas porque figure ya
        % tiene los ejes de coordenadas de la imagen (imshow).

        plot(X,Y,'.r');
    end


    % ======================== PREGUNTAS =====================================%
    % ¿Qué información contiene el parámetro de salida P?
    %
    % RESP:
    % P contiene la fila y la columna de H donde se encuentran las NumRectas
    % más votadas.

    % ¿Qué significado tiene la inclusión del parámetro de entrada Umbral en la
    % función?
    %
    % RESP:
    % El parámetro Umbral se utiliza para excluir las posibles rectas que
    % tengan un valor de H menor a dicho umbral, es decir, excluye las rectas
    % que pasen por un número de puntos menor al umbral.

    % ¿Qué efecto tiene en los resultados finales fijar el umbral con un valor ceil(0.5*max(H(:))?
    %
    % RESP:
    % Que no considerará rectas aquellas celdas en las que haya un número de
    % puntos inferior al de la mitad de la recta más votada.
    %=========================================================================%

    %-------------------------------------------------------------------------%
    % 5. Muestra los segmentos de puntos de Ib que incluyen a las 5 rectas
    % detectadas.
    %-------------------------------------------------------------------------%

    %************************ CÓDIGO DEL ENUNCIADO ***************************%
    lines = houghlines(Ib,theta,rho,P,'FillGap',5,'MinLength',7);
    figure, imshow(Ib), title("Puntos de las rectas más probables"), hold on
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
    %*************************************************************************%

    % ======================== PREGUNTAS =====================================%
    % ¿Qué información contiene la variable lines?
    %
    % RESP:
    % Contiene una estructura por cada línea que hay en la imagen:
    %   point1 y point2: son dos puntos contenidos en la recta
    %   theta: es el ángulo que tiene la recta que va desde el origen a la
    %       recta de la imagen perpendicularmente
    %   rho: es la distancia hasta origen de coordenadas.
    %
    % En definitiva, contiene dos puntos de cada recta junto con los parámetros
    % de dicha recta.
    %
    %
    % ¿Qué significado tienen las opciones elegidas en la llamada de la función
    % ('FillGap',5,'MinLength',7)

    % 'FillGap',5 : Cuando encuentra dos líneas asociadas a la misma
    % transoformada de Hough separadas por menos de 5 píxeles, las considera la
    % misma línea y las fusiona en un solo segmento.
    %
    % 'MinLength', 7:  Los segmentos fusionados menores de 7 píxeles se
    % descartan.
    % ========================================================================%

    %-------------------------------------------------------------------------%
    % 6. A partir de la información obtenida en los pasos anteriores, realiza
    % la segmentación de la carretera.
    %-------------------------------------------------------------------------%

    % 6.1 Sobre una imagen binaria inicializada a '1' de las mismas dimensiones
    % que la imagen de intensidad original, asigna un valor '0' a todos los
    % píxeles presentes en las rectas detectadas en P.

    Ibinaria = ones(N,M); % Imagen binaria de las mismas dimensiones que I.

    for j=1:length(P) % Por cada una de las rectas de P:
        % Se calculan sus puntos.
        Y = (rho_rectas(j) - X*cosd(theta_rectas(j)) ) / sind(theta_rectas(j));

        % Nos quedamos con los puntos que nos interesan (positivos y menores
        % que N).

        YoI = Y(Y>0 & Y<=N);
        XoI = X(Y>0 & Y<=N);

        for i=1:length(XoI) % Por cada valor de X
            % Ponemos a 0 los píxeles correspondientes.
            Ibinaria(ceil(YoI(i)),ceil(XoI(i))) = 0;
        end
    end


    % 6.2 Aplicar un filtro de mínimos 3x3 a la imagen binaria para unir los
    % puntos de las líneas detectadas y delimitar regiones candidatas a ser la
    % zona principal de la carretera.

    Ibinaria_filt = ordfilt2(Ibinaria,1,ones(3));
    figure, imshow(Ibinaria_filt), title("Imagen con filtro de mínimos 3x3")

    % 6.3 Genera y visualiza la imagen binaria que representa la segmentación
    % de la carretera asumiendo que es la región que contiene al píxel central
    % de la imagen.

    % Primero etiquetamos las zonas candidatas a ser la zona principal de la
    % carretera.
    Ietiq = bwlabel(Ibinaria_filt);

    % Escogemos la etiqueta que corresponde con el píxel central de la imagen.
    etiq = Ietiq(round(N/2),round(M/2));

    % La imagen de interés serán aquellos píxeles que tengan como etiqueta la
    % misma que la del píxel central.
    IOI = (Ietiq == etiq);

    % Visualizamos la zona de interés en la imagen original.
    figure, imshow(Imagen .* uint8(IOI)), title("Imagen Segmentada")
    pause
    close all

end

rmpath('Funciones Necesarias\')