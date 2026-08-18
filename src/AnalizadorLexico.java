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
        String lexema = "";          // variable de texto vacio para ir guardando letra por letra
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

        lexema = lexema + codigo.charAt(posicion); //va armando la palabra letra por letra
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
        int columnaInicio =  columna;
        String lexema = "";
        int longitud = codigo.length();

        //para la comilla inicial
        lexema = lexema + codigo.charAt(posicion);
        posicion++;
        columna++;

        //leemos todo hasta enoontrar el cierre comilla
        while (posicion < longitud && codigo.charAt(posicion) != '"'){

            //extraemos la letra actual para usarla en diferentes cuestiones
            char actual = codigo.charAt(posicion);
            lexema = lexema + actual;

            //por si el usuario dio enter dentro del texto
            if (actual == '\n') {
                fila++;
                columna = 1;
            } else {
                columna++;
            }
            posicion++;
        }

        if (posicion < longitud && codigo.charAt(posicion) == '"') { //se detiene porque encontro la comilla final
            lexema = lexema + '"';
            posicion++;
            columna++;

            listaTokens.add(new Token(idToken, lexema, "Literal de Cadena", fila, columnaInicio));
            idToken++;
        }else {
            listaErrores.add(new ErrorLexico(lexema, "Cadena sin cerrar", fila, columnaInicio));
        }
    }

    private void extraerNumero(){
        int columnaInicio = columna;
        String lexema = "";
        boolean tienePunto = false;
        int longitud = codigo.length();

        while(posicion < longitud){
            char actual = codigo.charAt(posicion);

            if(Character.isDigit(actual)){ //si la letra actual es un digito del 0-9 la agregamos y seguimos avanzando
                lexema = lexema + actual;
                posicion++;
                columna++;
            } else if (actual == '.' && !tienePunto) {// si tiene punto el tienePunto se activa
                lexema = lexema +  actual;
                tienePunto = true;
                posicion++;
                columna++;
            }else{
                break;
            }
        }

        if(tienePunto){
            listaTokens.add(new Token(idToken, lexema, "Literal Decimal", fila, columnaInicio));
        }else {
            listaTokens.add(new Token(idToken, lexema, "Literal Entero", fila, columnaInicio));
        }
        idToken++;
    }

    private void extraerSimboloUnico(char c){ //recibe el simobolo exacto que mando el metodo principal
        String lexema = String.valueOf(c); //toma el simbolo suelto y lo introduce al texto
        String tipo;
        if (c == '=' || c == '+'){
            tipo = "Operador";
        }else{
            tipo = "Delimitador";
        }

        listaTokens.add(new Token(idToken, lexema, tipo, fila, columna));
        idToken++;
        posicion++;
        columna++;
    }

    private void extraerFlechaOGuion(){
        int longitud = codigo.length();
        //posicion + 1 sirve para ver que hay en el siguiente espacio
        if (posicion + 1 < longitud && codigo.charAt(posicion + 1) == '>') {

            listaTokens.add(new Token(idToken, "->", "Conector", fila, columna));
            idToken++;
            //como leimos dos simbolos sumamos dos pasos hacia delante
            posicion +=2;
            columna += 2;
        }else {
            listaErrores.add(new ErrorLexico("-", "Carácter no reconocido", fila, columna));
            posicion++;
            columna++;
        }
    }

    private void extraerComentarioODivision(){
        int longitud = codigo.length();

        if (posicion + 1 < longitud && codigo.charAt(posicion + 1) == '/') { //verifica si es un comentario o un error
            while (posicion < longitud && codigo.charAt(posicion) != '\n') { // lee todo el comentario e ignora todo hasta que tope con un salto de linea
                posicion++;
                columna++;
                // No guardamos token, solo avanzamos
            }
        } else if (posicion + 1 < longitud && codigo.charAt(posicion + 1) == '*') { // en vez de un comentario es un /* significa que el texto tiene un comentario gigante
            posicion += 2;
            columna += 2;
            boolean cerrado = false; //sirve para encontrar el final del comentario

            while (posicion < longitud - 1) { //sirve para buscar el */ para poder salir
                char actual = codigo.charAt(posicion);
                char siguiente = codigo.charAt(posicion + 1);

                if (actual == '\n') {
                    fila++;
                    columna = 1;
                    posicion++;
                } else if (actual == '*' && siguiente == '/') { // Encontramos el cierre */
                    posicion += 2;
                    columna += 2;
                    cerrado = true;
                    break;
                } else {
                    posicion++;
                    columna++;
                }
            }

            if (!cerrado) { // Si se acabó el archivo y no lo cerraron
                // Forzamos el fin para no trabar el ciclo
                posicion = longitud;
            }
        } else {
            // Si solo era una '/', es un error
            listaErrores.add(new ErrorLexico("/", "Carácter no reconocido", fila, columna));
            posicion++;
            columna++;
        }
    }

    public List<Token> getListaTokens() {
        return listaTokens;
    }

    public List<ErrorLexico> getListaErrores() {
        return listaErrores;
    }
}
