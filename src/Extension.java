import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.List;

public class Extension {

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        System.out.println("Analizadodr Lexico PromptZal");
        System.out.println("Ingrese su archivo .pz");

        String rutaArchivo = sc.nextLine();

        if(!rutaArchivo.endsWith(".pz")){ // si la ruta no termina en pz, muestra el error
            System.out.println("Archivo no encontrado");
            return;
        }
        try{
            Path path = Paths.get(rutaArchivo); // es como un mapa que java usa para encontrar el archivo en la computadora
            String codigoFuente = Files.readString(path); // abre el archivo, copiatodo el texto que tiene dentro y lo guarda en una variable
            System.out.println("archivo encontrado");

            AnalizadorLexico analizador = new AnalizadorLexico(codigoFuente);
            analizador.analizar();

            List<Token> listaTokens = analizador.getListaTokens();
            List<ErrorLexico> listaErrores = analizador.getListaErrores();

            System.out.println("\n=======================================================");
            System.out.println("               TABLA DE TOKENS                           ");
            System.out.println("=========================================================");
            for (Token token : listaTokens) { // por cada token que exista dentro de mi "libreta" listaTokens, haz lo siguiente:
                System.out.println(token.toString()); // Usamos el toString() (del objeto token) para que imprima en una sola linea alineada con todos sus datos
            }

            if (!listaErrores.isEmpty()) { // si no esta vacia muestra los errores
                System.out.println("\n=======================================================");
                System.out.println("                   ERRORES ENCONTRADOS                 ");
                System.out.println("=======================================================");
                for (ErrorLexico error : listaErrores) {
                    System.out.println(error.toString());
                }
            }

            System.out.println("\n--- Resumen Final ---");
            System.out.println("Total de Tokens válidos: " + listaTokens.size()); //.size deuvleve el numero exacto de elementos guardados
            System.out.println("Total de Errores léxicos: " + listaErrores.size());
            System.out.println("Generando reportes...");

            generarReportesHTML(listaTokens, listaErrores);


        }catch(IOException e){
            System.out.println("Error al leer archivo");
        }finally{
            sc.close();
        }
    }

    public static void generarReportesHTML(List<Token> listaTokens, List<ErrorLexico> listaErrores) {
        try {
            // 1. GENERAR REPORTE DE TOKENS
            StringBuilder htmlTokens = new StringBuilder();
            htmlTokens.append("<html><head><title>Reporte de Tokens</title>\n");
            // Agregamos un poco de CSS
            htmlTokens.append("<style>table {width: 80%; margin: 20px auto; border-collapse: collapse; font-family: sans-serif;} \n");
            htmlTokens.append("th, td {border: 1px solid #dddddd; padding: 8px; text-align: left;} \n");
            htmlTokens.append("th {background-color: #f2f2f2;}</style></head><body>\n");
            htmlTokens.append("<h2 style='text-align:center;'>Reporte de Tokens Válidos - PromptZal</h2>\n");
            htmlTokens.append("<table><tr><th>#</th><th>Lexema</th><th>Tipo de Token</th><th>Fila</th><th>Columna</th></tr>\n");

            // Recorremos la lista y creamos una fila <tr> por cada token
            for (Token t : listaTokens) {
                htmlTokens.append("<tr><td>").append(t.getId()).append("</td>")
                        .append("<td>").append(t.getLexema()).append("</td>")
                        .append("<td>").append(t.getTipo()).append("</td>")
                        .append("<td>").append(t.getFila()).append("</td>")
                        .append("<td>").append(t.getColumna()).append("</td></tr>\n");
            }
            htmlTokens.append("</table></body></html>");

            // Guardamos el archivo en la carpeta del proyecto
            Files.writeString(Paths.get("Reporte_Tokens.html"), htmlTokens.toString());
            System.out.println("Reporte de Tokens generado: Reporte_Tokens.html");


            // 2. GENERAR REPORTE DE ERRORES LÉXICOS
            StringBuilder htmlErrores = new StringBuilder();
            htmlErrores.append("<html><head><title>Reporte de Errores</title>\n");
            htmlErrores.append("<style>table {width: 80%; margin: 20px auto; border-collapse: collapse; font-family: sans-serif;} \n");
            htmlErrores.append("th, td {border: 1px solid #dddddd; padding: 8px; text-align: left;} \n");
            htmlErrores.append("th {background-color: #ffcccc;}</style></head><body>\n");
            htmlErrores.append("<h2 style='text-align:center;'>Reporte de Errores Léxicos - PromptZal</h2>\n");

            // para indicar explícitamente si no hay errores
            if (listaErrores.isEmpty()) {
                htmlErrores.append("<h3 style='text-align:center; color:green;'>No se encontraron errores léxicos en el archivo analizado.</h3>\n");
            } else {
                htmlErrores.append("<table><tr><th>Carácter/Lexema</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>\n");
                for (ErrorLexico e : listaErrores) {
                    htmlErrores.append("<tr><td>").append(e.getLexema()).append("</td>")
                            .append("<td>").append(e.getDescripcion()).append("</td>")
                            .append("<td>").append(e.getFila()).append("</td>")
                            .append("<td>").append(e.getColumna()).append("</td></tr>\n");
                }
                htmlErrores.append("</table>\n");
            }
            htmlErrores.append("</body></html>");

            Files.writeString(Paths.get("Reporte_Errores.html"), htmlErrores.toString());
            System.out.println("Reporte de Errores generado: Reporte_Errores.html");

        } catch (IOException e) {
            System.out.println("Ocurrió un error al generar los reportes HTML: " + e.getMessage());
        }
    }
}
