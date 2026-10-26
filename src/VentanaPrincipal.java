import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private JButton btnAbrir;
    private JButton btnGuardar;
    private JButton btnAnalizar;
    private JTextArea txtEntrada;
    private JTextArea txtConsolaTokens;
    private JTextArea txtConsolaErrores;
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane2;
    private JButton btnReportes;
    private JPanel promptGenerado;
    private JTextArea txtPromptGenerado;
    private JButton btnPrompt;

    private List<Token> listaTokensActual = new ArrayList<>();
    private List<ErrorLexico> listaErroresActual = new ArrayList<>();
    private List<ErrorLexico> listaErroresSintacticos = new ArrayList<>();
    private List<ErrorLexico> listaErroresSemanticos = new ArrayList<>();

    private List<Simbolo> tablaSimbolosActual = new ArrayList<>();
    private List<Agente> astActual = new ArrayList<>();
    private String lexema = "";

    public VentanaPrincipal() {
        setContentPane(panelPrincipal);
        setTitle("Analizador Léxico - PromptZal");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lexema = txtEntrada.getText();

                if (lexema.trim().isEmpty()) { // verifica si no hay texto o solo tiene espacios
                    JOptionPane.showMessageDialog(null, "El editor esta vacio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ejecutarCompilacion();

            }
        });


        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser guardar = new  JFileChooser();
                int archivoGuardado = guardar.showSaveDialog(null);

                if (archivoGuardado == JFileChooser.APPROVE_OPTION) {
                    File archivo = guardar.getSelectedFile();

                    String ruta = archivo.getPath();
                    if(!ruta.endsWith(".pz")){
                        archivo = new File(ruta + ".pz");
                    }

                    try(FileWriter fw = new FileWriter(archivo)){
                        fw.write(txtEntrada.getText());
                        JOptionPane.showMessageDialog(null, "Archivo .pz guardado con exito");
                    }catch (IOException e){
                        JOptionPane.showMessageDialog(null, "Error al guardar el archivo");
                    }

                }
            }
        });


        btnAbrir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser explorador = new JFileChooser();
                int resultado = explorador.showOpenDialog(null);

                if (resultado == JFileChooser.APPROVE_OPTION) {
                    File archivoSeleccionado = explorador.getSelectedFile();
                    try {
                        String contenido = Files.readString(archivoSeleccionado.toPath());
                        txtEntrada.setText(contenido);
                    }catch (Exception ex){
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                }
            }
        });

        btnReportes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (lexema.isEmpty() && listaTokensActual.isEmpty() && listaErroresActual.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Primero debes Analizar un documento .pz para poder generar reportes", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Abrimos el explorador para elegir la carpeta destino
                JFileChooser explorador = new JFileChooser();
                explorador.setDialogTitle("Selecciona la carpeta para guardar los reportes");
                explorador.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int seleccion = explorador.showSaveDialog(null);

                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    // Obtenemos la ruta de la carpeta elegida
                    String rutaCarpeta = explorador.getSelectedFile().getAbsolutePath();

                    // Llamamos al metodo pasandole la nueva ruta
                    generarReportesHTML(listaTokensActual, listaErroresActual, lexema, rutaCarpeta);
                }
            }
        });

        btnPrompt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (txtPromptGenerado.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No hay ningún prompt generado para guardar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JFileChooser explorador = new JFileChooser();
                explorador.setDialogTitle("Guardar PromptZal");

                if (explorador.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File archivo = explorador.getSelectedFile();
                    String ruta = archivo.getPath();

                    if (!ruta.endsWith(".md") || !ruta.endsWith(".txt")) {
                        ruta += ".md";
                    }

                    try (FileWriter fw = new FileWriter(ruta)) {
                        fw.write(txtPromptGenerado.getText());
                        JOptionPane.showMessageDialog(null, "Prompt guardado exitosamente en:\n" + ruta);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al guardar el prompt: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private void ejecutarCompilacion(){
        //por si se compila dos veces
        txtPromptGenerado.setText("");
        listaTokensActual.clear();
        listaErroresActual.clear();
        listaErroresSintacticos.clear();
        listaErroresSemanticos.clear();
        List<ErrorLexico> listaErroresSintacticos = new ArrayList<>();
        List<ErrorLexico> listaErroresSemanticos = new ArrayList<>();

        try {

            //ANALISIS LEXICO
            LexerPromptZal lexer = new LexerPromptZal(new StringReader(lexema));

            while (true) {
                Token token = lexer.yylex();

                if (token == null || token.getTipo().equals("EOF")) {
                    listaTokensActual.add(new Token("EOF", "FIN", token.getFila(), token.getColumna()));
                    break;
                }

                if (token.getTipo().equals("ERROR_LEXICO")) {
                    listaErroresActual.add(new ErrorLexico(token.getLexema(), "Caracter lexico no reconocido", token.getFila(), token.getColumna()));
                } else {
                    listaTokensActual.add(token);
                }
            }

            //ANALISIS SINTACTICO
            Parser analizadorSintactico = new Parser(listaTokensActual);
            analizadorSintactico.parsePrograma();
            listaErroresSintacticos = analizadorSintactico.getErroresSintacticos();

            //ANALISIS SEMANTICO
            AnalizadorSemantico analizadorSemantico = new AnalizadorSemantico(
                    analizadorSintactico.getListaAgentes(),
                    analizadorSintactico.getEjecuciones(),
                    analizadorSintactico.getExportaciones()
            );
            analizadorSemantico.analizar();
            listaErroresSemanticos = analizadorSemantico.getErroresSemanticos();

            tablaSimbolosActual = analizadorSemantico.getTablaSimbolos();
            astActual = analizadorSintactico.getListaAgentes();

            //JTEXT AREA DE TOKENS
            StringBuilder reporteTokens = new StringBuilder(); //sirve para concatenar grandes cantidades de texto
            reporteTokens.append("--- TOKENS RECONOCIDOS ---\n\n");

            for (Token t : listaTokensActual) { // por cada token que exista dentro de mi listaTokensActual, haz lo siguiente:
                reporteTokens.append(" | Lexema: ").append(t.getLexema()).append("'")
                        .append(" | Tipo: ").append(t.getTipo())
                        .append(" | Fila: ").append(t.getFila())
                        .append(" | Columna: ").append(t.getColumna()).append("\n");
            }
            txtConsolaTokens.setText(reporteTokens.toString());

            //JTEXT AREA DE ERRORES
            StringBuilder reporteErrores = new StringBuilder();

            if (listaErroresActual.isEmpty() && listaErroresSintacticos.isEmpty() && listaErroresSemanticos.isEmpty()) {
                reporteErrores.append("No se encontraron errores lexicos, sintacticos ni semanticos.\n");
            } else {

                reporteErrores.append("--- ERRORES ENCONTRADOS ---\n\n");

                // Imprimimos los errores lexicos
                for (ErrorLexico err : listaErroresActual) {
                    reporteErrores.append(" Errores LEXICOS: '").append(err.getLexema()).append("'")
                            .append(" | Descripcion: ").append(err.getDescripcion())
                            .append(" | Fila: ").append(err.getFila())
                            .append(" | Columna: ").append(err.getColumna()).append("\n");
                }

                // Imprimimos los errores sintacticos
                for (ErrorLexico err : listaErroresSintacticos) {
                    reporteErrores.append("Errores SINTACTICOS: '").append(err.getLexema()).append("'")
                            .append(" | Descripcion: ").append(err.getDescripcion())
                            .append(" | Fila: ").append(err.getFila())
                            .append(" | Columna: ").append(err.getColumna()).append("\n");
                }

                // Imprimimos los errores semanticos
                for (ErrorLexico err : listaErroresSemanticos) {
                    reporteErrores.append("Errores SEMANTICOS: '").append(err.getLexema()).append("'")
                            .append(" | Descripcion: ").append(err.getDescripcion())
                            .append(" | Fila: ").append(err.getFila())
                            .append(" | Columna: ").append(err.getColumna()).append("\n");
                }
            }
            txtConsolaErrores.setText(reporteErrores.toString());


            //GENERACION DEL PROMPTZAL
            if (listaErroresActual.isEmpty() && listaErroresSintacticos.isEmpty()) {
                reporteErrores.append("No se encontraron errores estructurales (Lexicos ni Sintacticos).\n\n");

                GeneradorPrompt generador = new GeneradorPrompt(
                        analizadorSintactico.getListaAgentes(),
                        analizadorSintactico.getEjecuciones(),
                        analizadorSintactico.getExportaciones()
                );

                txtPromptGenerado.setText(generador.generarTexto());

                if (listaErroresSemanticos.isEmpty()) {
                    reporteErrores.append("No se encontraron errores Semanticos.\n");
                    JOptionPane.showMessageDialog(null, "Analisis y generacion completados con exito.", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Se genero el prompt, pero hay errores semanticos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                reporteErrores.append("--- ERRORES CRITICOS ENCONTRADOS ---\n");
                reporteErrores.append("No se pudo generar el PromptZal debido a errores de estructura.\n\n");
                JOptionPane.showMessageDialog(null, "Analisis completado con errores criticos. No se pudo crear el prompt.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        }catch (Exception ex){
            JOptionPane.showMessageDialog(null, "Error critico durante la compilación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReportesHTML(List<Token> tokens, List<ErrorLexico> errores, String codigoFuente, String rutaCarpeta) {
        try {
            // REPORTE DE TOKENS
            String rutaTokens = rutaCarpeta + File.separator + "Reporte_Tokens.html";
            FileWriter fwTokens = new FileWriter(rutaTokens);
            fwTokens.write("<html><head><title>Reporte de Tokens</title><style>table, th, td {border: 1px solid black; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwTokens.write("<h2>Reporte de Tokens - PromptZal</h2>");
            fwTokens.write("<table><tr><th>#</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");
            int contadorTokens = 1;
            for (Token t : tokens) {
                fwTokens.write("<tr><td>" + (contadorTokens++) + "</td><td>" + t.getLexema() + "</td><td>" + t.getTipo() + "</td><td>" + t.getFila() + "</td><td>" + t.getColumna() + "</td></tr>");
            }
            fwTokens.write("</table></body></html>");
            fwTokens.close();

            // REPORTE DE ERRORES
            String rutaErrores = rutaCarpeta + File.separator + "Reporte_Errores.html";
            FileWriter fwErrores = new FileWriter(rutaErrores);
            fwErrores.write("<html><head><title>Reporte de Errores</title><style>table, th, td {border: 1px solid red; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwErrores.write("<h2>Reporte de Errores - PromptZal</h2>");

            List<ErrorLexico> todosLosErrores = new ArrayList<>();
            todosLosErrores.addAll(errores); // Léxicos
            todosLosErrores.addAll(listaErroresSintacticos); // Sintácticos
            todosLosErrores.addAll(listaErroresSemanticos); // Semánticos

            if (todosLosErrores.isEmpty()) {
                fwErrores.write("<h3>No se encontraron errores.</h3>");
            } else {
                fwErrores.write("<table><tr><th>Lexema/Carácter</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>");
                for (ErrorLexico e : todosLosErrores) {
                    fwErrores.write("<tr><td>" + e.getLexema() + "</td><td>" + e.getDescripcion() + "</td><td>" + e.getFila() + "</td><td>" + e.getColumna() + "</td></tr>");
                }
                fwErrores.write("</table>");
            }
            fwErrores.write("</body></html>");
            fwErrores.close();

            // REPORTE DE TABLA DE SIMBOLOS
            String rutaSimbolos = rutaCarpeta + File.separator + "Reporte_TablaSimbolos.html";
            FileWriter fwSimbolos = new FileWriter(rutaSimbolos);
            fwSimbolos.write("<html><head><title>Tabla de Símbolos</title><style>table, th, td {border: 1px solid purple; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwSimbolos.write("<h2>Reporte de Tabla de Símbolos</h2>");
            fwSimbolos.write("<table><tr><th>#</th><th>Identificador</th><th>Tipo</th><th>Ámbito</th><th>Línea</th></tr>");
            int contadorSimbolos = 1;
            for (Simbolo s : tablaSimbolosActual) {
                fwSimbolos.write("<tr><td>" + (contadorSimbolos++) + "</td><td>" + s.getIdentificador() + "</td><td>" + s.getTipo() + "</td><td>" + s.getAmbito() + "</td><td>" + s.getLinea() + "</td></tr>");
            }
            fwSimbolos.write("</table></body></html>");
            fwSimbolos.close();

            // REPORTE DEL AST
            String rutaAST = rutaCarpeta + File.separator + "Reporte_AST.html";
            FileWriter fwAST = new FileWriter(rutaAST);

            fwAST.write("<html><head><title>Reporte AST</title><style>");
            fwAST.write("body { font-family: Arial, sans-serif; background-color: #f4f4f9; text-align: center; padding: 20px; }");
            fwAST.write(".nodo { background: white; border: 2px solid #2c3e50; border-radius: 8px; padding: 15px; margin: 0 auto; display: inline-block; min-width: 300px; text-align: left; box-shadow: 2px 2px 8px rgba(0,0,0,0.1); }");
            fwAST.write(".flecha { font-size: 24px; font-weight: bold; color: #2c3e50; margin: 10px 0; text-align: center; }");
            fwAST.write(".raiz { border-color: #8e44ad; border-width: 3px; text-align: center; font-size: 1.2em; font-weight: bold; }");
            fwAST.write(".agente { border-color: #2980b9; text-align: center; font-weight: bold; background-color: #eaf2f8; }");
            fwAST.write(".seccion { font-weight: bold; color: #333; border-bottom: 1px solid #ccc; margin-bottom: 8px; padding-bottom: 4px; text-align: center; }");
            fwAST.write(".item { margin: 5px 0; font-family: monospace; font-size: 1.1em; }");
            fwAST.write("</style></head><body>");

            fwAST.write("<h2>Árbol de Sintaxis Abstracta (AST)</h2>");
            fwAST.write("<div class='nodo raiz'>PROGRAMA</div>");

            for (Agente ag : astActual) {
                fwAST.write("<div class='flecha'>&#8595;</div>");
                fwAST.write("<div class='nodo agente'>AGENTE: " + ag.getNombre() + "</div>");

                // Nodo de Contexto
                fwAST.write("<div class='flecha'>&#8595;</div>");
                fwAST.write("<div class='nodo'><div class='seccion'>CONTEXTO</div>");
                fwAST.write("<div class='item' style='text-align:center;'>" + ag.getContexto() + "</div></div>");

                // Nodo de Variables
                if (!ag.getVariables().isEmpty()) {
                    fwAST.write("<div class='flecha'>&#8595;</div>");
                    fwAST.write("<div class='nodo'><div class='seccion'>VARIABLES</div>");
                    for (Variable var : ag.getVariables()) {
                        fwAST.write("<div class='item'>&bull; " + var.getNombre() + " = " + var.getValor() + "</div>");
                    }
                    fwAST.write("</div>");
                }

                // Nodo de Comandos
                if (!ag.getComandos().isEmpty()) {
                    fwAST.write("<div class='flecha'>&#8595;</div>");
                    fwAST.write("<div class='nodo'><div class='seccion'>COMANDOS</div>");
                    for (Comando cmd : ag.getComandos()) {
                        fwAST.write("<div class='item'>&bull; " + cmd.getTipoComando() + " " + cmd.getArgumentoPrincipal() + " " + cmd.getConectores() + " -> " + cmd.getVariableDestino() + "</div>");
                    }
                    fwAST.write("</div>");
                }
            }

            fwAST.write("</body></html>");
            fwAST.close();

            JOptionPane.showMessageDialog(null, "Reportes generados exitosamente en:\n" + rutaCarpeta);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al generar reportes: " + ex.getMessage());
        }
    }
}
