%Ejercicio 3 incluir un pulsador.
clear all
close all
Wall_e=legoev3('USB'); % para detectar el robot. 

% Definir Motores
% Cabeza
motor_cabeza = motor(Wall_e,'A');

motor_rueda_Izq = motor(Wall_e,'B');

motor_rueda_Drcha = motor(Wall_e,'C');

sonar = sonicSensor(Wall_e);

% Reset Encoder
resetRotation(motor_cabeza);
resetRotation(motor_rueda_Izq);
resetRotation(motor_rueda_Drcha);
start(motor_cabeza);
v_motor = 10;

tf = 10;

% t[1] = 0
k=1;
tiempo(k)=0;

X = 0;
Y = 0;
theta = 0;
mapa = [];

pulsador = touchSensor(Wall_e,2);
pulsacion = readTouch(pulsador);

% Giro[1] <- leer encoder
giro_cabeza(k)=readRotation(motor_cabeza);
giro_rueda(k)=readRotation(motor_rueda_Izq);

Periodo = 3;
delay = 2;
amplitud = 90;

% referencia[1]=90
referencia(k)=giro_rueda(k);
error(k)=signal_v2(tiempo(k), Periodo, delay, amplitud)-giro_cabeza(k);

while (readTouch(pulsador)==false)
disp ("activar pulsador")

end
while (readTouch(pulsador)==true)
    disp ("suelta el boton")
end

tstart = tic;

while(tiempo(k) < tf && readTouch(pulsador)==false)
    k=k+1;
    tiempo(k) = toc(tstart);
%     motor_cabeza.Speed = v_motor;
    
    % leer encoder
    giro_cabeza(k)=readRotation(motor_cabeza); %esto lo devuelve en grados.
    giro_rueda(k)=readRotation(motor_rueda_Izq);
    referencia(k)=giro_rueda(k);
    distancia(k) = readDistance(sonar);
    error(k) = signal_v2(tiempo(k), Periodo, delay, amplitud)-giro_cabeza(k);
    
    kp = 0.6;
    % aqui va el controlador
    controlador = kp*error(k);
    power = int8(controlador);
    
    if power > 100
        power = 100;
    elseif power < -100
        power = -100;
    end
    
    % Actuacion de los motores
    motor_cabeza.Speed=power;
    
    m = pinta_robot_v0(X,Y,theta,double(giro_cabeza(k))*pi/180,100*distancia(k),mapa);
    drawnow
    
    tiempo(k)
end
figure, plot(tiempo,giro_cabeza,'k')

hold on

plot(tiempo,error,'b')

hold on

plot(tiempo,referencia, 'r');

motor_cabeza.Speed = 0;
stop(motor_cabeza);
stop(motor_rueda_Izq);
plot(tiempo,giro_cabeza)





