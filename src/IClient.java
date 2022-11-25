import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IClient extends Remote {

    void notify(String message) throws RemoteException;
    void sendMessage(String message, IClient sender) throws RemoteException;
    void addRequest(String username) throws RemoteException;
    void add(String username) throws RemoteException;
    void setLoggedMenu(LoggedMenu loggedMenu) throws RemoteException;
    void setName(String username) throws RemoteException;
    String getName() throws RemoteException;
    ArrayList<IClient> getOnlineFriends() throws RemoteException;
    void setOnlineFriends(ArrayList<IClient> onlineFriends) throws RemoteException;
    ArrayList<String> getFriends() throws RemoteException;
    void setFriends(ArrayList<String> friends) throws RemoteException;
    ArrayList<String> getFriendRequests() throws RemoteException;
    void setFriendRequests(ArrayList<String> friendRequests) throws RemoteException;
}