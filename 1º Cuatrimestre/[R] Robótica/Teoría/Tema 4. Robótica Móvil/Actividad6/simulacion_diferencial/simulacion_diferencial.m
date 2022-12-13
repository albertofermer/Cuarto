
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Simulación del movimiento de un robot móvil
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

clear all
clc

j=1;

global l
global radio_rueda
global camino
global pose
global punto
%cargamos el camino

% camino=load('camino.dat');

% En lugar de utilizar el camino del fichero, generaremos el camino con la
% función funcion_spline
xc=[0 10 40 80 80 80];
yc=[0 0 40 40 100 120];
ds=3; %distancia entre puntos en cm.
camino = funcion_spline_cubica_varios_puntos(xc,yc,ds)';


l=3.5; %distancia entre rudas delanteras y traseras, tambien definido en modelo
radio_rueda=1;

%Condiciones iniciales
pose0=[0; 0; 0];

t0=0;

%final de la simulación
tf=15;

%paso de integracion
h=0.1;
%vector tiempo
t=0:h:tf;
%indice de la matriz
k=0;

%inicialización valores iniciales
pose(:,k+1)=pose0;
punto = [0,0];
t(k+1)=t0;
grid on
axis([-10,100,-10,100])
while ((t0+h*k) < tf)
    
    %punto(1)==pose(1,k) && (punto(2) == pose(2,k));
    %actualización
    k=k+1;

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %valores de los parámetros de control
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    orden_minimo = minima_distancia(camino,[pose(1,k),pose(2,k)]);
    look_ahead = 3;

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

    pose(:,k+1)=kuta_diferencial(t(k),pose(:,k),h,conduccion);

end





