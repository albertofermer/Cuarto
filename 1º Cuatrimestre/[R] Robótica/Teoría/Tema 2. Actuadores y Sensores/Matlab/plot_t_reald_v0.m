
clear all
clc

t=0:0.01:4*pi;
y=signal_v0(t);

i=1;
tiempo(i)=0;
x(i)=signal_v0(tiempo);
tstart=tic;

while tiempo(i)<4*pi
    i=i+1;
    tiempo(i)=toc(tstart);
    tiempo(i)
    
    % Controlador
    % pause(0.5);
    
    
    x(i)=signal_v0(tiempo(i));
    cla
    plot(t,y), hold on
    plot(tiempo(i),x(i),'xr')
    drawnow
end

