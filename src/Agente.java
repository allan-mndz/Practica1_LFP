import java.util.ArrayList;
import java.util.List;

public class Agente {
    private String nombre;
    private String contexto;

    private List<Variable> variables;
    private List<Comando> comandos;

    public Agente(String nombre) {
        this.nombre = nombre;
        this.contexto = "";

        // Inicializamos las listas vacías
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
}
