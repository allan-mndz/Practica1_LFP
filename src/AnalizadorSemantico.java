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

            if (existeSimbolo(agente.getNombre(), "Global")) {
                erroresSemanticos.add(new ErrorLexico(agente.getNombre(), "Error Semántico: Ya existe un agente con este nombre", agente.getFila(), agente.getColumna()));
            } else {
                tablaSimbolos.add(new Simbolo(agente.getNombre(), "Agente", "Global", agente.getFila()));
            }

            for (Variable var : agente.getVariables()) {
                tablaSimbolos.add(new Simbolo(var.getNombre(), "Variable Declarada", agente.getNombre(), var.getFila()));
            }

            for (Comando cmd : agente.getComandos()) {
                tablaSimbolos.add(new Simbolo(cmd.getVariableDestino(), "Resultado de Comando", agente.getNombre(), cmd.getFila()));
            }
        }

        for (Token ejecucion : ejecuciones) {
            if (!existeSimbolo(ejecucion.getLexema(), "Global")) {
                erroresSemanticos.add(new ErrorLexico(ejecucion.getLexema(), "Error Semántico: Se intenta EJECUTAR un agente que no existe", ejecucion.getFila(), ejecucion.getColumna()));
            }
        }

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

    // Buscador auxiliar en nuestra tabla de símbolos
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