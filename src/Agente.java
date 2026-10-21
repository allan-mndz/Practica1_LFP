import java.util.ArrayList;
import java.util.List;

public class Agente {
    private String nombre;
    private String contexto;
    private int fila;
    private int columna;


    private List<Variable> variables;
    private List<Comando> comandos;

    public Agente(String nombre, int fila, int columna) {
        this.nombre = nombre;
        this.contexto = "";
        this.fila = fila;
        this.columna = columna;
        this.variables = new ArrayList<>();
        this.comandos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    public List<Comando> getComandos() {
        return comandos;
    }

    public void setComandos(List<Comando> comandos) {
        this.comandos = comandos;
    }

    public void agregarVariable(Variable nuevaVariable) {
        this.variables.add(nuevaVariable);
    }

    public void agregarComando(Comando nuevoComando) {
        this.comandos.add(nuevoComando);
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
}
