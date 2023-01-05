% -----------------------------------------------------------------------%
%%                            Tercera Fase
%                       Análisis de Resultados
% -----------------------------------------------------------------------%
addpath('..\Funciones_Necesarias\')
ruta = "../Material_Imagenes_Plantillas/02_Test/Test_";
numImagenes = 20;

aciertos = {};

matriculas_gold = {};
matriculas_gold{1} = '7824BLX';
matriculas_gold{2} = 'H0504S';
matriculas_gold{3} = '1374BXC';
matriculas_gold{4} = '8959DDY';
matriculas_gold{5} = '3189FYY';
matriculas_gold{6} = '4787DCX';
matriculas_gold{7} = 'H2305AB';
matriculas_gold{8} = 'H0853Z';
matriculas_gold{9} = 'H2462Y';
matriculas_gold{10} = 'H0612Y';
matriculas_gold{11} = '3189FYY';
matriculas_gold{12} = '4787DCX';
matriculas_gold{13} = '7226BLK';
matriculas_gold{14} = '3680FSH';
matriculas_gold{15} = '1675FLR';
matriculas_gold{16} = '9315FTC';
matriculas_gold{17} = '2904CNN';
matriculas_gold{18} = '8959DDY';
matriculas_gold{19} = '8959DDY';
matriculas_gold{20} = 'H0612Y';

for imagen = 1:numImagenes

    nombre = ruta + num2str(imagen,'%02d') + ".jpg";
    matricula = funcion_reconoce_matricula(nombre);
    aciertos{imagen} = matricula == matriculas_gold{imagen};
    close all;
end

tasa_acierto = sum(cell2mat(aciertos))/numImagenes


