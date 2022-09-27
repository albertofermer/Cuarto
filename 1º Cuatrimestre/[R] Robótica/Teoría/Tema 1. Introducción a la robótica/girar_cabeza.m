%   Script para girar la cabeza del robot utilizaremos la función seno en tiempo
%   real.

% tic: iniciar el cronómetro.
% toc: parar el cronómetro.
clear all
tfinal = 2*pi;
tstart = tic;
k = 1;
y(k) = 0;
tiempo(k) = 0;
% Mientras que el tiempo sea menor que el tiempo final, 

while(tiempo(k) < tfinal)
    k = k + 1;
    tiempo(k) = toc(tstart);
    tiempo(k)
    y(k) = sin(tiempo(k));

end

plot(tiempo,y)