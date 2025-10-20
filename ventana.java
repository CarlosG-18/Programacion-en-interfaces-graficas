package vista;

import modelo.persona;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ventana extends JFrame {
    // campos del formulario
    public JTextField txt_nombres;
    public JTextField txt_telefono;
    public JTextField txt_email;
    public JTextField txt_buscar;
    public JCheckBox chb_favorito;
    public JComboBox<String> cmb_categoria;
    public JButton btn_add;
    public JButton btn_modificar;
    public JButton btn_eliminar;
    public JButton btn_buscar; // nuevo botón BUSCAR
    public JTable tbl_contactos;
    public DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JProgressBar progressBar;

    private JTabbedPane tabs;
    private JPanel panelContactos;
    private JPanel panelEstadisticas;
    private PieChartPanel pieChartPanel; // panel de pastel
    public JButton btn_refrescar_estadisticas; // botón REFRESCAR en estadísticas

    public ventana() {
        setTitle("GESTION DE CONTACTOS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 1026, 760);
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        tabs = new JTabbedPane();

        panelContactos = new JPanel(null);

        JLabel lbl1 = new JLabel("NOMBRES:");
        lbl1.setFont(new Font("Tahoma", Font.BOLD, 15));
        lbl1.setBounds(25, 28, 89, 31);
        panelContactos.add(lbl1);

        txt_nombres = new JTextField();
        txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_nombres.setBounds(124, 28, 427, 31);
        panelContactos.add(txt_nombres);

        JLabel lbl2 = new JLabel("TELEFONO:");
        lbl2.setFont(new Font("Tahoma", Font.BOLD, 15));
        lbl2.setBounds(25, 69, 89, 31);
        panelContactos.add(lbl2);

        txt_telefono = new JTextField();
        txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_telefono.setBounds(124, 69, 427, 31);
        panelContactos.add(txt_telefono);

        JLabel lbl3 = new JLabel("EMAIL:");
        lbl3.setFont(new Font("Tahoma", Font.BOLD, 15));
        lbl3.setBounds(25, 110, 89, 31);
        panelContactos.add(lbl3);

        txt_email = new JTextField();
        txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_email.setBounds(124, 110, 427, 31);
        panelContactos.add(txt_email);

        chb_favorito = new JCheckBox("CONTACTO FAVORITO");
        chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chb_favorito.setBounds(24, 152, 220, 25);
        panelContactos.add(chb_favorito);

        cmb_categoria = new JComboBox<>();
        cmb_categoria.setBounds(300, 152, 251, 31);
        String[] categorias = {"Elija una Categoria", "Familia", "Amigos", "Trabajo"};
        for (String c : categorias) cmb_categoria.addItem(c);
        panelContactos.add(cmb_categoria);

        btn_add = new JButton("AGREGAR");
        btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_add.setBounds(601, 70, 125, 65);
        panelContactos.add(btn_add);

        btn_modificar = new JButton("MODIFICAR");
        btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_modificar.setBounds(736, 70, 125, 65);
        panelContactos.add(btn_modificar);

        btn_eliminar = new JButton("ELIMINAR");
        btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_eliminar.setBounds(871, 69, 125, 65);
        panelContactos.add(btn_eliminar);

        JLabel lbl_buscar = new JLabel("BUSCAR:");
        lbl_buscar.setFont(new Font("Tahoma", Font.BOLD, 15));
        lbl_buscar.setBounds(25, 650, 80, 31);
        panelContactos.add(lbl_buscar);

        txt_buscar = new JTextField();
        txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_buscar.setBounds(100, 650, 700, 31);
        panelContactos.add(txt_buscar);

        // Botón BUSCAR
        btn_buscar = new JButton("BUSCAR");
        btn_buscar.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btn_buscar.setBounds(820, 650, 95, 31);
        panelContactos.add(btn_buscar);

        // Tabla
        String[] cols = {"NOMBRE", "TELEFONO", "EMAIL", "CATEGORIA", "FAVORITO"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Boolean.class;
                return String.class;
            }
        };
        tbl_contactos = new JTable(tableModel);
        tbl_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_contactos.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(tableModel);
        tbl_contactos.setRowSorter(sorter);

        JScrollPane scr = new JScrollPane(tbl_contactos);
        scr.setBounds(25, 242, 971, 398);
        panelContactos.add(scr);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBounds(25, 710, 971, 25);
        panelContactos.add(progressBar);

        // Menú contextual
        JPopupMenu popup = new JPopupMenu();
        JMenuItem miEditar = new JMenuItem("Editar");
        JMenuItem miEliminar = new JMenuItem("Eliminar");
        popup.add(miEditar);
        popup.add(miEliminar);
        tbl_contactos.setComponentPopupMenu(popup);

        tbl_contactos.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    for (Component c : popup.getComponents()) {
                        if (c instanceof JMenuItem && "Editar".equals(((JMenuItem)c).getText())) {
                            ((JMenuItem)c).doClick();
                            break;
                        }
                    }
                }
            }
        });

        // Filtro en tiempo real
        txt_buscar.getDocument().addDocumentListener(new DocumentListener() {
            private void apply() {
                String text = txt_buscar.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
            }
            public void insertUpdate(DocumentEvent e) { apply(); }
            public void removeUpdate(DocumentEvent e) { apply(); }
            public void changedUpdate(DocumentEvent e) { apply(); }
        });

        // Atajos globales
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusBuscar");
        am.put("focusBuscar", new AbstractAction() { public void actionPerformed(ActionEvent e) { txt_buscar.requestFocusInWindow(); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "nuevo");
        am.put("nuevo", new AbstractAction() { public void actionPerformed(ActionEvent e) { btn_add.doClick(); }});
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "exportar");
        am.put("exportar", new AbstractAction() { public void actionPerformed(ActionEvent e) { /* controlador asignará acción */ }});

        // Panel Estadísticas con PieChartPanel y botón REFRESCAR
        panelEstadisticas = new JPanel(null);
        pieChartPanel = new PieChartPanel();
        pieChartPanel.setBounds(20, 20, 600, 400);
        panelEstadisticas.add(pieChartPanel);

        // Botón refrescar estadísticas
        btn_refrescar_estadisticas = new JButton("REFRESCAR");
        btn_refrescar_estadisticas.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btn_refrescar_estadisticas.setBounds(650, 40, 140, 36);
        panelEstadisticas.add(btn_refrescar_estadisticas);

        JLabel lblEst = new JLabel("Estadísticas por categoría");
        lblEst.setHorizontalAlignment(SwingConstants.CENTER);
        lblEst.setBounds(20, 430, 800, 24);
        panelEstadisticas.add(lblEst);

        // Añadir pestañas
        tabs.addTab("Contactos", panelContactos);
        tabs.addTab("Estadísticas", panelEstadisticas);

        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    // Métodos públicos para controlador
    public void setContactos(List<persona> personas) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (persona p : personas) {
                tableModel.addRow(new Object[] {
                    p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria(), p.isFavorito()
                });
            }
        });
    }

    public persona getSelectedPersonaFromTable() {
        int row = tbl_contactos.getSelectedRow();
        if (row < 0) return null;
        int modelRow = tbl_contactos.convertRowIndexToModel(row);
        persona p = new persona();
        p.setNombre((String) tableModel.getValueAt(modelRow, 0));
        p.setTelefono((String) tableModel.getValueAt(modelRow, 1));
        p.setEmail((String) tableModel.getValueAt(modelRow, 2));
        p.setCategoria((String) tableModel.getValueAt(modelRow, 3));
        p.setFavorito(Boolean.TRUE.equals(tableModel.getValueAt(modelRow, 4)));
        return p;
    }

    public List<persona> getAllPersonasFromModel() {
        List<persona> list = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            persona p = new persona(
                (String) tableModel.getValueAt(i, 0),
                (String) tableModel.getValueAt(i, 1),
                (String) tableModel.getValueAt(i, 2),
                (String) tableModel.getValueAt(i, 3),
                Boolean.TRUE.equals(tableModel.getValueAt(i, 4))
            );
            list.add(p);
        }
        return list;
    }

    public void updateProgress(int value, String text) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            progressBar.setString(text);
        });
    }

    public File showSaveDialog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exportar contactos a CSV");
        chooser.setSelectedFile(new File("contactos_export.csv"));
        int r = chooser.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) return chooser.getSelectedFile();
        return null;
    }

    // Nuevo: actualizar estadísticas (invocado por el controlador)
    public void setEstadisticas(int familia, int amigos, int trabajo) {
        pieChartPanel.setValues(familia, amigos, trabajo);
    }

    // Aplica filtro y selecciona la primera fila coincidente si existe (invocado por botón BUSCAR)
    public void buscarYSeleccionar(String texto) {
        final String t = texto == null ? "" : texto.trim();
        SwingUtilities.invokeLater(() -> {
            if (t.isEmpty()) {
                sorter.setRowFilter(null);
                tbl_contactos.clearSelection();
                return;
            }
            try {
                RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(t));
                sorter.setRowFilter(rf);
                if (tbl_contactos.getRowCount() > 0) {
                    int viewRow = 0;
                    tbl_contactos.setRowSelectionInterval(viewRow, viewRow);
                    tbl_contactos.scrollRectToVisible(tbl_contactos.getCellRect(viewRow, 0, true));
                } else {
                    tbl_contactos.clearSelection();
                }
            } catch (java.util.regex.PatternSyntaxException ex) {
                sorter.setRowFilter(null);
                tbl_contactos.clearSelection();
            }
        });
    }

    // Panel que dibuja una gráfica de pastel simple sin bibliotecas externas
    private static class PieChartPanel extends JPanel {
        private int vFamilia, vAmigos, vTrabajo;

        public PieChartPanel() {
            this.vFamilia = 0;
            this.vAmigos = 0;
            this.vTrabajo = 0;
            setPreferredSize(new Dimension(400, 300));
        }

        public void setValues(int familia, int amigos, int trabajo) {
            this.vFamilia = Math.max(0, familia);
            this.vAmigos = Math.max(0, amigos);
            this.vTrabajo = Math.max(0, trabajo);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int total = vFamilia + vAmigos + vTrabajo;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 40;
            int x = (w - size) / 2;
            int y = (h - size) / 2;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            if (total == 0) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("No hay datos para mostrar", x + 10, y + size/2);
                drawLegend(g2, x + size + 10, y, 12);
                g2.dispose();
                return;
            }

            double start = 0.0;
            double extent;

            extent = 360.0 * vFamilia / total;
            g2.setColor(new Color(66, 133, 244));
            g2.fillArc(x, y, size, size, (int)Math.round(start), (int)Math.round(extent));
            start += extent;

            extent = 360.0 * vAmigos / total;
            g2.setColor(new Color(52, 168, 83));
            g2.fillArc(x, y, size, size, (int)Math.round(start), (int)Math.round(extent));
            start += extent;

            extent = 360.0 * vTrabajo / total;
            g2.setColor(new Color(245, 124, 0));
            g2.fillArc(x, y, size, size, (int)Math.round(start), (int)Math.round(extent));

            int legendX = x + size + 20;
            int legendY = y;
            drawLegend(g2, legendX, legendY, 16);

            g2.dispose();
        }

        private void drawLegend(Graphics2D g2, int lx, int ly, int boxSize) {
            Font f = g2.getFont().deriveFont(Font.PLAIN, 12f);
            g2.setFont(f);
            int gap = 8;
            int y = ly;

            g2.setColor(new Color(66, 133, 244));
            g2.fillRect(lx, y, boxSize, boxSize);
            g2.setColor(Color.BLACK);
            g2.drawString("Familia: " + vFamilia, lx + boxSize + gap, y + boxSize - 4);
            y += boxSize + 6;

            g2.setColor(new Color(52, 168, 83));
            g2.fillRect(lx, y, boxSize, boxSize);
            g2.setColor(Color.BLACK);
            g2.drawString("Amigos: " + vAmigos, lx + boxSize + gap, y + boxSize - 4);
            y += boxSize + 6;

            g2.setColor(new Color(245, 124, 0));
            g2.fillRect(lx, y, boxSize, boxSize);
            g2.setColor(Color.BLACK);
            g2.drawString("Trabajo: " + vTrabajo, lx + boxSize + gap, y + boxSize - 4);
        }
    }
}