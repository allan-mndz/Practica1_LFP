import java.util.ArrayList;
import java.util.List;

public class Parser {
    private int posicionActual;
    private Token tokenActual;

    private List<Token> tokens;
    private List<ErrorLexico> erroresSintacticos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.erroresSintacticos = new ArrayList<>();
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
            avanzar();
        }else{
            String descripcionError = String.format("Se esperaba '%s' pero encontro '%s'", tipoEsperado, tokenActual.getTipo());

            ErrorLexico nuevoError = new ErrorLexico(tokenActual.getLexema(), descripcionError, tokenActual.getFila(), tokenActual.getColumna());

            erroresSintacticos.add(nuevoError);
            recuperacionModoPanico();
        }
    }

    private void recuperacionModoPanico(){
        avanzar();

        while (!tokenActual.getTipo().equals("AGENTE") &&
                !tokenActual.getTipo().equals("LLAVE_C") &&
                !tokenActual.getTipo().equals("COMANDO_IA") &&
                !tokenActual.getTipo().equals("VARIABLE") &&
                !tokenActual.getTipo().equals("EJECUTAR") &&
                !tokenActual.getTipo().equals("EXPORTAR") &&
                !tokenActual.getTipo().equals("EOF")) {

            avanzar();
        }
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
        match("ID");
        match("LLAVE_A");

        parseContexto();
        parseVariables();
        parseComandos();

        match("LLAVE_C");
    }

    private void parseContexto(){
        match("CONTEXTO");
        match("IGUAL");
        match("CADENA");
    }

    private void parseVariables(){
        if (tokenActual.getTipo().equals("VARIABLE")){
            match("VARIABLE");
            match("ID");
            match("IGUAL");

            parseExpresion();
            parseVariables();
        }else{
            return;
        }
    }

    private void parseComandos(){
        parseComando();
        parseMasComandos();
    }

    private void parseMasComandos(){
        if (tokenActual.getTipo().equals("COMANDO_IA")){
            parseComando();
            parseMasComandos();
        }else {
            return;
        }
    }

    private void parseComando(){
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
