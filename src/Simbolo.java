public class Simbolo {
    private String identificador;
    private String tipo;          // Agente o Variable
    private String ambito;        // Nombre del agente al que pertenece
    private int linea;            // Fila donde se declaro

    public Simbolo(String identificador, String tipo, String ambito, int linea) {
        this.identificador = identificador;
        this.tipo = tipo;
        this.ambito = ambito;
        this.linea = linea;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getTipo() {
        return tipo;
    }

    public String getAmbito() {
        return ambito;
    }

    public int getLinea() {
        return linea;
    }
}