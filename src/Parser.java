import java.util.ArrayList;
import java.util.List;

public class Parser {
    private int posicionActual;
    private boolean modoPanico = false;
    private Token tokenActual;
    private List<Token> tokens;
    private List<ErrorLexico> erroresSintacticos;

    // cajas que sirven para guardar los datos utiles
    private List<Agente> listaAgentes;
    private List<Token> ejecuciones;
    private List<Token> exportaciones;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.erroresSintacticos = new ArrayList<>();
        this.listaAgentes = new ArrayList<>();
        this.ejecuciones = new ArrayList<>();
        this.exportaciones = new ArrayList<>();
        this.posicionActual = -1;
        avanzar();
    }

    private void avanzar(){
        posicionActual++;
        if (posicionActual < tokens.size()){
            tokenActual = tokens.get(posicionActual);
        }else{
            tokenActual = new Token("EOF", "FIN",0,0);
        }
    }

    private void match(String tipoEsperado){
        if (tokenActual.getTipo().equals(tipoEsperado)){
            modoPanico = false;
            avanzar();
        }else{
            if (modoPanico == false){
                String descripcionError = String.format("Se esperaba '%s' pero encontro '%s'", tipoEsperado, tokenActual.getTipo());

                ErrorLexico nuevoError = new ErrorLexico(tokenActual.getLexema(), descripcionError, tokenActual.getFila(), tokenActual.getColumna());

                erroresSintacticos.add(nuevoError);
                modoPanico = true;
            }
            recuperacionModoPanico();
        }
    }

    private void recuperacionModoPanico(){
        if (esTokenValido(tokenActual.getTipo()) || tokenActual.getTipo().equals("EOF")){
            return;
        }
        // dejamos los errores atras y avanzamos
        avanzar();
        while (!esTokenValido(tokenActual.getTipo()) && !tokenActual.getTipo().equals("EOF")) {
            avanzar();
        }
    }

    private boolean esTokenValido(String tipo) {
        return tipo.equals("AGENTE") ||
                tipo.equals("LLAVE_C") ||
                tipo.equals("COMANDO_IA") ||
                tipo.equals("VARIABLE") ||
                tipo.equals("EJECUTAR") ||
                tipo.equals("EXPORTAR");
    }

    public void parsePrograma() {
        parseDirectivas();
        parseAgentes();
        parseEjecuciones();
        parseExportar();
        match("EOF");
    }

    private void parseDirectivas() {
        if(tokenActual.getTipo().equals("DIRECTIVA")){

            match("DIRECTIVA");
            match("CADENA");

            parseDirectivas();
        }else {
            return;
        }
    }

    private void parseAgentes() {
        parseAgente();
        parseMasAgentes();
    }

    private void parseMasAgentes(){
        if (tokenActual.getTipo().equals("AGENTE")){
            parseAgente();
            parseMasAgentes();
        }else{
            return;
        }
    }

    private void parseAgente(){
        match("AGENTE");

        //guardamos el dato antes de seguir avanzando
        String nombreAgente = tokenActual.getLexema();
        int fila = tokenActual.getFila();
        int columna = tokenActual.getColumna();
        match("ID");

        //creamos el circulo inicial
        Agente nuevoAgente = new Agente(nombreAgente, fila, columna);

        match("LLAVE_A");

        parseContexto(nuevoAgente);
        parseVariables(nuevoAgente);
        parseComandos(nuevoAgente);

        match("LLAVE_C");

        listaAgentes.add(nuevoAgente);
    }

    private void parseContexto(Agente agenteActual){
        match("CONTEXTO");
        match("IGUAL");

        //como solo es texto lo guardamos en un string
        String textoContexto = tokenActual.getLexema();
        match("CADENA");

        //Lo guardamos en la memoria del agente
        agenteActual.setContexto(textoContexto);
    }

    private void parseVariables(Agente agenteActual){
        if (tokenActual.getTipo().equals("VARIABLE")){
            match("VARIABLE");

            //atrapamos el nombre de la variable
            String nombreVariable = tokenActual.getLexema();
            int fila = tokenActual.getFila();
            int columna = tokenActual.getColumna();
            match("ID");

            match("IGUAL");

            //guardadr <expresion> en un string
            String valorVariable = parseExpresion();

            // se tiene el nombreVariable y valorVariable los guardamos
            Variable nuevaVariable = new Variable(nombreVariable, valorVariable, fila, columna);
            agenteActual.agregarVariable(nuevaVariable);

            parseVariables(agenteActual);
        }else{
            return;
        }
    }

    private void parseComandos(Agente agenteActual){
        parseComando(agenteActual);
        parseMasComandos(agenteActual);
    }

    private void parseMasComandos(Agente agenteActual){
        if (tokenActual.getTipo().equals("COMANDO_IA")){
            parseComando(agenteActual);
            parseMasComandos(agenteActual);
        }else {
            return;
        }
    }

    private void parseComando(Agente agenteActual){
        String tipoComando = tokenActual.getLexema();
        int fila = tokenActual.getFila();
        int columna = tokenActual.getColumna();
        match("COMANDO_IA");

        // Recibimos los textos armados
        String expresion = parseExpresion();
        String conectores = parseConectores();

        match("FLECHA");

        String destino = tokenActual.getLexema();
        match("ID");

        // Creamos el comando y lo guardamos en el agente
        Comando nuevoComando = new Comando(tipoComando, expresion, conectores, destino, fila, columna);
        agenteActual.agregarComando(nuevoComando);
    }

    private String parseConectores(){
        if (tokenActual.getTipo().equals("CONECTOR")){
            String conector = tokenActual.getLexema();
            match("CONECTOR");

            String expresion = parseExpresion();
            String masConectores = parseConectores();

            // devolvemos el texto armado con espacios
            return conector + " " + expresion + " " + masConectores;
        }else {
            return "";
        }
    }

    private String parseExpresion(){
        String termino = parseTermino();
        String masTerminos = parseMasTerminos();

        return termino + masTerminos;
    }

    private String parseMasTerminos(){
        if (tokenActual.getTipo().equals("MAS")){
            match("MAS");
            String termino = parseTermino();
            String masTerminos = parseMasTerminos();
            return " + " + termino + masTerminos;
        }else{
            return "";
        }
    }

    private String parseTermino(){
        String valor = "";

        if (tokenActual.getTipo().equals("CADENA")) {
            valor = tokenActual.getLexema(); // Atrapamos el texto
            match("CADENA");

        } else if (tokenActual.getTipo().equals("ID")) {
            valor = tokenActual.getLexema(); // Atrapamos la palabra
            match("ID");

        } else if (tokenActual.getTipo().equals("NUMERO")) {
            valor = tokenActual.getLexema(); // Atrapamos el número
            match("NUMERO");
            valor = parseUnidad() + valor;

        } else if (tokenActual.getTipo().equals("CARGAR")) {
            valor = parseCargar(); // Recibimos toda la instrucción cargar

        } else {
            match("TERMINO_INVALIDO");
        }

        return valor;
    }

    private String parseUnidad(){
        if (tokenActual.getTipo().equals("ID")) {
            String unidad = tokenActual.getLexema();
            match("ID");
            return " " + unidad;
        } else {
            return "";
        }
    }

    private String parseCargar(){
        String resultado = "CARGAR(";
        match("CARGAR");
        match("PAR_A");

        resultado += tokenActual.getLexema(); // Atrapamos el nombre del archivo
        match("CADENA");

        match("PAR_C");
        resultado += ")";

        return resultado;
    }

    private void parseEjecuciones() {
        match("EJECUTAR");
        Token tokenEjecutar = tokenActual;
        match("ID");
        ejecuciones.add(tokenEjecutar);

        parseMasEjecuciones();
    }

    private void parseMasEjecuciones(){
        if (tokenActual.getTipo().equals("EJECUTAR")){
            match("EJECUTAR");

            Token tokenEjecutar = tokenActual;
            match("ID");
            ejecuciones.add(tokenEjecutar);

            parseMasEjecuciones();
        }else{
            return;
        }
    }

    private void parseExportar() {
        match("EXPORTAR");
        Token tokenExportar = tokenActual;
        match("ID");
        exportaciones.add(tokenExportar);

        parseMasIds();
    }

    private void parseMasIds(){
        if (tokenActual.getTipo().equals("COMA")){
            match("COMA");
            Token tokenExportar = tokenActual;
            match("ID");
            exportaciones.add(tokenExportar);

            parseMasIds();
        }else {
            return;
        }
    }

    public List<Agente> getListaAgentes() { return listaAgentes; }
    public List<Token> getEjecuciones() { return ejecuciones; }
    public List<Token> getExportaciones() { return exportaciones; }
    public List<ErrorLexico> getErroresSintacticos() {
        return erroresSintacticos;
    }
}
