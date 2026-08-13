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

        }catch(IOException e){
            System.out.println("Error al leer archivo");
        }finally{
            sc.close();
        }
    }

}
