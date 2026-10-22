import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    public void generarArchivo(String rutaCarpeta){
        try {
            File archivoSalida = new File(rutaCarpeta + File.separator + "PromptZal_Final.md");
            FileWriter writer = new FileWriter(archivoSalida);

            writer.write("# Prompt generado con PromptZal\n\n");

            // Recorremos solo los agentes que el usuario pidio
            for (Token tokenEjecutar : ejecuciones) {
                String nombreAgenteEjecutado = tokenEjecutar.getLexema();

                // Buscamos ese agente en nuestra lista del AST
                Agente agenteEncontrado = null;
                for (Agente a : listaAgentes) {
                    if (a.getNombre().equals(nombreAgenteEjecutado)) {
                        agenteEncontrado = a;
                        break;
                    }
                }

                // Si encontramos traducimos sus datos a texto
                if (agenteEncontrado != null) {
                    writer.write("## Agente: " + agenteEncontrado.getNombre() + "\n");
                    writer.write("**Contexto:** " + agenteEncontrado.getContexto() + "\n\n");

                    writer.write("**Variables disponibles:**\n");
                    for (Variable var : agenteEncontrado.getVariables()) {
                        writer.write("- " + var.getNombre() + ": " + var.getValor() + "\n");
                    }
                    writer.write("\n");

                    writer.write("**Instrucciones:**\n");
                    for (Comando cmd : agenteEncontrado.getComandos()) {
                        writer.write("- " + cmd.getTipoComando() + " " + cmd.getArgumentoPrincipal());
                        if (!cmd.getConectores().trim().isEmpty()) {
                            writer.write(" " + cmd.getConectores());
                        }
                        writer.write(" (Guarda el resultado como: `" + cmd.getVariableDestino() + "`)\n");
                    }
                    writer.write("\n---\n\n");
                }
            }

            // Agregamos las exportaciones al final
            writer.write("## Resultado solicitado\n");
            for (Token tokenExportar : exportaciones) {
                writer.write("- `" + tokenExportar.getLexema() + "`\n");
            }

            writer.close();
            System.out.println("Archivo PromptZal generado exitosamente en: " + archivoSalida.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Error al generar el archivo: " + e.getMessage());
        }
    }
}
