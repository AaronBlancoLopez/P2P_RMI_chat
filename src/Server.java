import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args){
        try {
            IServer obj = new IServerImpl();
            startRegistry();
            String registryURL = "rmi://localhost:1099/P2PServer";
            Naming.rebind(registryURL, obj);
            System.out.println("Servidor listo.");
        } catch (Exception e) {e.printStackTrace();}
    }

    private static void startRegistry() throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(1099);

            registry.list();
        } catch (RemoteException e) {
            System.out.println("RMI registry cannot be located at port " + 1099);
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry created at port " + 1099);
        }
    }
}
