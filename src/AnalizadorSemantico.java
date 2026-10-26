import java.util.ArrayList;
import java.util.List;

public class AnalizadorSemantico {
    private List<Agente> listaAgentes;
    private List<Token> ejecuciones;
    private List<Token> exportaciones;

    private List<Simbolo> tablaSimbolos;
    private List<ErrorLexico> erroresSemanticos;

    public AnalizadorSemantico(List<Agente> agentes, List<Token> ejecuciones, List<Token> exportaciones) {
        this.listaAgentes = agentes;
        this.ejecuciones = ejecuciones;
        this.exportaciones = exportaciones;
        this.tablaSimbolos = new ArrayList<>();
        this.erroresSemanticos = new ArrayList<>();
    }

    public void analizar() {
        for (Agente agente : listaAgentes) {

            //registrar agente
            if (existeSimbolo(agente.getNombre(), "Global")) {
                erroresSemanticos.add(new ErrorLexico(agente.getNombre(), "Error Semántico: Ya existe un agente con este nombre", agente.getFila(), agente.getColumna()));
            } else {
                tablaSimbolos.add(new Simbolo(agente.getNombre(), "Agente", "Global", agente.getFila()));
            }

            //valida y registrar variables
            for (Variable var : agente.getVariables()) {
                validarIdentificadoresEnTexto(var.getValor(), agente.getNombre(), var.getFila(), var.getColumna(), "");
                tablaSimbolos.add(new Simbolo(var.getNombre(), "Variable Declarada", agente.getNombre(), var.getFila()));
            }

            //validar y registrar comandos
            for (Comando cmd : agente.getComandos()) {
                validarIdentificadoresEnTexto(cmd.getArgumentoPrincipal(), agente.getNombre(), cmd.getFila(), cmd.getColumna(), cmd.getTipoComando());
                validarIdentificadoresEnTexto(cmd.getConectores(), agente.getNombre(), cmd.getFila(), cmd.getColumna(), "");
                tablaSimbolos.add(new Simbolo(cmd.getVariableDestino(), "Resultado de Comando", agente.getNombre(), cmd.getFila()));
            }
        }

        //validar ejecuciones
        for (Token ejecucion : ejecuciones) {
            if (!existeSimbolo(ejecucion.getLexema(), "Global")) {
                erroresSemanticos.add(new ErrorLexico(ejecucion.getLexema(), "Error Semántico: Se intenta EJECUTAR un agente que no existe", ejecucion.getFila(), ejecucion.getColumna()));
            }
        }

        //validadr exportaciones
        for (Token exportacion : exportaciones) {
            boolean existe = false;
            for (Token ejecucion : ejecuciones) {
                if (existeSimbolo(exportacion.getLexema(), ejecucion.getLexema())) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                erroresSemanticos.add(new ErrorLexico(exportacion.getLexema(), "Error Semántico: Se intenta EXPORTAR una variable que no existe en los agentes ejecutados", exportacion.getFila(), exportacion.getColumna()));
            }
        }
    }

    private void validarIdentificadoresEnTexto(String texto, String nombreAgente, int fila, int columna, String tipoComando) {
        if (texto == null || texto.trim().isEmpty()) return;

        // 1. ignoramos todo el texto que este entre comillas
        texto = texto.replaceAll("\".*?\"", "");

        // 2. Limpiamos espacios extra y separamos por palabras
        String[] palabras = texto.trim().split("\\s+");
        boolean ignorarSiguiente = false;

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];

            // Si quedo un espacio vacío tras borrar las comillas, lo saltamos
            if (palabra.isEmpty()) continue;

            // Si es GENERAR, el primer argumento no es variable
            if (i == 0 && tipoComando.equals("GENERAR")) continue;

            // Si la palabra anterior era un numero, esta es una <unidad> y se ignora
            if (ignorarSiguiente) {
                ignorarSiguiente = false;
                continue;
            }

            // Si detectamos un numero, marcamos para ignorar la unidad que le sigue
            if (palabra.matches("\\d+(\\.\\d+)?")) {
                ignorarSiguiente = true;
                continue;
            }

            // Ignoramos la instrucción CARGAR, conectores o el signo +
            if (palabra.startsWith("CARGAR") || esConector(palabra)) {
                continue;
            }

            // Si supero todos los filtros, es un ID real
            if (!existeSimbolo(palabra, nombreAgente)) {
                erroresSemanticos.add(new ErrorLexico(palabra, "Error Semántico: El identificador '" + palabra + "' no ha sido declarado antes de su uso.", fila, columna));
            }
        }
    }

    private boolean esConector(String palabra) {
        String p = palabra.toUpperCase();
        return p.equals("SOBRE") || p.equals("DESDE") || p.equals("EN") || p.equals("COMO") || p.equals("+");
    }

    private boolean existeSimbolo(String nombre, String ambito) {
        for (Simbolo s : tablaSimbolos) {
            if (s.getIdentificador().equals(nombre) && s.getAmbito().equals(ambito)) {
                return true;
            }
        }
        return false;
    }

    public List<Simbolo> getTablaSimbolos() {
        return tablaSimbolos;
    }

    public List<ErrorLexico> getErroresSemanticos() {
        return erroresSemanticos;
    }
}