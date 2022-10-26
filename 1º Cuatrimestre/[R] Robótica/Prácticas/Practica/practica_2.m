%Ejercicio 3 incluir un pulsador.
clear all
close all
mi_Robot=legoev3('USB'); % para detectar el robot. 

% Definir Motores
% Cabeza
motor_cabeza = motor(mi_Robot,'A');

motor_rueda_Izq = motor(mi_Robot,'B');

motor_rueda_Drcha = motor(mi_Robot,'C');

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

pulsador = touchSensor(mi_Robot,2);
pulsacion = readTouch(pulsador);

% Giro[1] <- leer encoder
giro_cabeza(k)=readRotation(motor_cabeza);
giro_rueda(k)=readRotation(motor_rueda_Izq);

% referencia[1]=90
referencia(k)=giro_rueda(k);
error(k)=referencia(k)-giro_cabeza(k);

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
    
    error(k) = referencia(k)-giro_cabeza(k);
    
    const = 0.6;
    % aqui va el controlador
    controlador = const*error(k);
    power = int8(controlador);
    
    if power > 100
        power = 100;
    elseif power < -100
        power = -100;
    end
    
    % Actuacion de los motores
    motor_cabeza.Speed=power;
    
    m = pinta_robot_v0(X,Y,theta,double(giro_cabeza(k))*pi/180,0,mapa);
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





