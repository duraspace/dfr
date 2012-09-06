package org.duraspace.dfr.sync;

import java.awt.AWTException;
import java.awt.Dialog.ModalityType;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.security.ProtectionDomain;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DfrSyncDriver {
    private static int port;
    private static String contextPath;
    private final static Logger log =
        LoggerFactory.getLogger(DfrSyncDriver.class);

    public static void main(String[] args) throws Exception {
        try {

            final JDialog dialog = new JDialog();
            dialog.setSize(new java.awt.Dimension(400, 50));
            dialog.setModalityType(ModalityType.MODELESS);
            dialog.setTitle("UpSync Utility");
            dialog.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            final JLabel label = new JLabel("Starting...");
            final JProgressBar progress = new JProgressBar();
            progress.setStringPainted(true);

            panel.add(label);
            panel.add(progress);
            dialog.add(panel);
            dialog.setVisible(true);

            port = getPort();
            contextPath = getContextPath();
            Server srv = new Server(port);

            ProtectionDomain protectionDomain =
                DfrSyncDriver.class.getProtectionDomain();
            String warFile =
                protectionDomain.getCodeSource().getLocation().toExternalForm();
            log.debug("warfile: {}", warFile);
            WebAppContext context = new WebAppContext();
            context.setContextPath(contextPath);
            context.setWar(warFile);
            srv.setHandler(context);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    createSysTray();

                    String url = getUrl();
                    HttpClient client = new HttpClient();
                    while (true) {
                        try {

                            if (progress.getValue() < 100) {
                                progress.setValue(progress.getValue() + 3);
                            }

                            Thread.sleep(2000);

                            GetMethod get = new GetMethod(url);
                            int response = client.executeMethod(get);
                            log.debug("response from {}: {}", url, response);
                            if (response == 200)
                                break;
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    progress.setValue(100);

                    label.setText("Launching browser...");
                    launchBrowser();
                    dialog.setVisible(false);


                }

            }).start();

            srv.start();

            srv.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getContextPath() {
        return System.getProperty("dfr.contextPath", "/dfr-sync");
    }

    private static int getPort() {
        return Integer.parseInt(System.getProperty("dfr.port", "8888"));
    }

    private static void createSysTray() {
        final TrayIcon trayIcon;
        try {

            if (SystemTray.isSupported()) {

                SystemTray tray = SystemTray.getSystemTray();
                InputStream is =  DfrSyncDriver.class.getClassLoader().getResourceAsStream("tray.png");
                
                Image image = ImageIO.read(is);
                MouseListener mouseListener = new MouseListener() {

                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Tray Icon - Mouse clicked!");
                    }

                    public void mouseEntered(MouseEvent e) {
                        System.out.println("Tray Icon - Mouse entered!");
                    }

                    public void mouseExited(MouseEvent e) {
                        System.out.println("Tray Icon - Mouse exited!");
                    }

                    public void mousePressed(MouseEvent e) {
                        System.out.println("Tray Icon - Mouse pressed!");
                    }

                    public void mouseReleased(MouseEvent e) {
                        System.out.println("Tray Icon - Mouse released!");
                    }
                };

                ActionListener exitListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        log.info("Exiting...");
                        System.exit(0);
                    }
                };

                PopupMenu popup = new PopupMenu();
                MenuItem view = new MenuItem("View Status");
                view.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        launchBrowser();
                    }
                });
                popup.add(view);

                MenuItem exit = new MenuItem("Exit");
                exit.addActionListener(exitListener);
                popup.add(exit);

                trayIcon = new TrayIcon(image, "UpSync", popup);

                ActionListener actionListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        trayIcon.displayMessage("Action Event",
                                                "An Action Event Has Been Performed!",
                                                TrayIcon.MessageType.INFO);
                    }
                };

                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(actionListener);
                trayIcon.addMouseListener(mouseListener);

                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    System.err.println("TrayIcon could not be added.");
                }
            } else {
                log.warn("System Tray is not supported.");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }

    private static void launchBrowser() {
        if (!java.awt.Desktop.isDesktopSupported()) {
            log.warn("Desktop is not supported. Unable to open");

        } else {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                log.warn("Desktop doesn't support the browse action.");
            } else {
                java.net.URI uri;
                try {
                    uri = new java.net.URI(getUrl());
                    desktop.browse(uri);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private static String getUrl() {
        return "http://localhost:" + getPort() + getContextPath();
    }
}