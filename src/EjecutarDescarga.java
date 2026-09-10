import javax.swing.JProgressBar;

public class EjecutarDescarga implements Runnable {

    private final String nombreArchivo;
    private final JProgressBar progressBar;
    private final Ventana ventanaPrincipal;

    public EjecutarDescarga(String nombreArchivo, JProgressBar progressBar, Ventana ventanaPrincipal) {
        this.nombreArchivo = nombreArchivo;
        this.progressBar = progressBar;
        this.ventanaPrincipal = ventanaPrincipal;
    }

    @Override
    public void run() {
        int progreso = 0;
        int tardanza = 50 + (int) (Math.random() * 151);

        while (progreso < 100 && !ventanaPrincipal.cancelado) {
            try {
                Thread.sleep(tardanza);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            progreso += (int) (Math.random() * 4) + 1;
            if (progreso > 100) progreso = 100;

            final int progress = progreso;

            javax.swing.SwingUtilities.invokeLater(() -> progressBar.setValue(progress));

            if (progress % 20 == 0 || progress == 100) {
                ventanaPrincipal.registrarEventos(nombreArchivo + ": descarga al " + progress + "%");
            }
        }

        if (ventanaPrincipal.cancelado) {
            ventanaPrincipal.registrarEventos(nombreArchivo + ": descarga cancelada.");
        } else if (progreso >= 100) {
            ventanaPrincipal.registrarEventos(nombreArchivo + ": descarga completada");
            ventanaPrincipal.registrarCompletas();
        }
    }
}