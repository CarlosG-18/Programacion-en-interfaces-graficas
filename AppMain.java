import vista.ventana;
import controlador.logica_ventana;
import javax.swing.SwingUtilities;
public class AppMain {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            ventana v = new ventana();
            new logica_ventana(v);
            v.setVisible(true);
        });
    }
}