public class Token {
    private int idToken;
    private String lexema; // texto puro
    private String tipo; // categoria gramatical
    private int fila;
    private int columna;

    public Token(String tipo, String lexema, int fila, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.fila = fila;
        this.columna = columna;
        this.idToken = 0;
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
