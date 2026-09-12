import javax.swing.*;
import java.io.File;
import java.io.FileWriter;

public class GeneradorAFD {

    public void crearAFD(String rutaCarpeta) {

        String codigoDOT = """
            digraph AFD {
                
                // flecha del estado inicial
                inicio [shape = point];
                
                // nodo del estado inicial
                S0 [shape = circle];
                
                // nodos del estado final
                S1 [shape = doublecircle];
                S2 [shape = doublecircle];
                S4 [shape = doublecircle];
                S6 [shape = doublecircle];
                S8 [shape = doublecircle];
                S11 [shape = doublecircle];
                S14 [shape = doublecircle];
                S16 [shape = doublecircle];
                S17 [shape = doublecircle];
                S18 [shape = doublecircle];
                S19 [shape = doublecircle];
                S20 [shape = doublecircle];
                S21 [shape = doublecircle];
                S22 [shape = doublecircle];
                S23 [shape = doublecircle];
                S24 [shape = doublecircle];
  
                // nodos normales
                S3 [shape = circle];
                S5 [shape = circle];
                S7 [shape = circle];
                S9 [shape = circle];
                S10 [shape = circle];
                S12 [shape = circle];
                S13 [shape = circle];
                S15 [shape = circle];
                
                // flecha de entrada
                inicio -> S0 [label = " inicio "];
                
                
                // identificadores, palabras reservadas y comandos
                
                S0 -> S1 [label = " Letra "];
                S1 -> S1 [label = " Letra, Digito, _ "];
                
                // Numeros enteros y decimales
                
                S0 -> S2 [label = " Digito "];
                S2 -> S2 [label = " Digito "];
                S2 -> S3 [label = " . "]
                S3 -> S4 [label = " Digito "];
                S4 -> S4 [label = " Digito "];
                
                // Directivas
                
                S0 -> S5 [label = " @ "];
                S5 -> S6 [label = " Letra "];
                S6 -> S6 [label = " Letra "];
                
                // Cadenas de texto
                
                S0 -> S7 [label = " Comilla doble "];
                S7 -> S7 [label = " Cualquier cosa "];
                S7 -> S8 [label = " Comilla doble "];
                
                // Comentarios
                
                S0 -> S9 [label = " / "];
                S9 -> S10 [label = " / "];
                S10 -> S10 [label = " Cualquier cosa "];
                S10 -> S11 [label = " Salto de linea (enter) "];
                
                // comentario de bloque
                
                S9 -> S12 [label = " * "];
                S12 -> S12 [label = " Cualquier cosa "];
                S12 -> S13 [label = " * "];
                S13 -> S12 [label = " Cualquier cosa "];
                S13 -> S14 [label = " / "];
                
                
                // conector con flecha
                
                S0 -> S15 [label = " - "];
                S15 -> S16 [label = " > "];
                
                // operadoes 
                
                S0 -> S17 [label = " = "];
                S0 -> S18 [label = " + "];
                
                // delimitadores
                
                S0 -> S19 [label = " { "];
                S0 -> S20 [label = " } "];
                S0 -> S21 [label = " ( "];
                S0 -> S22 [label = " ) "];
                S0 -> S23 [label = " , "];
                S0 -> S24 [label = " : "];
            }
            """;
        try {
            String rutaDot = rutaCarpeta + File.separator + "automata.dot";
            String rutaPng = rutaCarpeta + File.separator + "automata.png";

            //Creamos el archivo .dot en la carpeta elegida
            File archivoDot = new File(rutaDot);
            FileWriter escritor = new FileWriter(archivoDot);
            escritor.write(codigoDOT);
            escritor.close();

            // Le pasamos las nuevas rutas exactas a Graphviz
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", rutaDot, "-o", rutaPng);
            pb.inheritIO();
            Process proceso = pb.start();
            proceso.waitFor();

            JOptionPane.showMessageDialog(null, "Grafo generado exitosamente en:\n" + rutaPng, "Graphviz", JOptionPane.INFORMATION_MESSAGE);

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar la imagen del AFD" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
