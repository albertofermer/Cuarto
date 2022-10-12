
clear all
clc


Periodo = 12;
tf = 2*Periodo;
t=0:0.01:tf;
y=signal_v2(t,Periodo);
plot(t,y), grid on
punto = animatedline('Marker','o'); % La declaración se 
                                    % realiza después de hacer el plot
k=1;
tiempo(k)=0;
referencia(k) = signal_v2(tiempo,Periodo);
tstart=tic;

while tiempo(k)<tf
    k=k+1;
    tiempo(k)=toc(tstart);
    tiempo(k)

    referencia(k) = signal_v2(tiempo(k),Periodo);
    clearpoints(punto);
    addpoints(punto,tiempo(k),referencia(k));

    drawnow
    % Controlador


end

close all
