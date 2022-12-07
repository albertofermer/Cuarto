
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

camino=load('camino.dat');

l=3.5; %distancia entre rudas delanteras y traseras, tambien definido en modelo
radio_rueda=1;

%Condiciones iniciales
pose0=[0; 0; 0];

t0=0;

%final de la simulación
tf=60;

%paso de integracion
h=0.1;
%vector tiempo
t=0:h:tf;
%indice de la matriz
k=0;

%inicialización valores iniciales
pose(:,k+1)=pose0;
punto = [30,50];
t(k+1)=t0;
grid on
while ((t0+h*k) < tf)
    
    %punto(1)==pose(1,k) && (punto(2) == pose(2,k));
    %actualización
    k=k+1;

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %valores de los parámetros de control
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    orden_minimo = minima_distancia(camino,[pose(1,k),pose(2,k)]);
    punto = camino(orden_minimo+25,:);
    
    % --- Controlador de Alto Nivel ----


    delta = (pose(1,k) - punto(1))*sin(pose(3,k))-(pose(2,k) - punto(2))*cos(pose(3,k));
    LH = sqrt( (pose(1,k) - punto(1))^2 + (pose(2,k) - punto(2))^2 );
    rho=(2*delta)/(LH^2);
    kv = 1;
    velocidad_lineal = 10;
    %velocidad_lineal = kv*LH;
    
    if (velocidad_lineal > 10)
        velocidad_lineal = 10;
    elseif (velocidad_lineal < -10)
         velocidad_lineal = -10;
    end
    %velocidad_lineal = 10;
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




