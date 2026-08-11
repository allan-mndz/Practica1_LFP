public class Token {
    private int idToken;
    private String lexema;
    private String tipo;
    private int fila;
    private int columna;

    public Token(int idToken, String lexema, String tipo, int fila, int columna) {
        this.idToken = idToken;
        this.lexema = lexema;
        this.tipo = tipo;
        this.fila = fila;
        this.columna = columna;
    }

    public int getId() {
        return idToken;
    }

    public void setId(int id) {
        this.idToken = id;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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
        return String.format("ID: %-4d | Lexema: %-20s | Tipo: %-20s | Fila: %-4d | Columna: %-4d",
                idToken, "'" + lexema + "'", tipo, fila, columna);
    }
}
