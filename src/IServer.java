import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {

    boolean signUp(IClient client, String username, String password) throws IOException;
    boolean logIn(IClient client, String username, String password) throws IOException;
    void logOut(IClient client) throws RemoteException;
    void changePassword(IClient client, String newPassword, String oldPassword) throws IOException;
    void sendFriendRequest(IClient client, String usernameTarget) throws IOException;
    void acceptFriendRequest(IClient client, String usernameRequest) throws IOException;
}
