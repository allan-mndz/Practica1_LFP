import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class VentanaPrincipal extends JFrame {
    private JButton btnAbrir;
    private JButton btnGuardar;
    private JButton btnAnalizar;
    private JTextArea txtEntrada;
    private JTextArea txtConsolaTokens;
    private JTextArea txtConsolaErrores;
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane2;

    public VentanaPrincipal() {
        setContentPane(panelPrincipal);
        setTitle("Analizador Léxico - PromptZal");
        setSize(1500, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en la pantalla

        btnAnalizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codigoFuente = txtEntrada.getText();

                if (codigoFuente.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El editor está vacío. Abre un archivo o escribe código primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);                    return;
                }

                AnalizadorLexico analizador = new AnalizadorLexico(codigoFuente);
                analizador.analizar();

                var listaTokens = analizador.getListaTokens();
                var listaErrores = analizador.getListaErrores();

                StringBuilder reporteTokens = new StringBuilder();
                reporteTokens.append("--- TOKENS RECONOCIDOS ---\n\n");
                for (Token t : listaTokens) {
                    reporteTokens.append("Lexema: '").append(t.getLexema()).append("'")
                            .append(" | Tipo: ").append(t.getTipo())
                            .append(" | Fila: ").append(t.getFila())
                            .append(" | Columna: ").append(t.getColumna()).append("\n");
                }
                txtConsolaTokens.setText(reporteTokens.toString());

                StringBuilder reporteErrores = new StringBuilder();
                if (listaErrores.isEmpty()) {
                    reporteErrores.append("No se encontraron errores léxicos.\n");
                } else {
                    reporteErrores.append("--- ERRORES ENCONTRADOS ---\n\n");
                    for (ErrorLexico err : listaErrores) {
                        reporteErrores.append("Error: '").append(err.getLexema()).append("'") // o err.getCaracter() según lo tengas
                                .append(" | Descripción: ").append(err.getDescripcion())
                                .append(" | Fila: ").append(err.getFila())
                                .append(" | Columna: ").append(err.getColumna()).append("\n");
                    }
                }
                txtConsolaErrores.setText(reporteErrores.toString());

                JOptionPane.showMessageDialog(null, "Análisis completado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        });


        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                ventana.setVisible(true);
            }
        });
    }
}
