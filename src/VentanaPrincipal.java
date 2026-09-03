import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private List<Token> listaTokensActual = new ArrayList<>();
    private List<ErrorLexico> listaErroresActual = new ArrayList<>();
    private String codigoFuenteActual = "";

    public VentanaPrincipal() {
        setContentPane(panelPrincipal);
        setTitle("Analizador Léxico - PromptZal");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codigoFuenteActual = txtEntrada.getText();

                if (codigoFuenteActual.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El editor está vacio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                AnalizadorLexico analizador = new AnalizadorLexico(codigoFuenteActual);
                analizador.analizar();

                listaTokensActual = analizador.getListaTokens();
                listaErroresActual = analizador.getListaErrores();

                StringBuilder reporteTokens = new StringBuilder();
                reporteTokens.append("--- TOKENS RECONOCIDOS ---\n\n");
                for (Token t : listaTokensActual) {
                    reporteTokens.append("Lexema: '").append(t.getLexema()).append("'")
                            .append(" | Tipo: ").append(t.getTipo())
                            .append(" | Fila: ").append(t.getFila())
                            .append(" | Columna: ").append(t.getColumna()).append("\n");
                }
                txtConsolaTokens.setText(reporteTokens.toString());

                StringBuilder reporteErrores = new StringBuilder();
                if (listaErroresActual.isEmpty()) {
                    reporteErrores.append("No se encontraron errores lexicos.\n");
                } else {
                    reporteErrores.append("--- ERRORES ENCONTRADOS ---\n\n");
                    for (ErrorLexico err : listaErroresActual) {
                        reporteErrores.append("Error: '").append(err.getLexema()).append("'")
                                .append(" | Descripcion: ").append(err.getDescripcion())
                                .append(" | Fila: ").append(err.getFila())
                                .append(" | Columna: ").append(err.getColumna()).append("\n");
                    }
                }
                txtConsolaErrores.setText(reporteErrores.toString());

                JOptionPane.showMessageDialog(null, "Análisis completado.", "listo", JOptionPane.INFORMATION_MESSAGE);
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
                        String contenido = java.nio.file.Files.readString(archivoSeleccionado.toPath());
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
                if (codigoFuenteActual.isEmpty() && listaTokensActual.isEmpty() && listaErroresActual.isEmpty()) {
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
                    generarReportesHTML(listaTokensActual, listaErroresActual, codigoFuenteActual, rutaCarpeta);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            }
        });
    }

    private void generarReportesHTML(java.util.List<Token> tokens, java.util.List<ErrorLexico> errores, String codigoFuente, String rutaCarpeta) {
        try {
            // --- 1. REPORTE DE TOKENS ---
            String rutaTokens = rutaCarpeta + java.io.File.separator + "Reporte_Tokens.html";
            java.io.FileWriter fwTokens = new java.io.FileWriter(rutaTokens);
            fwTokens.write("<html><head><title>Reporte de Tokens</title><style>table, th, td {border: 1px solid black; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwTokens.write("<h2>Reporte de Tokens - PromptZal</h2>");
            fwTokens.write("<table><tr><th>#</th><th>Lexema</th><th>Tipo</th><th>Fila</th><th>Columna</th></tr>");

            for (Token t : tokens) {
                fwTokens.write("<tr><td>" + t.getId() + "</td><td>" + t.getLexema() + "</td><td>" + t.getTipo() + "</td><td>" + t.getFila() + "</td><td>" + t.getColumna() + "</td></tr>");
            }
            fwTokens.write("</table></body></html>");
            fwTokens.close();

            // --- 2. REPORTE DE ERRORES ---
            String rutaErrores = rutaCarpeta + java.io.File.separator + "Reporte_Errores.html";
            java.io.FileWriter fwErrores = new java.io.FileWriter(rutaErrores);
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

            // --- 3. REPORTE DE ESTADÍSTICAS ---
            java.util.Map<String, Integer> frecuencias = new java.util.HashMap<>();
            for (Token t : tokens) {
                frecuencias.put(t.getTipo(), frecuencias.getOrDefault(t.getTipo(), 0) + 1);
            }

            int totalLineas = codigoFuente.isEmpty() ? 0 : codigoFuente.split("\n").length;

            String rutaStats = rutaCarpeta + java.io.File.separator + "Reporte_Estadisticas.html";
            java.io.FileWriter fwStats = new java.io.FileWriter(rutaStats);
            fwStats.write("<html><head><title>Estadísticas</title><style>table, th, td {border: 1px solid blue; border-collapse: collapse; padding: 5px;}</style></head><body>");
            fwStats.write("<h2>Reporte de Estadísticas</h2>");

            fwStats.write("<h3>Resumen General</h3>");
            fwStats.write("<ul><li><b>Total de Tokens:</b> " + tokens.size() + "</li>");
            fwStats.write("<li><b>Total de Errores:</b> " + errores.size() + "</li>");
            fwStats.write("<li><b>Total de Líneas Analizadas:</b> " + totalLineas + "</li></ul>");

            fwStats.write("<h3>Frecuencia por Tipo de Token</h3>");
            fwStats.write("<table><tr><th>Tipo de Token</th><th>Cantidad Encontrada</th></tr>");
            for (java.util.Map.Entry<String, Integer> entry : frecuencias.entrySet()) {
                fwStats.write("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
            }
            fwStats.write("</table></body></html>");
            fwStats.close();

            JOptionPane.showMessageDialog(null, "¡Reportes guardados exitosamente en:\n" + rutaCarpeta);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al generar reportes: " + ex.getMessage());
        }
    }
}
