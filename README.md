# Persistencia-de-datos-1
Breve guía para compilar, ejecutar y usar la aplicación de gestión de contactos con MVC, JTable, exportación CSV y gráfico de pastel.
# Estructura
- modelo: persona.java, personaDAO.java (persistencia CSV).
- vista: ventana.java (JTabbedPane, JTable, formulario, JProgressBar, PieChartPanel).
- controlador: logica_ventana.java (enlaza vista y DAO, usa SwingWorker).
- AppMain.java: arranque.
# Requisitos
- Java 8+; no librerías externas.
# Ejecución rápida
- Compila las clases respetando paquetes.
- Ejecuta AppMain.
- Datos guardados en USER_HOME/gestionContactos/datosContactos.csv.
# Funcionalidades clave
- Agregar, modificar y eliminar contactos desde el formulario.
- Tabla con ordenamiento y filtrado; búsqueda por campo o botón BUSCAR.
- Exportar contactos a CSV.
- Gráfica de pastel por categoría (Familia, Amigos, Trabajo) en pestaña Estadísticas.
- Operaciones de E/S en background con JProgressBar; atajos (Ctrl+F, Ctrl+N, Ctrl+E); menú contextual y doble clic.
# Formato CSV
- Encabezado: NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO
- Separador: punto y coma (;)
# Notas rápidas
- Validación: requiere al menos nombre o teléfono y categoría válida al guardar.
- Para cambiar separador o persistencia, modifica persona.datosContacto() o personaDAO.
