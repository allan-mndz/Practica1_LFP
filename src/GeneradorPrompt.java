import java.util.List;

public class GeneradorPrompt {
    private List<Agente> listaAgentes;
    private List<Token> ejecuciones;
    private List<Token> exportaciones;

    public GeneradorPrompt(List<Agente> listaAgentes, List<Token> ejecuciones, List<Token> exportaciones) {
        this.listaAgentes = listaAgentes;
        this.ejecuciones = ejecuciones;
        this.exportaciones = exportaciones;
    }

    public String generarTexto() {
        StringBuilder texto = new StringBuilder();
        texto.append("# Prompt generado con PromptZal\n\n");

        for (Token tokenEjecutar : ejecuciones) {
            String nombreAgenteEjecutado = tokenEjecutar.getLexema();
            Agente agenteEncontrado = null;
            for (Agente a : listaAgentes) {
                if (a.getNombre().equals(nombreAgenteEjecutado)) {
                    agenteEncontrado = a;
                    break;
                }
            }

            if (agenteEncontrado != null) {
                texto.append("## Agente: ").append(agenteEncontrado.getNombre()).append("\n");
                texto.append("**Contexto:** ").append(agenteEncontrado.getContexto()).append("\n\n");

                texto.append("**Variables disponibles:**\n");
                for (Variable var : agenteEncontrado.getVariables()) {
                    texto.append("- ").append(var.getNombre()).append(": ").append(var.getValor()).append("\n");
                }
                texto.append("\n");

                texto.append("**Instrucciones:**\n");
                for (Comando cmd : agenteEncontrado.getComandos()) {
                    texto.append("- ").append(cmd.getTipoComando()).append(" ").append(cmd.getArgumentoPrincipal());
                    if (!cmd.getConectores().trim().isEmpty()) {
                        texto.append(" ").append(cmd.getConectores());
                    }
                    texto.append(" (Guarda el resultado como: `").append(cmd.getVariableDestino()).append("`)\n");
                }
                texto.append("\n---\n\n");
            }
        }

        texto.append("## Resultado solicitado\n");
        for (Token tokenExportar : exportaciones) {
            texto.append("- `").append(tokenExportar.getLexema()).append("`\n");
        }

        return texto.toString();
    }
}