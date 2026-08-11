public class ErrorLexico {
    private String lexema;
    private String descripcion;
    private int fila;
    private int columna;

    public ErrorLexico(String lexema, String descripcion, int fila, int columna) {
        this.lexema = lexema;
        this.descripcion = descripcion;
        this.fila = fila;
        this.columna = columna;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    @Override
    public String toString() {
        return String.format("Error: '%s' | Descripción: %-25s | Fila: %-4d | Columna: %-4d",
                lexema, descripcion, fila, columna);
    }
}
