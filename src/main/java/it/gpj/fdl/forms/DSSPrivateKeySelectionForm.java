package it.gpj.fdl.forms;

import com.google.common.base.Splitter;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.security.cert.CertificateException;


import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;


/** 
 * Dialog pensata per la selezione di una chiave/certificato, da utilizzare
 * nel caso dall'accesso ad una Smart Card siano rilevate più chiavi.
 *
 * @author Giovanni
 */
public class DSSPrivateKeySelectionForm extends JDialog implements KeyListener {
private ButtonGroup certificatesGroup;
private DSSPrivateKeyEntry selectedKey;
private DSSPrivateKeyEntry[] keys;

    /** Crea un JDialog per la selezione di una chiave/certificato
     *
     * @param pkCertificates elenco di chiavi/certificati da mostrare
     * @throws java.io.IOException in caso di errore nella lettura della chiave
     * @throws java.security.cert.CertificateException in caso di errore nella lettura del certificato
     */
    public DSSPrivateKeySelectionForm(DSSPrivateKeyEntry[] pkCertificates) throws IOException, CertificateException {

        this.keys = new DSSPrivateKeyEntry[pkCertificates.length];
        this.certificatesGroup = new ButtonGroup();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
                selectedKey = null;
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        setTitle("Selezione Certificato");

        setModalityType(DEFAULT_MODALITY_TYPE);
        fillForm(pkCertificates);
    }

    // Riempie il form con i vari pannelli/checkbox per la scelta della chiave
    private void fillForm(DSSPrivateKeyEntry[] pkCertificates) throws IOException, CertificateException {
        JPanel smartCardPanel = new JPanel();
        smartCardPanel.setLayout(new BoxLayout(smartCardPanel, BoxLayout.Y_AXIS));
        JLabel labelTitle = new JLabel(" Seleziona il certificato da utilizzare:            ");
        smartCardPanel.add(labelTitle);
        smartCardPanel.add(new JLabel(" "));

        for (int i=0; i<pkCertificates.length; i++) {
            // Tiro fuori dai certificati dell'issuer e del subject la stringa "DN",
            // ossia una stringa con un formato "chiave-valore" in cui sono mostrati
            // vari dati del certificato. In particolare splitto per avere solo il
            // valore corrispondente alla chiave "CN" che contiene il nome del soggetto
            
            String issuerDN = pkCertificates[i].getCertificate().getCertificate().getIssuerDN().getName();
            String subjectDN = pkCertificates[i].getCertificate().getCertificate().getSubjectDN().getName();
            String issuerCN = Splitter.on(',')
                .trimResults()
                .withKeyValueSeparator(
                    Splitter.on('=')
                        .limit(2)
                        .trimResults())
                .split(issuerDN).get("CN");
            String subjectCN = Splitter.on(',')
                .trimResults()
                .withKeyValueSeparator(
                    Splitter.on('=')
                        .limit(2)
                        .trimResults())
                .split(subjectDN).get("CN");
            keys[i] = pkCertificates[i];
            JRadioButton certificate = new JRadioButton(issuerCN + " - " + subjectCN, true);
            certificate.addKeyListener(this);

            // Assegno al nome del JRadioButton, l'indice relativo al certificato, in modo da poterlo ottenere quando l'utente clicca sul bottone "Ok"
            certificate.setName(String.valueOf(i));
            certificatesGroup.add(certificate);
            smartCardPanel.add(certificate);
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        JButton confirmButton = new JButton("Ok");
        JButton cancelButton = new JButton("Annulla");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        confirmButton.addKeyListener(this);
        cancelButton.addKeyListener(this);

        // quando viene premuto il bottone "Ok" identifica il certificato selezionato e ne setta la proprieta relativa
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Enumeration<AbstractButton> buttons = certificatesGroup.getElements();
                for (int i=0; i < certificatesGroup.getButtonCount(); i++) {
                    JRadioButton button = (JRadioButton) buttons.nextElement();
                    if (button.isSelected()) {
                        selectedKey = keys[Integer.parseInt(button.getName())];
                        break;
                    }
                }
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedKey = null;
                dispose();
            }
        });

        // Eventuale controllo in caso non ci siano certificati va qui
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(smartCardPanel), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        addKeyListener(this);

        // setto il pulsante "Ok" (confirmButton) come bottone selezionato premendo "Invio" sulla tastiera
        getRootPane().setDefaultButton(confirmButton);

        pack();
        setSize(getWidth() + (int)(getWidth() * 0.2), getHeight() + (int)(getHeight() * 0.2));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2 );
        setAlwaysOnTop(true);
        setVisible(true);
    }

    /** Ritorna il certificato selezionato
     *
     * @return il certificato selezionato, null se non è stato selezionato nessuna certificato
     */
    public DSSPrivateKeyEntry getSelectedKey() {
        return selectedKey;
    }

    @Override
    public void keyPressed(KeyEvent kEvt) {
        if (kEvt.getKeyCode() != KeyEvent.VK_ENTER)
            kEvt.consume();

        if (kEvt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            selectedKey = null;
            dispose();
        }

        if (kEvt.getKeyCode() == KeyEvent.VK_DOWN) {
            ArrayList<AbstractButton> buttonsArrayList = new ArrayList<AbstractButton>();
            Enumeration<AbstractButton> radioButtons = certificatesGroup.getElements();
            while (radioButtons.hasMoreElements()) {
                buttonsArrayList.add(radioButtons.nextElement());
            }
            for (int i=0; i<buttonsArrayList.size() - 1; i++) {
                if (buttonsArrayList.get(i).isSelected()) {
                    buttonsArrayList.get(i + 1).setSelected(true);
                    break;
                }
            }
        }

        if (kEvt.getKeyCode() == KeyEvent.VK_UP) {
            ArrayList<AbstractButton> buttonsArrayList = new ArrayList<AbstractButton>();
            Enumeration<AbstractButton> radioButtons = certificatesGroup.getElements();
            while (radioButtons.hasMoreElements()) {
                buttonsArrayList.add(radioButtons.nextElement());
            }
            for (int i=buttonsArrayList.size()-1; i>0; i--) {
                if (buttonsArrayList.get(i).isSelected()) {
                    buttonsArrayList.get(i - 1).setSelected(true);
                    break;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}