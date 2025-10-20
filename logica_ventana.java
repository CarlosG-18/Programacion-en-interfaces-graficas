package controlador;

import modelo.persona;
import modelo.personaDAO;
import vista.ventana;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class logica_ventana {
    private ventana vista;
    private personaDAO dao;

    public logica_ventana(ventana v) {
        this.vista = v;
        this.dao = new personaDAO();
        bind();
        cargarContactos();
    }

    private void bind() {
        vista.btn_add.addActionListener(e -> accionAgregar());
        vista.btn_modificar.addActionListener(e -> accionModificar());
        vista.btn_eliminar.addActionListener(e -> accionEliminar());
        vista.btn_buscar.addActionListener(e -> vista.buscarYSeleccionar(vista.txt_buscar.getText()));
        vista.txt_buscar.addActionListener(e -> vista.buscarYSeleccionar(vista.txt_buscar.getText()));

        // Botón REFRESCAR estadísticas
        vista.btn_refrescar_estadisticas.addActionListener(e -> refrescarEstadisticasAsync());

        // Registrar acciones del popup creado en la vista (Editar/Eliminar)
        JPopupMenu popup = vista.tbl_contactos.getComponentPopupMenu();
        if (popup != null) {
            for (int i = 0; i < popup.getComponentCount(); i++) {
                java.awt.Component ci = popup.getComponent(i);
                if (ci instanceof JMenuItem) {
                    JMenuItem mi = (JMenuItem) ci;
                    if ("Editar".equals(mi.getText())) mi.addActionListener(ev -> accionCargarSeleccionEnFormulario());
                    if ("Eliminar".equals(mi.getText())) mi.addActionListener(ev -> accionEliminar());
                }
            }
        }

        // Doble click carga selección en formulario
        vista.tbl_contactos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    accionCargarSeleccionEnFormulario();
                }
            }
        });

        // Atajo exportar: Ctrl+E
        vista.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "exportarContacts");
        vista.getRootPane().getActionMap().put("exportarContacts", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { accionExportar(); }
        });
    }

    private void cargarContactos() {
        SwingWorker<List<persona>, Integer> loader = new SwingWorker<List<persona>, Integer>() {
            @Override protected List<persona> doInBackground() throws Exception {
                publish(0);
                List<persona> lista;
                try { lista = dao.leerArchivo(); } catch (IOException ex) { lista = java.util.Collections.emptyList(); }
                publish(100);
                return lista;
            }
            @Override protected void process(List<Integer> chunks) {
                int v = chunks.get(chunks.size()-1);
                vista.updateProgress(v, v < 100 ? "Cargando..." : "Listo");
            }
            @Override protected void done() {
                try {
                    List<persona> data = get();
                    vista.setContactos(data);
                    actualizarEstadisticas(data);
                    vista.updateProgress(0, "");
                } catch (Exception e) { e.printStackTrace(); vista.updateProgress(0, ""); }
            }
        };
        loader.execute();
    }

    private void accionAgregar() {
        persona p = leerFormulario();
        if (p == null) return;

        SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                vista.updateProgress(0, "Guardando...");
                boolean ok = dao.agregar(p);
                vista.updateProgress(100, "Guardado");
                return ok;
            }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        limpiarFormulario();
                        cargarContactos();
                        JOptionPane.showMessageDialog(vista, "Contacto guardado");
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al guardar contacto");
                    }
                } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(vista, "Error al guardar contacto"); }
                vista.updateProgress(0, "");
            }
        };
        sw.execute();
    }

    private void accionModificar() {
        persona seleccionado = vista.getSelectedPersonaFromTable();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un contacto en la tabla para modificar");
            return;
        }

        persona actualizado = leerFormulario();
        if (actualizado == null) return;

        SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                List<persona> all = dao.leerArchivo();
                boolean replaced = false;
                for (int i = 0; i < all.size(); i++) {
                    persona p = all.get(i);
                    if ((p.getTelefono() != null && p.getTelefono().equals(seleccionado.getTelefono()))
                        || (p.getEmail() != null && p.getEmail().equals(seleccionado.getEmail()))
                        || (p.getNombre() != null && p.getNombre().equals(seleccionado.getNombre()))) {
                        all.set(i, actualizado);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) all.add(actualizado);
                dao.actualizarContactos(all);
                return true;
            }
            @Override protected void done() {
                try {
                    get();
                    limpiarFormulario();
                    cargarContactos();
                    JOptionPane.showMessageDialog(vista, "Contacto modificado");
                } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(vista, "Error al modificar contacto"); }
                vista.updateProgress(0, "");
            }
        };
        sw.execute();
    }

    private void accionEliminar() {
        persona sel = vista.getSelectedPersonaFromTable();
        if (sel == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un contacto en la tabla para eliminar");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(vista, "¿Eliminar contacto seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                List<persona> all = dao.leerArchivo();
                List<persona> keep = new java.util.ArrayList<>();
                for (persona p : all) {
                    boolean igual = (p.getTelefono()!=null && p.getTelefono().equals(sel.getTelefono()))
                        || (p.getEmail()!=null && p.getEmail().equals(sel.getEmail()))
                        || (p.getNombre()!=null && p.getNombre().equals(sel.getNombre()));
                    if (!igual) keep.add(p);
                }
                dao.actualizarContactos(keep);
                return true;
            }
            @Override protected void done() {
                try {
                    get();
                    limpiarFormulario();
                    cargarContactos();
                    JOptionPane.showMessageDialog(vista, "Contacto eliminado");
                } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(vista, "Error al eliminar contacto"); }
                vista.updateProgress(0, "");
            }
        };
        sw.execute();
    }

    private void accionExportar() {
        File destino = vista.showSaveDialog();
        if (destino == null) return;
        SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() throws Exception {
                List<persona> data = dao.leerArchivo();
                vista.updateProgress(0, "Exportando...");
                boolean ok = dao.exportarCSV(destino, data);
                vista.updateProgress(100, "Exportado");
                return ok;
            }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    JOptionPane.showMessageDialog(vista, ok ? "Exportado correctamente" : "Error al exportar");
                } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(vista, "Error al exportar"); }
                vista.updateProgress(0, "");
            }
        };
        sw.execute();
    }

    // refrescar estadísticas en background y actualizar pie
    private void refrescarEstadisticasAsync() {
        SwingWorker<List<persona>, Void> sw = new SwingWorker<List<persona>, Void>() {
            @Override protected List<persona> doInBackground() throws Exception {
                vista.updateProgress(0, "Calculando estadísticas...");
                List<persona> all = dao.leerArchivo();
                vista.updateProgress(100, "Listo");
                return all;
            }
            @Override protected void done() {
                try {
                    List<persona> data = get();
                    actualizarEstadisticas(data);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    vista.updateProgress(0, "");
                }
            }
        };
        sw.execute();
    }

    // Helper: leer y validar formulario
    private persona leerFormulario() {
        String nombre = vista.txt_nombres.getText().trim();
        String telefono = vista.txt_telefono.getText().trim();
        String email = vista.txt_email.getText().trim();
        String categoria = (String) vista.cmb_categoria.getSelectedItem();
        boolean favorito = vista.chb_favorito.isSelected();

        if ((nombre.isEmpty() && telefono.isEmpty()) ) {
            JOptionPane.showMessageDialog(vista, "Debe ingresar al menos Nombre o Teléfono");
            return null;
        }
        if (categoria == null || categoria.equals("Elija una Categoria") || categoria.trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Seleccione una categoría válida");
            return null;
        }
        return new persona(nombre, telefono, email, categoria, favorito);
    }

    // Cargar selección en formulario
    private void accionCargarSeleccionEnFormulario() {
        persona sel = vista.getSelectedPersonaFromTable();
        if (sel == null) {
            JOptionPane.showMessageDialog(vista, "Seleccione un registro en la tabla");
            return;
        }
        vista.txt_nombres.setText(sel.getNombre());
        vista.txt_telefono.setText(sel.getTelefono());
        vista.txt_email.setText(sel.getEmail());
        vista.cmb_categoria.setSelectedItem(sel.getCategoria());
        vista.chb_favorito.setSelected(sel.isFavorito());
    }

    private void limpiarFormulario() {
        vista.txt_nombres.setText("");
        vista.txt_telefono.setText("");
        vista.txt_email.setText("");
        vista.cmb_categoria.setSelectedIndex(0);
        vista.chb_favorito.setSelected(false);
    }

    // Actualiza datos del pie chart según la lista recibida
    private void actualizarEstadisticas(List<persona> lista) {
        int familia = 0, amigos = 0, trabajo = 0;
        for (persona p : lista) {
            String c = p.getCategoria() == null ? "" : p.getCategoria().trim().toLowerCase();
            if (c.equals("familia")) familia++;
            else if (c.equals("amigos")) amigos++;
            else if (c.equals("trabajo")) trabajo++;
        }
        vista.setEstadisticas(familia, amigos, trabajo);
    }
}