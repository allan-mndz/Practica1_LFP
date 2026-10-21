public class Variable {
    private String nombre;
    private String valor;
    private int fila;
    private int columna;

    public Variable(String nombre, String valor, int fila, int columna) {
        this.nombre = nombre;
        this.valor = valor;
        this.fila = fila;
        this.columna = columna;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getFila() { return fila; }

    public int getColumna() { return columna; }
}
