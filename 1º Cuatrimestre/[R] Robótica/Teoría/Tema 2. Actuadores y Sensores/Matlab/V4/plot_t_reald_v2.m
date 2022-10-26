
clear all
clc


Periodo = 3;
delay = 2;
amplitud = 90;

tf = Periodo*2;
punto = animatedline('Marker','o'); % La declaración se 
% realiza después de hacer el plot

X = 0;
Y = 0;
theta = pi/4;
mapa = [];
distancia = 5;
k=1;
tiempo(k)=0;
alpha(k) = signal_v2(tiempo,Periodo,delay,amplitud);
tstart=tic;

while tiempo(k)<tf
    k=k+1;
    tiempo(k)=toc(tstart);
    tiempo(k)

    alpha(k) = signal_v2(tiempo(k),Periodo,delay,amplitud);
    m = pinta_robot_v0(X,Y,theta,alpha(k)*pi/180,distancia,mapa);
    drawnow

end

