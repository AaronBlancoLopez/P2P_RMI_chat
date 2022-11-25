import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class ChatWindow extends JFrame {

    private final IClient sender, receiver;
    private JTextArea chat = new JTextArea(25,20);
    private final JTextField message = new JTextField(30);
    public final JButton sendButton = new JButton("Enviar");

    public ChatWindow(IClient client1, IClient client2){
        this.sender = client1;
        this.receiver = client2;

        JPanel jPanel = new JPanel();

        JScrollPane scroll = new JScrollPane(chat);
        scroll.setHorizontalScrollBarPolicy(30);
        scroll.setVerticalScrollBarPolicy(20);
        chat.setEditable(false);

        sendButtonListener sendButtonListener = new sendButtonListener();
        sendButton.addActionListener(sendButtonListener);

        jPanel.add(sendButton);
        jPanel.add(scroll);
        jPanel.add(message);

        this.add(jPanel);
        setSize(700, 700);
        setResizable(false);
        try {
            setTitle(client1.getName() + "->" + client2.getName());
        } catch (Exception e){e.printStackTrace();}
        setVisible(true);
    }

    //Menejador del botón de envío de mensajes
    private class sendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evento){
            /* comprobación de mensaje vacío */
            if (message.getText().isBlank())
                return;
            try {
                receiver.sendMessage("\n" + sender.getName() + ": " + message.getText(), sender);
                chat.append("\n" + sender.getName() + ": " + message.getText());
                message.setText("");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /////////////// Getters y Setters ///////////////
    public IClient getReceiver() {
        return receiver;
    }
    public JTextArea getChat() {
        return chat;
    }
    public void setChat(JTextArea chat) {
        this.chat = chat;
    }

}
