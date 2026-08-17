import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
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
       
    }
}
