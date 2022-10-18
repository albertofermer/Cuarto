% Cargar los datos.
load('../Variables Necesarias/ValoresColores.mat')
load('../Variables Generadas/variablesGeneradas.mat')

%% Generación de Datos de Entrenamiento
InputsTrainRGB = ValoresColores(:,1:3)';
InputsTrainLab = ValoresColores(:,10:12)';
InputsTrainRSL = ValoresColores(:,[1,5,10])';
InputsTrainHSIUab = ValoresColores(:,[4,5,6,8,11,12])';


OutputsTrain = (CodifValoresColores == 255)';

%% Estructura de una Red Neuronal

[NeuronasInputsRGB,temp] = size(InputsTrainRGB);
[NeuronasInputsLab,temp] = size(InputsTrainLab);
[NeuronasInputsRSL,temp] = size(InputsTrainRSL);
[NeuronasInputsHSIUab,temp] = size(InputsTrainHSIUab);

NeuronaCapaOculta1 = 15;
NeuronaCapaOculta2 = 15;

CapasRed_RGB = [NeuronasInputsRGB,NeuronaCapaOculta1,NeuronaCapaOculta2];
CapasRed_Lab = [NeuronasInputsLab,NeuronaCapaOculta1,NeuronaCapaOculta2];
CapasRed_RSL = [NeuronasInputsRSL,NeuronaCapaOculta1,NeuronaCapaOculta2];
CapasRed_HSIUab = [NeuronasInputsHSIUab,NeuronaCapaOculta1,NeuronaCapaOculta2];

netRGB = feedforwardnet(CapasRed_RGB);
netLab = feedforwardnet(CapasRed_Lab);
netRSL = feedforwardnet(CapasRed_RSL);
netHSIUab = feedforwardnet(CapasRed_HSIUab);

netRGB.trainParam.epochs = 500;
netLab.trainParam.epochs = 500;
netRSL.trainParam.epochs = 500;
netHSIUab.trainParam.epochs = 500;

netRGB = train(netRGB,InputsTrainRGB,OutputsTrain);
netLab = train(netLab,InputsTrainLab,OutputsTrain);
netRSL = train(netRSL,InputsTrainRSL,OutputsTrain);
netHSIUab = train(netHSIUab,InputsTrainHSIUab,OutputsTrain);

save('./Variables_Generadas/redes',"netRGB","netHSIUab","netRSL","netLab")