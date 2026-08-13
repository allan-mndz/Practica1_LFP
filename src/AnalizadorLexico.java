import java.util.ArrayList;
import java.util.List;

public class AnalizadorLexico {
    private String codigo;
    private int posicion;
    private int fila;
    private int columna;
    private List<Token> listaTokens;
    private List<ErrorLexico> listaErrores;
    private int idToken;

    public AnalizadorLexico(String codigo) {
        this.codigo = codigo;
        this.posicion = 0;
        this.fila = 1;
        this.columna = 1;
        this.listaTokens = new ArrayList<>();
        this.listaErrores = new ArrayList<>();
        this.idToken = 0;
    }

    public void analizar(){
        int longitud = codigo.length(); // nos dice cuantas letras tiene el archivo en total

        while(posicion < longitud){
            // el char almacena un unico caracter
            char c = codigo.charAt(posicion); // saca la letra actual y la guarda en la variable

            if(c == '\n'){ // saltos de linea
                fila++;
                columna = 1;
                posicion++;
                continue;
            }else if(c == '\r'){
                posicion++;
                continue;
            }else if(c == '\t' || c == ' '){ // tabulaciones o espacios
                columna++;
                posicion++;
                continue;
            }

            if(Character.isLetter(c) || c == '_'){ // si vemos una letra o un guion bajo
                extraerPalabra();
            } else if (c == '@') { // para las directivas
                extraerDirectiva();
            } else if (c == '"') { // para las cadenas de texto
                extraerCadena();
            } else if (Character.isDigit(c)) { // para numero enteros y decimales
                extraerNumero();
            } else if (c == '{' || c == '}' || c == '(' || c == ')' || c == ',' || c == '=' || c == '+') { //si la letra es uno de estos simbolos que entre
                extraerSimboloUnico(c);
            } else if (c == '-') { // sirve para guiar al programa si es un error o es la primera mitdas de la flecha ->
                extraerFlechaOGuion();
            } else if (c == '/') { // sirve para guiar al programa si es un comentario, un multilinea o un error
                extraerComentarioODivision();
            } else {
                listaErrores.add(new ErrorLexico(String.valueOf(c), "Carácter no reconocido", fila, columna));
                posicion++;
                columna++;
            }
        }
    }

    private void extraerPalabra() {
        int columnaInicio = columna; // sirve para recordar en que columna inicia las palabras o el -
        String lexema = "";          // variable de texto vacia para ir guardando letra por letra
        int longitud = codigo.length();

        while(posicion < longitud){
            char actual  = codigo.charAt(posicion);

            //ve si es una letra del abededario o un numero
            if (Character.isLetterOrDigit(actual) || actual == '_') {
                lexema = lexema + actual;
                posicion++;
                columna++;
            }else {
                break;
            }
        }

        String tipoToken = "Identificador";


        //comparamos si la palabra del lexema es igual a "......"
        if (lexema.equals("AGENTE") || lexema.equals("contexto") || lexema.equals("variable") || lexema.equals("EJECUTAR") || lexema.equals("EXPORTAR")) {

            tipoToken = "Palabra reservada";

        } else if (lexema.equals("PREGUNTAR") || lexema.equals("GENERAR") || lexema.equals("RESUMIR") || lexema.equals("ANALIZAR") || lexema.equals("TRADUCIR") ||
                    lexema.equals("CLASIFICAR") || lexema.equals("EXTRAER") || lexema.equals("CARGAR")) {

            tipoToken = "Comando de IA";

        } else if (lexema.equals("SOBRE") || lexema.equals("DESDE") || lexema.equals("EN") || lexema.equals("COMO")) {

            tipoToken = "Conector";

        }

        listaTokens.add(new Token(idToken, lexema, tipoToken, fila, columnaInicio)); // guardamos el token en la lista "oficial"
        idToken++;
    }

    private void extraerDirectiva(){
        int columnaInicio = columna; // sirve para recordar en que columna empezo la palabra con el @
        String lexema = "";
        int longitud = codigo.length();

        // para el arroba inicial
        lexema = lexema + codigo.charAt(posicion);
        posicion++;
        columna++;

        //mientras haya letras se sigue avanzando
        while(posicion < longitud && Character.isLetter(codigo.charAt(posicion))){
            lexema = lexema + codigo.charAt(posicion);
            posicion++;
            columna++;
        }

        if (lexema.equals("@modelo") || lexema.equals("@rol") || lexema.equals("@formato")){
            listaTokens.add(new Token(idToken, lexema, "Directiva", fila, columnaInicio));
            idToken++;
        }else{
            listaErrores.add(new ErrorLexico(lexema, "Directiva no reconocida", fila, columnaInicio));
        }
    }

    private void extraerCadena(){

    }

    private void extraerNumero(){

    }

    private void extraerSimboloUnico(char c){

    }

    private void extraerFlechaOGuion(){

    }

    private void extraerComentarioODivision(){

    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
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

    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public void setListaTokens(List<Token> listaTokens) {
        this.listaTokens = listaTokens;
    }

    public List<ErrorLexico> getListaErrores() {
        return listaErrores;
    }

    public void setListaErrores(List<ErrorLexico> listaErrores) {
        this.listaErrores = listaErrores;
    }

    public int getIdToken() {
        return idToken;
    }

    public void setIdToken(int idToken) {
        this.idToken = idToken;
    }
}
