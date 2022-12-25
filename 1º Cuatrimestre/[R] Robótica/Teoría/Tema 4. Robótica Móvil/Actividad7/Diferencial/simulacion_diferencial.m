close all
clear all
global l
global radio_rueda
global camino
global pose
global punto
global k
k = 0;
pose = [];
punto = [];
camino = [];
camino_acumulado = [];
while(true)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Simulación del movimiento de un robot móvil
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

close all
clc

j=1;


%cargamos el camino

% camino=load('camino.dat');

%%%%%%%%%%%%%%%%%%%%%%%% Ejercicio 2 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
configuracion_inicial = [10,15,-pi/4]; % [X0,Y0,theta0]
configuracion_final = [80,80,(2/3)*pi]; % [Xf, Yf, tehtaf]
dd = 5; % Distancia de despegue
da = 5; % Distancia de aterrizaje

% Posición de Despegue
Pdx = configuracion_inicial(1) + dd*cos(configuracion_inicial(3));
Pdy = configuracion_inicial(2) + dd*sin(configuracion_inicial(3));

% Posición de aterrizaje
Pax = configuracion_final(1) - da*cos(configuracion_final(3));
Pay = configuracion_final(2) - da*sin(configuracion_final(3));
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% En lugar de utilizar el camino del fichero, generaremos el camino con la
% función funcion_spline
%xc=[0 10 40 80 80 80];
xc = [Pdx configuracion_inicial(1) 40 70 70 configuracion_final(1) Pax];
yc = [Pdy configuracion_inicial(2) 40 40  50 configuracion_final(2) Pay];
%yc=[0 0 40 40 100 120];

ds=5; %distancia entre puntos en cm.


l=3.5; %distancia entre rudas delanteras y traseras, tambien definido en modelo
radio_rueda=1;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Carga el fichero  BMP
MAPA = imread('.\Mapa.bmp');

%Transformación para colocar correctamente el origen del Sistema de
%Referencia
MAPA(1:end,:,:)=MAPA(end:-1:1,:,:);
%Llamada del algoritmo
delta = 15;

%longitud_camino = length(camino);
longitud_camino = length(camino_acumulado);
c = A_estrella(MAPA, delta,punto,camino_acumulado);
camino_acumulado = [camino_acumulado;c];

%camino= [camino; c];
camino = c;
if(~isempty(c))
    camino = funcion_spline_cubica_varios_puntos(camino(:,1)',camino(:,2)',ds)';
end
axis xy
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

if (~isempty(c))
%Condiciones iniciales

%pose0 = [camino(1 + longitud_camino,:), 0];
pose0 = [camino_acumulado(1 + longitud_camino,:), 0];
t0=0;

%final de la simulación
tf=60;

%paso de integracion
h=0.1;
%vector tiempo
t=0:h:tf;
%indice de la matriz
%k=0;
kt = 0;
%inicialización valores iniciales
pose(:,k+1)= pose0;
punto = camino(end,:);
t(k+1)=t0;


while ((t0+h*kt) < tf)

    %punto(1)==pose(1,k) && (punto(2) == pose(2,k));
    %actualización
    k=k+1;
    kt = kt+1;

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %valores de los parámetros de control
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



    orden_minimo = minima_distancia(camino,[pose(1,k),pose(2,k)]);
    look_ahead = 1;

    if(orden_minimo+look_ahead >= length(camino))
        punto = camino(end,:);
    else
        punto = camino(orden_minimo+look_ahead,:);
    end

    % --- Controlador de Alto Nivel ----


    delta = (pose(1,k) - punto(1))*sin(pose(3,k))-(pose(2,k) - punto(2))*cos(pose(3,k));
    LH = sqrt( (pose(1,k) - punto(1))^2 + (pose(2,k) - punto(2))^2 );
    rho=(2*delta)/(LH^2);
    kv = 1;
    %velocidad_lineal = 10;
    Ep = sqrt( (camino(end,1) - pose(1,k))^2  + (camino(end,2) - pose(2,k))^2) ;
    kp = 1;
    velocidad_lineal = kp*Ep;

    if (velocidad_lineal > 20)
        velocidad_lineal = 20;
    elseif (velocidad_lineal < -20)
        velocidad_lineal = -20;
    end


    % ---------------------------------
    %           Modelo Inverso
    % ---------------------------------

    velocidad_derecha=(velocidad_lineal/radio_rueda)*(1+l*rho);
    velocidad_izquierda=(velocidad_lineal/radio_rueda)*(1-l*rho);

    %phi=atan(rho*l);

    %volante=-0.1416;

    %volante=phi;

    conduccion=[velocidad_derecha velocidad_izquierda];

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    %para representar el punto onjetivo sobre la trayectoria


    %metodo de integración ruge-kuta

    %pose(:,k+1)=kuta_diferencial(t(k),pose(:,k),h,conduccion);
    pose(:,k+1)=kuta_diferencial_mapa(t(kt),pose(:,k),h,conduccion,MAPA);
end

 
end 

end