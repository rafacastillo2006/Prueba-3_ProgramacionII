import javax.swing.*;
import java.awt.*;

public class Ventana extends JFrame {

    private JProgressBar descarga1, descarga2, descarga3;
    private JButton iniciarDescarga, btnCancelar;
    private JTextArea areaNotifs;

    public volatile boolean cancelado = false;
    private int descargasCompletas = 0;
    private final Object lockCompletadas = new Object();

    public Ventana() {
        setTitle("Prueba #3 - Descarga múltiple");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel progresoDescarga = new JPanel(new GridLayout(6, 1, 5, 5));
        progresoDescarga.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        descarga1 = crearProgressBar();
        descarga2 = crearProgressBar();
        descarga3 = crearProgressBar();

        progresoDescarga.add(new JLabel("Archivo 1:"));
        progresoDescarga.add(descarga1);
        progresoDescarga.add(new JLabel("Archivo 2:"));
        progresoDescarga.add(descarga2);
        progresoDescarga.add(new JLabel("Archivo 3:"));
        progresoDescarga.add(descarga3);

        add(progresoDescarga, BorderLayout.NORTH);

        areaNotifs = new JTextArea();
        areaNotifs.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaNotifs);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registro de Eventos"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnCancelar = new JButton("Cancelar");
        iniciarDescarga = new JButton("Iniciar Descarga");
        btnCancelar.setEnabled(false);

        botones.add(iniciarDescarga);
        botones.add(btnCancelar);
        add(botones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> cancelarDescargas());
        iniciarDescarga.addActionListener(e -> iniciarDescargas());
    }

    private JProgressBar crearProgressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        return bar;
    }

    private void iniciarDescargas() {
        cancelado = false;
        synchronized (lockCompletadas) {
            descargasCompletas = 0;
        }

        descarga1.setValue(0);
        descarga2.setValue(0);
        descarga3.setValue(0);

        areaNotifs.setText("");

        iniciarDescarga.setEnabled(false);
        btnCancelar.setEnabled(true);

        registrarEventos("Iniciando descargas.");

        Thread thread1 = new Thread(new EjecutarDescarga("Archivo 1", descarga1, this));
        Thread thread2 = new Thread(new EjecutarDescarga("Archivo 2", descarga2, this));
        Thread thread3 = new Thread(new EjecutarDescarga("Archivo 3", descarga3, this));

        thread1.start();
        thread2.start();
        thread3.start();
    }

    public void registrarEventos(String texto) {
        SwingUtilities.invokeLater(() -> {
            areaNotifs.append(texto + "\n");
            areaNotifs.setCaretPosition(areaNotifs.getDocument().getLength());
        });
    }

    private void cancelarDescargas() {
        cancelado = true;
        btnCancelar.setEnabled(false);
        registrarEventos("Cancelando descargas...");

        SwingUtilities.invokeLater(() -> {
            iniciarDescarga.setEnabled(true);
        });
    }

    public void registrarCompletas() {
        synchronized (lockCompletadas) {
            descargasCompletas++;
            if (descargasCompletas == 3 && !cancelado) {
                registrarEventos("Todas las descargas han finalizado.");
                SwingUtilities.invokeLater(() -> {
                    iniciarDescarga.setEnabled(true);
                    btnCancelar.setEnabled(false);
                });
            }
        }
    }
}
