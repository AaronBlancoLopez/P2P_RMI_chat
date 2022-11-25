import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class IClientImpl extends UnicastRemoteObject implements IClient {

    private LoggedMenu loggedMenu;
    private String name;
    private ArrayList<IClient> onlineFriends = new ArrayList<>();
    private ArrayList<String> friends = new ArrayList<>();
    private ArrayList<String> friendRequests = new ArrayList<>();

    public IClientImpl() throws RemoteException {
        super();
    }

    //Función de notificación al cliente (casos de error, etc)
    public void notify(String message) throws RemoteException{
        JOptionPane.showMessageDialog(null, message);
    }

    //Función envío de mensajes a otro cliente
    public void sendMessage(String message, IClient sender) throws RemoteException {
        /* se comprueba que el cliente tiene una ventana de chat abierta con el cliente que envía el mensaje*/
        for (ChatWindow chatWindow : this.loggedMenu.getChatWindows()){
            if (chatWindow.getReceiver().getName().equals(sender.getName())){
                /* se añade al chat el nuevo mensaje */
                JTextArea textArea = chatWindow.getChat();
                textArea.append(message);
                chatWindow.setChat(textArea);
                if (!chatWindow.isVisible())
                    chatWindow.setVisible(true);
                return;
            }
        }
        /* se abre una ventana de chat con el cliente que envía y se añade el nuevo mensaje */
        ChatWindow chatWindow = new ChatWindow(this, sender);
        JTextArea textArea = chatWindow.getChat();
        textArea.append(message);
        chatWindow.setChat(textArea);
        ArrayList<ChatWindow> chatWindows = this.loggedMenu.getChatWindows();
        chatWindows.add(chatWindow);
        this.loggedMenu.setChatWindows(chatWindows);
    }

    //Función para añadir a un usuario a la JList de la interfaz del cliente
    public void add(String username) throws RemoteException {
        this.loggedMenu.addToList(username);
    }

    //Función para añadir una solicitud a la Jlist de solicitudes de la interfaz del cliente
    public void addRequest(String username) throws RemoteException {
        this.loggedMenu.requestsWindow.addToList(username);
    }

    /////////////// Getters y Setters remotos ///////////////
    public void setLoggedMenu(LoggedMenu loggedMenu) throws RemoteException{
        this.loggedMenu = loggedMenu;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() throws RemoteException {
        return name;
    }
    public ArrayList<IClient> getOnlineFriends() throws RemoteException {
        return onlineFriends;
    }
    public void setOnlineFriends(ArrayList<IClient> onlineFriends) throws RemoteException {
        this.onlineFriends = onlineFriends;
    }
    public ArrayList<String> getFriends() throws RemoteException {
        return friends;
    }
    public void setFriends(ArrayList<String> friends) throws RemoteException {
        this.friends = friends;
    }
    public ArrayList<String> getFriendRequests() throws RemoteException {
        return friendRequests;
    }
    public void setFriendRequests(ArrayList<String> friendRequests) throws RemoteException {
        this.friendRequests = friendRequests;
    }

}
