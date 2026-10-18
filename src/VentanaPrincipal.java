import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private JButton btnGenerarAFD;

    private List<Token> listaTokensActual = new ArrayList<>();
    private List<ErrorLexico> listaErroresActual = new ArrayList<>();
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
                    JOptionPane.showMessageDialog(null, "El editor está vacio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //por si se compila dos veces
                listaTokensActual.clear();
                listaErroresActual.clear();
                List<ErrorLexico> listaErroresSintacticos = new ArrayList<>();

                try {

                    //analisis lexico
                    LexerPromptZal lexer = new LexerPromptZal(new StringReader(lexema));

                    while (true){
                        Token token = lexer.yylex();

                        if (token == null || token.getTipo().equals("EOF")){
                            listaTokensActual.add(new Token("EOF", "FIN", token.getFila(), token.getColumna()));
                            break;
                        }

                        if (token.getTipo().equals("ERROR_LEXICO")){
                            listaErroresActual.add(new ErrorLexico(token.getLexema(), "Caracter lexico no reconocido", token.getFila(), token.getColumna()));
                        }else {
                            listaTokensActual.add(token);
                        }
                    }

                    Parser analizadorSintactico = new Parser(listaTokensActual);
                    analizadorSintactico.parsePrograma();

                    listaErroresSintacticos = analizadorSintactico.getErroresSintacticos();

                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Error crítico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder reporteTokens = new StringBuilder(); //sirve para concatenar grandes cantidades de texto

                reporteTokens.append("--- TOKENS RECONOCIDOS ---\n\n");
                for (Token t : listaTokensActual) { // por cada token que exista dentro de mi listaTokensActual, haz lo siguiente:
                    reporteTokens.append(" | Lexema: ").append(t.getLexema()).append("'")
                            .append(" | Tipo: ").append(t.getTipo())
                            .append(" | Fila: ").append(t.getFila())
                            .append(" | Columna: ").append(t.getColumna()).append("\n");
                }
                txtConsolaTokens.setText(reporteTokens.toString());

                StringBuilder reporteErrores = new StringBuilder();
                if (listaErroresActual.isEmpty() && listaErroresSintacticos.isEmpty()) {
                    reporteErrores.append("No se encontraron errores lexicos ni sintacticos.\n");
                    JOptionPane.showMessageDialog(null, "Analisis completado sin errores.", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    reporteErrores.append("--- ERRORES ENCONTRADOS ---\n\n");

                    // Imprimimos los errores léxicos
                    for (ErrorLexico err : listaErroresActual) {
                        reporteErrores.append(" Errores LEXICOS: '").append(err.getLexema()).append("'")
                                .append(" | Descripcion: ").append(err.getDescripcion())
                                .append(" | Fila: ").append(err.getFila())
                                .append(" | Columna: ").append(err.getColumna()).append("\n");
                    }

                    // Imprimimos los errores sintácticos
                    for (ErrorLexico err : listaErroresSintacticos) {
                        reporteErrores.append("Errores SINTACTICOS: '").append(err.getLexema()).append("'")
                                .append(" | Descripcion: ").append(err.getDescripcion())
                                .append(" | Fila: ").append(err.getFila())
                                .append(" | Columna: ").append(err.getColumna()).append("\n");
                    }

                    JOptionPane.showMessageDialog(null, "Analisis completado con errores. Revisa la consola.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }

                txtConsolaErrores.setText(reporteErrores.toString());
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

        btnGenerarAFD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser explorador = new JFileChooser();
                explorador.setDialogTitle("Selecciona donde guardar la imagen del AFD");
                explorador.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int seleccion = explorador.showSaveDialog(null);

                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    String rutaCarpeta = explorador.getSelectedFile().getAbsolutePath();

                    GeneradorAFD generador = new GeneradorAFD();
                    generador.crearAFD(rutaCarpeta);
                }
            }
        });
    }

    private void generarReportesHTML(List<Token> tokens, List<ErrorLexico> errores, String codigoFuente, String rutaCarpeta) {
        try {
            // REPORTE DE TOKENS
            String rutaTokens = rutaCarpeta + File.separator + "Reporte_Tokens.html";
            FileWriter fwTokens = new FileWriter(rutaTokens);

            fwTokens.write("<html><head><title>Reporte de Tokens</title><style>table, th, td {border: 1px solid black; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwTokens.write("<h2>Reporte de Tokens - PromptZal</h2>");
            fwTokens.write("<table><tr><th>#</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");

            for (Token t : tokens) {
                String colorFondo = "";

                // Convertimos a mayúsculas para evitar problemas de texto (puedes ajustar los nombres exactos a como los tienes en tu código)
                switch (t.getTipo().toUpperCase()) {
                    case "DIRECTIVA":
                        colorFondo = "#cce5ff"; // Azul muy claro
                        break;
                    case "NUMERO ENTERO":
                    case "NUMERO DECIMAL":
                        colorFondo = "#d4edda"; // Verde claro
                        break;
                    case "PALABRA RESERVADA":
                    case "COMANDO":
                        colorFondo = "#fff3cd"; // Amarillo
                        break;
                    case "CADENA DE TEXTO":
                        colorFondo = "#f8d7da"; // Rosa
                        break;
                    case "IDENTIFICADOR":
                        colorFondo = "#e2e3e5"; // Gris claro
                        break;
                    case "COMENTARIO":
                        colorFondo = "#d1ecf1"; // Turquesa claro
                        break;
                    default:
                        colorFondo = "#ffffff"; // Blanco
                        break;
                }

                // Inyectamos el color exacto en la etiqueta <tr> de esta fila
                fwTokens.write("<tr style='background-color: " + colorFondo + ";'>" +
                        "<td>" + t.getId() + "</td>" +
                        "<td>" + t.getLexema() + "</td>" +
                        "<td>" + t.getTipo() + "</td>" +
                        "<td>" + t.getFila() + "</td>" +
                        "<td>" + t.getColumna() + "</td>" +
                        "</tr>");
            }
            fwTokens.write("</table></body></html>");
            fwTokens.close();

            // REPORTE DE ERRORES
            String rutaErrores = rutaCarpeta + File.separator + "Reporte_Errores.html";
            FileWriter fwErrores = new FileWriter(rutaErrores);
            fwErrores.write("<html><head><title>Reporte de Errores</title><style>table, th, td {border: 1px solid red; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwErrores.write("<h2>Reporte de Errores - PromptZal</h2>");

            if (errores.isEmpty()) {
                fwErrores.write("<h3>¡Felicidades! No se encontraron errores léxicos en el archivo.</h3>");
            } else {
                fwErrores.write("<table><tr><th>Lexema/Carácter</th><th>Descripción</th><th>Fila</th><th>Columna</th></tr>");
                for (ErrorLexico e : errores) {
                    fwErrores.write("<tr><td>" + e.getLexema() + "</td><td>" + e.getDescripcion() + "</td><td>" + e.getFila() + "</td><td>" + e.getColumna() + "</td></tr>");
                }
                fwErrores.write("</table>");
            }
            fwErrores.write("</body></html>");
            fwErrores.close();

            // REPORTE DE ESTADÍSTICAS
            Map<String, Integer> frecuencias = new HashMap<>();
            for (Token t : tokens) {
                frecuencias.put(t.getTipo(), frecuencias.getOrDefault(t.getTipo(), 0) + 1);
            }

            int totalLineas = codigoFuente.isEmpty() ? 0 : codigoFuente.split("\n").length;

            String rutaStats = rutaCarpeta + File.separator + "Reporte_Estadisticas.html";
            FileWriter fwStats = new FileWriter(rutaStats);
            fwStats.write("<html><head><title>Estadísticas</title><style>table, th, td {border: 1px solid blue; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwStats.write("<h2>Reporte de Estadisticas</h2>");

            fwStats.write("<h3>Resumen General</h3>");
            fwStats.write("<ul><li><b>Total de Tokens:</b> " + tokens.size() + "</li>");
            fwStats.write("<li><b>Total de Errores:</b> " + errores.size() + "</li>");
            fwStats.write("<li><b>Total de Líneas Analizadas:</b> " + totalLineas + "</li></ul>");

            fwStats.write("<h3>Frecuencia por Tipo de Token</h3>");
            fwStats.write("<table><tr><th>Tipo de Token</th><th>Cantidad Encontrada</th></tr>");
            for (Map.Entry<String, Integer> entry : frecuencias.entrySet()) {
                fwStats.write("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
            }
            fwStats.write("</table></body></html>");
            fwStats.close();

            JOptionPane.showMessageDialog(null, "Reportes guardados exitosamente en:\n" + rutaCarpeta);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al generar reportes: " + ex.getMessage());
        }
    }
}
