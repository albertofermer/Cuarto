%Ejercicio 3 incluir un pulsador.
clear 
mi_Robot=legoev3('USB'); % para detectar el robot. 
motor_cabeza = motor(mi_Robot,'A');
resetRotation(motor_cabeza);
start(motor_cabeza);
v_motor = 10;

tf = 10;
k=1;
tiempo(k)=0;
pulsador = touchSensor(mi_Robot,2);
pulsacion = readTouch(pulsador);
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
    motor_cabeza.Speed = v_motor;
    giro_A(k)=readRotation(motor_cabeza); %esto lo devuelve en grados.
    tiempo(k)
end
motor_cabeza.Speed = 0;
stop(motor_cabeza);
plot(tiempo,giro_A)





