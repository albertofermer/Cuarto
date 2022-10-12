function salida = signal_v2(t, Periodo)
%Genera una función seno con mayor periodo.
% sin(w*t) donde w es la fase (2*pi/T)
salida=sin((2*pi/Periodo)*t);
end

