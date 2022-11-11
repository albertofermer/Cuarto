%% Iniciar Robot
codigo=45; 
% Identificador = Iniciacion('RBDK',codigo); 
Identificador = Iniciacion('Robot_2',codigo); 

%% Posicion Inicial
% Robot vuelve a posicion inicial
Ready_lab(Identificador,codigo);

%% Configuracion Pick

posicion_pick=[20 -10 0];

alfa=0;
beta=0;
gamma=0; % Angulos de Euler

codo = 1;
avance = 1;
simetrico = 0;

RG2_lab(100,Identificador, codigo)

matriz_pieza=Desplazamiento(posicion_pick(1), posicion_pick(2), posicion_pick(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);

matriz_agarre = Desplazamiento(0,0,4)*Rotacionz(-pi/2)*Rotacionx(pi);
matriz_pinza_global = matriz_pieza*matriz_agarre;

[q1, q2, q3, q4, q5, q6]=inv_kinema_ur3_new(matriz_pinza_global,codo,avance,simetrico);

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];
aceleracion = 0.5;
velocidad = 10.5;

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)


%% Configuracion pick de aproximacion

matriz_pieza=Desplazamiento(posicion_pick(1), posicion_pick(2), posicion_pick(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);

matriz_agarre = Desplazamiento(0,0,3)*Rotacionz(-pi/2)*Rotacionx(pi);
matriz_pinza_global = matriz_pieza*matriz_agarre;

[q1, q2, q3, q4, q5, q6]=inv_kinema_ur3_new(matriz_pinza_global,codo,avance,simetrico);

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)

%% Acciona la pinza

RG2_lab(20,Identificador, codigo)


%% Configuracion Desplazamiento

posicion=[20 -10 0];

matriz_pieza=Desplazamiento(posicion(1), posicion(2), posicion(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionx(gamma);

matriz_agarre = Desplazamiento(0,0,5)*Rotacionz(-pi/2)*Rotacionx(pi);
matriz_pinza_global = matriz_pieza*matriz_agarre;

[q1, q2, q3, q4, q5, q6]=inv_kinema_ur3_new(Desplazamiento(0,0,5)*matriz_pinza_global,codo,avance,simetrico);

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)


%% Configuracion de aproximacion de place

posicion_place=[20 10 0];

alfa=0;
beta=0;
gamma=0; % Angulos de Euler

matriz_pieza=Desplazamiento(posicion_place(1), posicion_place(2), posicion_place(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionz(gamma);

matriz_pinza = matriz_pieza*matriz_agarre;
[q1 q2 q3 q4 q5 q6] = inv_kinema_ur3_new(Desplazamiento(0,0,5)*matriz_pinza, codo, avance, simetrico)

% %% Configuracion de place
% 
% [q1 q2 q3 q4 q5 q6] = inv_kinema_ur3_new(Desplazamiento(0,0,5)*matriz_pinza, codo, avance, simetrico)


% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)

%% Configuracion de place

matriz_pieza=Desplazamiento(posicion_place(1), posicion_place(2), posicion_place(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionz(gamma);

matriz_pinza = matriz_pieza*matriz_agarre;
[q1 q2 q3 q4 q5 q6] = inv_kinema_ur3_new(Desplazamiento(0,0,-2)*matriz_pinza, codo, avance, simetrico)

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)

%% Desacciona la pinza
RG2_lab(100,Identificador, codigo)

%% Configuracion de despegue

matriz_pinza = matriz_pieza*matriz_agarre;

[q1 q2 q3 q4 q5 q6] = inv_kinema_ur3_new(Desplazamiento(0,0,5)*matriz_pinza, codo, avance, simetrico)

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)


%% Configuracion de despegue

matriz_pinza = matriz_pieza*matriz_agarre;

[q1 q2 q3 q4 q5 q6] = inv_kinema_ur3_new(Desplazamiento(0,0,5)*matriz_pinza, codo, avance, simetrico)

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)

%% Configuracion de desplazamiento a posicion inicial
posicion_place=[20 -10 0];

matriz_pieza=Desplazamiento(posicion_place(1), posicion_place(2), posicion_place(3))*Rotacionz(alfa)*Rotaciony(beta)*Rotacionz(gamma);

matriz_pinza = matriz_pieza*matriz_agarre;

[q1 q2 q3 q4 q5 q6] = inv_kinema_ur3_new(Desplazamiento(0,0,5)*matriz_pinza, codo, avance, simetrico)

% Mover Robot
% angulos
angulos = [q1 q2 q3 q4 q5 q6];

% mover robot a y con los parametros introducidos previamente
MoveJ_Robot_lab(angulos, aceleracion, velocidad, Identificador, codigo)

