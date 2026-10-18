public class Comando {
    private String tipoComando;        // PREGUNTAR, RESUMIR, GENERAR
    private String argumentoPrincipal; // Para comandos ia
    private String conectores;         // Conectores xd
    private String variableDestino;    // (lo que va después de ->)

    public Comando(String tipoComando, String argumentoPrincipal, String conectores, String variableDestino) {
        this.tipoComando = tipoComando;
        this.argumentoPrincipal = argumentoPrincipal;
        this.conectores = conectores;
        this.variableDestino = variableDestino;
    }

    public String getTipoComando() {
        return tipoComando;
    }

    public void setTipoComando(String tipoComando) {
        this.tipoComando = tipoComando;
    }

    public String getArgumentoPrincipal() {
        return argumentoPrincipal;
    }

    public void setArgumentoPrincipal(String argumentoPrincipal) {
        this.argumentoPrincipal = argumentoPrincipal;
    }

    public String getConectores() {
        return conectores;
    }

    public void setConectores(String conectores) {
        this.conectores = conectores;
    }

    public String getVariableDestino() {
        return variableDestino;
    }

    public void setVariableDestino(String variableDestino) {
        this.variableDestino = variableDestino;
    }
}
