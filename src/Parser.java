import java.util.ArrayList;
import java.util.List;

public class Parser {
    private int posicionActual;
    private Token tokenActual;

    private List<Token> tokens;
    private List<ErrorLexico> erroresSintacticos;
    private List<Agente> listaAgentes;

    private boolean modoPanico = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.erroresSintacticos = new ArrayList<>();
        this.listaAgentes = new ArrayList<>();
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
            if (!modoPanico){
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
        match("ID");

        //creamos el circulo inicial
        Agente nuevoAgente = new Agente(nombreAgente);

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

        //Atrapamos el texto del contexto antes de avanzar
        String textoContexto = tokenActual.getLexema();
        match("CADENA");

        //Lo guardamos en la memoria del agente
        agenteActual.setContexto(textoContexto);
    }

    private void parseVariables(Agente nuevoAgente){
        if (tokenActual.getTipo().equals("VARIABLE")){
            match("VARIABLE");
            match("ID");
            match("IGUAL");

            parseExpresion();
            parseVariables(nuevoAgente);
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
        match("COMANDO_IA");

        parseExpresion();
        parseConectores();

        match("FLECHA");
        match("ID");
    }

    private void parseConectores(){
        if (tokenActual.getTipo().equals("CONECTOR")){
            match("CONECTOR");
            parseExpresion();
            parseConectores();
        }else {
            return;
        }
    }

    private void parseExpresion(){
            parseTermino();
            parseMasTerminos();
    }

    private void parseMasTerminos(){
        if (tokenActual.getTipo().equals("MAS")){
            match("MAS");
            parseTermino();
            parseMasTerminos();
        }else{
            return;
        }
    }

    private void parseTermino(){
        if (tokenActual.getTipo().equals("CADENA")){
            match("CADENA");
        } else if (tokenActual.getTipo().equals("ID")) {
            match("ID");
        } else if (tokenActual.getTipo().equals("NUMERO")) {
            match("NUMERO");
            parseUnidad();
        } else if (tokenActual.getTipo().equals("CARGAR")) {
            parseCargar();
        } else{
            match("TERMINO_VALIDO");
        }
    }

    private void parseUnidad(){
        if (tokenActual.getTipo().equals("ID")){
            match("ID");
        }else {
            return;
        }
    }

    private void parseCargar(){
        match("CARGAR");
        match("PAR_A");
        match("CADENA");
        match("PAR_C");
    }

    private void parseEjecuciones() {
        match("EJECUTAR");
        match("ID");

        parseMasEjecuciones();
    }

    private void parseMasEjecuciones(){
        if (tokenActual.getTipo().equals("EJECUTAR")){
            match("EJECUTAR");
            match("ID");

            parseMasEjecuciones();
        }else{
            return;
        }
    }

    private void parseExportar() {
        match("EXPORTAR");
        match("ID");

        parseMasIds();
    }

    private void parseMasIds(){
        if (tokenActual.getTipo().equals("COMA")){
            match("COMA");
            match("ID");

            parseMasIds();
        }else {
            return;
        }
    }

    public List<ErrorLexico> getErroresSintacticos() {
        return erroresSintacticos;
    }
}
