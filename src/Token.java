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
        return idToken + 1;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    @Override
    public String toString() {
        return String.format("ID: %-4d | Lexema: %-20s | Tipo: %-20s | Fila: %-4d | Columna: %-4d",
                idToken, "'" + lexema + "'", tipo, fila, columna);
    }
}
