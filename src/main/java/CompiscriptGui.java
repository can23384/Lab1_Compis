import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class CompiscriptGui extends JFrame {
    private static final Color NAVY = new Color(22, 38, 66);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color GREEN = new Color(21, 128, 61);
    private static final Color RED = new Color(185, 28, 28);
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    private final JButton openButton = new JButton("Abrir archivo .cps");
    private final JButton analyzeButton = new JButton("Analizar");
    private final JLabel fileLabel = new JLabel("Ningún archivo seleccionado");
    private final JLabel statusLabel = new JLabel("SIN ARCHIVO");
    private final JTextArea sourceArea = new JTextArea();
    private final ErrorTableModel tableModel = new ErrorTableModel();
    private final JTable errorTable = new JTable(tableModel);
    private final JLabel summaryLabel = new JLabel("Seleccione un archivo Compiscript para comenzar.");
    private Path selectedFile;

    public CompiscriptGui() {
        super("Analizador de Compiscript");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(1180, 760);
        setLocationRelativeTo(null);
        buildUi();
        wireActions();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(16, 18, 16, 18));
        root.setBackground(new Color(238, 242, 247));
        root.add(buildHeader(), BorderLayout.NORTH);

        sourceArea.setEditable(false);
        sourceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        sourceArea.setTabSize(4);
        sourceArea.setMargin(new Insets(8, 10, 8, 10));
        JScrollPane sourceScroll = new JScrollPane(sourceArea);
        sourceScroll.setRowHeaderView(new LineNumberView(sourceArea));

        configureTable();
        JScrollPane tableScroll = new JScrollPane(errorTable);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                titled("Código fuente", sourceScroll), titled("Resultados del análisis", tableScroll));
        split.setResizeWeight(0.57);
        split.setDividerSize(8);
        root.add(split, BorderLayout.CENTER);

        summaryLabel.setOpaque(true);
        summaryLabel.setBackground(Color.WHITE);
        summaryLabel.setBorder(new EmptyBorder(11, 14, 11, 14));
        root.add(summaryLabel, BorderLayout.SOUTH);
        setContentPane(root);
        analyzeButton.setEnabled(false);
        setStatus("SIN ARCHIVO", new Color(75, 85, 99));
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 8));
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(14, 16, 14, 16));
        JLabel title = new JLabel("COMPISCRIPT  ·  ANALIZADOR LÉXICO Y SINTÁCTICO");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        styleButton(openButton, Color.WHITE, NAVY);
        styleButton(analyzeButton, BLUE, Color.WHITE);
        actions.add(openButton);
        actions.add(analyzeButton);
        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);

        JPanel details = new JPanel(new BorderLayout(12, 0));
        details.setOpaque(false);
        fileLabel.setForeground(new Color(219, 234, 254));
        fileLabel.setToolTipText("Ruta del archivo seleccionado");
        details.add(fileLabel, BorderLayout.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(new EmptyBorder(4, 10, 4, 10));
        details.add(statusLabel, BorderLayout.EAST);
        header.add(details, BorderLayout.SOUTH);
        return header;
    }

    private JPanel titled(String title, Component content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);
        panel.add(content);
        return panel;
    }

    private void configureTable() {
        errorTable.setRowHeight(34);
        errorTable.setAutoCreateRowSorter(true);
        errorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        errorTable.setFillsViewportHeight(true);
        errorTable.getTableHeader().setReorderingAllowed(false);
        int[] widths = {45, 100, 60, 70, 135, 540};
        for (int i = 0; i < widths.length; i++) errorTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        errorTable.setDefaultRenderer(Object.class, new ErrorCellRenderer());
    }

    private void wireActions() {
        openButton.addActionListener(event -> chooseFile());
        analyzeButton.addActionListener(event -> analyze());
        errorTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent event) {
                int viewRow = errorTable.getSelectedRow();
                if (viewRow >= 0) focusLine(tableModel.errorAt(errorTable.convertRowIndexToModel(viewRow)).line());
            }
        });
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser(selectedFile == null ? Path.of(".").toFile() : selectedFile.getParent().toFile());
        chooser.setDialogTitle("Seleccionar archivo Compiscript");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos Compiscript (*.cps)", "cps"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        loadFile(chooser.getSelectedFile().toPath());
    }

    private void loadFile(Path file) {
        Path normalized = file.toAbsolutePath().normalize();
        if (!normalized.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".cps")) {
            showFriendlyError("Archivo no válido", "Seleccione un archivo con extensión .cps."); return;
        }
        try {
            if (!Files.isRegularFile(normalized) || !Files.isReadable(normalized)) throw new IOException("El archivo no existe o no se puede leer.");
            if (Files.size(normalized) > MAX_FILE_SIZE) throw new IOException("El archivo supera el límite de 2 MB de esta interfaz.");
            String source = Files.readString(normalized, StandardCharsets.UTF_8);
            selectedFile = normalized;
            sourceArea.setText(source);
            sourceArea.setCaretPosition(0);
            tableModel.setErrors(List.of());
            fileLabel.setText(normalized.toString());
            fileLabel.setToolTipText(normalized.toString());
            analyzeButton.setEnabled(true);
            setStatus("LISTO", BLUE);
            summaryLabel.setText(source.isEmpty() ? "El archivo está vacío; puede analizarlo para validar su sintaxis." : "Archivo cargado. Presione Analizar para procesarlo.");
        } catch (IOException | SecurityException exception) {
            showFriendlyError("No fue posible abrir el archivo", exception.getMessage());
        }
    }

    private void analyze() {
        if (selectedFile == null) return;
        tableModel.setErrors(List.of());
        analyzeButton.setEnabled(false);
        openButton.setEnabled(false);
        setStatus("ANALIZANDO", new Color(180, 83, 9));
        summaryLabel.setText("Analizando el archivo con ANTLR4…");
        Path file = selectedFile;
        new SwingWorker<AnalysisResult, Void>() {
            @Override protected AnalysisResult doInBackground() throws Exception { return new CompiscriptAnalyzer().analyze(file); }
            @Override protected void done() {
                analyzeButton.setEnabled(true);
                openButton.setEnabled(true);
                try { showResult(get()); }
                catch (Exception exception) {
                    setStatus("ERROR", RED);
                    showFriendlyError("No se pudo completar el análisis", "Verifique que el archivo siga disponible y vuelva a intentarlo.");
                }
            }
        }.execute();
    }

    private void showResult(AnalysisResult result) {
        tableModel.setErrors(result.errors());
        if (result.success()) {
            setStatus("CORRECTO", GREEN);
            summaryLabel.setText("El archivo fue analizado correctamente. No se encontraron errores léxicos ni sintácticos.  ·  Archivo: " + result.file().getFileName());
        } else {
            setStatus("CON ERRORES", RED);
            summaryLabel.setText("Archivo: %s  ·  Léxicos: %d  ·  Sintácticos: %d  ·  Total: %d"
                    .formatted(result.file().getFileName(), result.lexicalErrorCount(), result.syntacticErrorCount(), result.totalErrorCount()));
        }
    }

    private void focusLine(int oneBasedLine) {
        try {
            int start = sourceArea.getLineStartOffset(Math.max(0, oneBasedLine - 1));
            int end = sourceArea.getLineEndOffset(Math.max(0, oneBasedLine - 1));
            sourceArea.requestFocusInWindow();
            sourceArea.select(start, end);
        } catch (Exception ignored) { /* El archivo pudo cambiar; no se interrumpe la interfaz. */ }
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBackground(color);
    }

    private void showFriendlyError(String title, String message) {
        JOptionPane.showMessageDialog(this, message == null ? "Ocurrió un problema inesperado." : message, title, JOptionPane.WARNING_MESSAGE);
    }

    private static void styleButton(JButton button, Color background, Color foreground) {
        button.setBackground(background); button.setForeground(foreground); button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 14, 8, 14)); button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
            new CompiscriptGui().setVisible(true);
        });
    }

    private static final class ErrorTableModel extends AbstractTableModel {
        private final String[] columns = {"#", "Tipo", "Línea", "Columna", "Símbolo / token", "Descripción"};
        private List<AnalysisError> errors = new ArrayList<>();
        void setErrors(List<AnalysisError> value) { errors = new ArrayList<>(value); fireTableDataChanged(); }
        AnalysisError errorAt(int row) { return errors.get(row); }
        @Override public int getRowCount() { return errors.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public Object getValueAt(int row, int column) {
            AnalysisError e = errors.get(row);
            return switch (column) { case 0 -> row + 1; case 1 -> e.type().displayName(); case 2 -> e.line(); case 3 -> e.column(); case 4 -> e.symbol(); default -> e.description(); };
        }
    }

    private final class ErrorCellRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
            int modelRow = table.convertRowIndexToModel(row);
            AnalysisError.Type type = tableModel.errorAt(modelRow).type();
            if (!selected) component.setBackground(type == AnalysisError.Type.LEXICO ? new Color(255, 247, 237) : new Color(254, 242, 242));
            setToolTipText(value == null ? null : value.toString());
            if (column == 1) setFont(getFont().deriveFont(Font.BOLD));
            else setFont(getFont().deriveFont(Font.PLAIN));
            return component;
        }
    }
}
