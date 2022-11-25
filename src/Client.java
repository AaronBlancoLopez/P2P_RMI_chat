import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class Client {

    public static void main(String[] args) throws IOException, NotBoundException {

        String registryURL = "rmi://localhost:1099/P2PServer";
        IServer iServer = (IServer) Naming.lookup(registryURL);
        IClient iClientobj = new IClientImpl();
        MainMenu mainMenu = new MainMenu(iServer, iClientobj);

    }
}
