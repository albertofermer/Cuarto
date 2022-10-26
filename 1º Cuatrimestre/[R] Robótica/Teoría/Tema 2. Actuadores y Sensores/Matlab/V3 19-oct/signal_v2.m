function salida = signal_v2(t, Periodo, delay,amplitud)
%Genera una función seno con mayor periodo.
% sin(w*t) donde w es la fase (2*pi/T)
salida=amplitud*sin((2*pi/Periodo)*(t-delay));
salida(t<delay) = 0;
salida(t>Periodo+delay) = 0;


end

