import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class LoggedMenu extends JFrame{

    public RequestsWindow requestsWindow;
    private ArrayList<ChatWindow> chatWindows = new ArrayList<>();
    private final IServer iServer;
    private final IClient iClient;
    private final JList<String> list;
    private final DefaultListModel<String> defaultListModel;

    public LoggedMenu(IServer iServer, IClient iClient) {
        this.iServer = iServer;
        this.iClient = iClient;
        this.requestsWindow = new RequestsWindow(iClient, iServer);

        JPanel jPanel = new JPanel();

        JButton requestsButton = new JButton("Solicitudes");
        JButton addFriendButton = new JButton("Añadir amigo");
        JButton changePasswordButton = new JButton("Cambiar contraseña");

        requestButtonListener requestButtonListener = new requestButtonListener();
        addFriendButtonListener addFriendButtonListener = new addFriendButtonListener();
        changePasswordButtonListener changePasswordButtonListener = new changePasswordButtonListener();

        changePasswordButton.addActionListener(changePasswordButtonListener);
        addFriendButton.addActionListener(addFriendButtonListener);
        requestsButton.addActionListener(requestButtonListener);

        jPanel.add(changePasswordButton);
        jPanel.add(addFriendButton);
        jPanel.add(requestsButton);

        defaultListModel = new DefaultListModel<>();

        /*  se añaden a la lista los amigos */
        try {
            for (String iClient1 : iClient.getFriends()) {
                if (!iClient.getName().equals(iClient1))
                    defaultListModel.addElement(iClient1);
            }
        } catch (Exception e){e.printStackTrace();}

        list = new JList<>(defaultListModel);

        /* cada celda de la lista tendrá un color de fondo distinto en función de si el cliente es amigo o no */
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                try {
                    ArrayList<String> onlineFriends = new ArrayList<>();
                    for (IClient iClient1 : iClient.getOnlineFriends()) {
                        onlineFriends.add(iClient1.getName());
                    }
                    if (onlineFriends.contains(value))
                        setBackground(Color.GREEN);
                    else
                        setBackground(Color.WHITE);
                    return c;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return c;
            }

        });

        /* manejador de clicks en elementos de la lista */
        list.addMouseListener( new MouseAdapter() {
            @Deprecated
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isLeftMouseButton(e) ) {
                    try {
                        /* al hacer click se crea una ventana de chat */
                        String userTarget = defaultListModel.getElementAt(getRow(e.getPoint()));
                        for (IClient iClient1 : iClient.getOnlineFriends()){
                            if (iClient1.getName().equals(userTarget)) {
                                for (ChatWindow chatWindow : chatWindows) {
                                    if (chatWindow.getReceiver().getName().equals(userTarget))
                                        return;
                                }
                                chatWindows.add(new ChatWindow(iClient, iClient1));
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        list.setBounds(100,100, 75,75);

        jPanel.add(list);

        this.addWindowListener(new WindowEventHandler(iServer, iClient));
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            setTitle(iClient.getName());
        } catch (Exception ex){ex.printStackTrace();}
        setSize(600, 600);
        setResizable(false);
        setContentPane(jPanel);
        setVisible(true);
    }

    //Manejador de botón de solicitudes de amistad
    private class requestButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            requestsWindow.setVisible(true);
        }
    }

    //Manejador del botón de envío de solicitudes
    private class addFriendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
           String friend = JOptionPane.showInputDialog("Introduce el nombre del amigo");
           if (friend != null) {
               try {
                   iServer.sendFriendRequest(iClient, friend);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
    }

    //Manejador del botón de cambio de contraseña
    private class changePasswordButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String passwordLegacy = JOptionPane.showInputDialog("Introduce tu contraseña actual");
            if (passwordLegacy != null) {
                String newPassword = JOptionPane.showInputDialog("Introduce tu nueva contraseña");
                if (newPassword != null){
                    try {
                        iServer.changePassword(iClient, newPassword, passwordLegacy);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    //Función que añade un elemento a la lista
    public void addToList(String username){
        this.defaultListModel.addElement(username);
    }

    //Función que devuelve el índice de la JList en el que se encuentra el cursor
    private int getRow(Point point) {
        return list.locationToIndex(point);
    }

    /////////////// Getters y Setters ///////////////
    public ArrayList<ChatWindow> getChatWindows() {
        return chatWindows;
    }
    public void setChatWindows(ArrayList<ChatWindow> chatWindows) {
        this.chatWindows = chatWindows;
    }
}
