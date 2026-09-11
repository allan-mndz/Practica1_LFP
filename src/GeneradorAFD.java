public class GeneradorAFD {

    public void crearAFD() {
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
                
                // nodos normales
                S3 [shape = circle];
                S5 [shape = circle];
                
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
                
                
            
            }
            """;

    }
}
