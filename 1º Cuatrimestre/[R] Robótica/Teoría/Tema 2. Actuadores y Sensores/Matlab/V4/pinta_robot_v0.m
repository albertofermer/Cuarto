function mapa_out=pinta_robot_v0(x,y,theta,alfa,distancia,mapa)

%-------------------------------------------------
% función para pintar el robot y el mapa versión 0
%               19/10/22 FGB
%-------------------------------------------------


persistent robot cabeza

if isempty(robot)
    robot = hgtransform; % Sistema de referencia del robot
end

if isempty(cabeza)
    cabeza = hgtransform('Parent',robot); % Sistema de referencia de la cabeza
end



M = makehgtform('translate',[x y 0], 'zrotate',theta);
robot.Matrix=M;

%cuerpo del robot
R=rectangle('Position',[-1.5 -1.5 3 3],'Parent',robot); % Figura geométrica en el sistema de referencia local.
R.LineWidth=2;

%ruedas
rueda_derecha=hgtransform('Parent',robot); % Sistema de referencia de la rueda derecha
rd=rectangle('Position',[-0.5 -0.1 1 0.2],'Parent',rueda_derecha);
M = makehgtform('translate',[0 -1 0]); % Matriz de transformacion
rueda_derecha.Matrix=M;

rueda_izquierda=hgtransform('Parent',robot); % Sistema de referencia de la rueda izquierda
ri=rectangle('Position',[-0.5 -0.1 1 0.2],'Parent',rueda_izquierda);
M = makehgtform('translate',[0 1 0]);
rueda_izquierda.Matrix=M;


%cabeza del robot

ca=rectangle('Position',[-0.25 -0.5 0.5 1],'Parent',cabeza);
M = makehgtform('translate',[1 0 0],'zrotate',alfa);
cabeza.Matrix=M;

punto_local = [distancia 0 0 1]'; % Punto esquina superior derecha (Vector Columna)

Mt=robot.Matrix*cabeza.Matrix; % Matriz de transformacion cabeza -> global

punto_global = Mt*punto_local

axis([-10 10 -10 10]);


d = animatedline(double(punto_global(1)),double(punto_global(2)),'Marker','*','LineStyle','none','Color','r');

mapa = [mapa; punto_global(1), punto_global(2)];

mapa_out = mapa;

end
