package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {
    private File dir;
    private File archivo;

    public personaDAO() {
        dir = new File(System.getProperty("user.home"), "gestionContactos");
        if (!dir.exists()) dir.mkdirs();
        archivo = new File(dir, "datosContactos.csv");
        try {
            if (!archivo.exists()) {
                archivo.createNewFile();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
                    bw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean agregar(persona p) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(p.datosContacto());
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized List<persona> leerArchivo() throws IOException {
        List<persona> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean primera = true;
            while ((linea = br.readLine()) != null) {
                if (primera) { primera = false; continue; } // saltar encabezado
                if (linea.trim().isEmpty()) continue;
                String[] parts = linea.split(";", -1);
                persona p = new persona();
                p.setNombre(parts.length > 0 ? parts[0] : "");
                p.setTelefono(parts.length > 1 ? parts[1] : "");
                p.setEmail(parts.length > 2 ? parts[2] : "");
                p.setCategoria(parts.length > 3 ? parts[3] : "");
                p.setFavorito(parts.length > 4 ? Boolean.parseBoolean(parts[4]) : false);
                lista.add(p);
            }
        }
        return lista;
    }

    public synchronized boolean actualizarContactos(List<persona> personas) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
            bw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
            bw.newLine();
            for (persona p : personas) {
                bw.write(p.datosContacto());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized boolean exportarCSV(File destino, List<persona> personas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(destino))) {
            bw.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO");
            bw.newLine();
            for (persona p : personas) {
                bw.write(p.datosContacto());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getArchivo() { return archivo; }
    public File getDir() { return dir; }
}