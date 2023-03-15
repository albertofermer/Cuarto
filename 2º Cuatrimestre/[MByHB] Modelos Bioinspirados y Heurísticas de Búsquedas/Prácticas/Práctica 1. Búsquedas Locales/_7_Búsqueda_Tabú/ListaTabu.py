class ListaTabu:
    def __init__(self, tenencia):
        self.max_size = tenencia
        self.size = 0
        self.lista = []
    def add(self, accion):
        hora, cantidad = accion
        if self.size < self.max_size:
            self.lista.append( (hora, cantidad) )

