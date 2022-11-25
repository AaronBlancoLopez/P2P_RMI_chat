import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;

public class IServerImpl extends UnicastRemoteObject implements IServer{

    private ArrayList<IClient> clientsList = new ArrayList<>();

    public IServerImpl() throws RemoteException {
        super();
    }

    //Función que incluye al nuevo usuario en la base de datos
    public boolean signUp(IClient client, String username, String password) throws RemoteException {
        /* lectura del archivo de usuarios */
        try {
            FileReader fr = new FileReader("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            String[] separator;
            while ((line = br.readLine()) != null) {
                separator = line.split(",");
                /* comprobación de nombre repetido */
                if (separator[0].equals(username)) {
                    return false;
                }
            }
            /* escritura en el archivo del nuevo usuario */
            FileWriter fw = new FileWriter("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.newLine();
            bw.write(username + "," + password + ",;,;");
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Función que comprueba que el usuario esté registrado e inicia su sesión incluyendolo en la lista de usuarios conectados
    public boolean logIn(IClient client, String username, String password) throws IOException {
        FileReader fr = new FileReader("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt");
        BufferedReader br = new BufferedReader(fr);
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> friendRequests = new ArrayList<>();
        ArrayList<IClient> onlineFriends = new ArrayList<>();
        ArrayList<IClient> iClientOnlineFriends;
        String line;
        String[] separator;
        String[] friendsSeparator;
        String[] requestSeparator;
        while ((line = br.readLine()) != null) {
            separator = line.split(",");
            /* comprobación de usuario registrado */
            if (separator[0].equals(username) && separator[1].equals(password)) {
                /* actualización de clientes conectados en servidor */
                client.setName(username);
                ArrayList<IClient> clients = getClientsList();
                clients.add(client);
                setClientsList(clients);
                /* establecimiento de las propiedades del usuario */
                friendsSeparator = separator[2].split(";");
                Collections.addAll(friends, friendsSeparator);
                client.setFriends(friends);
                requestSeparator = separator[3].split(";");
                Collections.addAll(friendRequests, requestSeparator);
                client.setFriendRequests(friendRequests);
                /* actualización de amigos conectados */
                for (IClient iClient : clientsList) {
                    if (!iClient.equals(client) && client.getFriends().contains(iClient.getName())) {
                        iClientOnlineFriends = iClient.getOnlineFriends();
                        iClientOnlineFriends.add(client);
                        iClient.setOnlineFriends(iClientOnlineFriends);
                        onlineFriends.add(iClient);
                    }
                }
                client.setOnlineFriends(onlineFriends);
                return true;
            }
        }
        return false;
    }

    //Función elimina al cliente de la lista de clientes conectados
    public void logOut(IClient client) throws RemoteException{
        ArrayList<IClient> onlineFriends;
        /* comprobación de cliente conectado */
        if (clientsList.contains(client)) {
            /* actualización de clientes conectados en servidor */
            ArrayList<IClient> clients = getClientsList();
            clients.remove(client);
            setClientsList(clients);
            /* actualización de clientes conectados y notificación a los clientes */
            for (IClient iClient : clientsList) {
               onlineFriends = iClient.getOnlineFriends();
               if (onlineFriends.contains(client)){
                   onlineFriends.remove(client);
                   iClient.setOnlineFriends(onlineFriends);
               }
            }
        }
    }

    //Función que permite a un usuario cambiar su contraseña en el sistema
    public void changePassword(IClient iClient, String newPassword, String oldPassword) throws IOException {
        File file = new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt");
        File tmp = new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/tmp.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp, true));
        String line, newline;
        String[] separator;
        /* lectura del archivo original */
        while ((line = br.readLine()) != null ) {
            separator = line.split(",");
            /* escritura en archivo temporal */
            if (!separator[0].equals(iClient.getName())){
                bw.write(line + "\n");
            } else {
                if (separator[1].equals(oldPassword)){
                    separator[1] = newPassword;
                    newline = separator[0] + "," + separator[1] + "," + separator[2] + "," + separator[3];
                    bw.write(newline + "\n");
                } else {
                    System.out.println("Contraseña incorrecta\n");
                }
            }
        }
        /* borrado de archivo original y renombrado del archivo temporal */
        bw.flush();
        bw.close();
        br.close();
        file.delete();
        tmp.renameTo(new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt"));
    }

    //Función que permite a un usuario mandar una solicitud de amistad a otro a partir de su nombre
    public void sendFriendRequest(IClient client, String usernameTarget) throws IOException{
        /* comprobación de que el usuario no es ya amigo */
        if (client.getFriends().contains(usernameTarget))
            return;
        /* comprobación de solicitud repetida */
        if (client.getFriendRequests().contains(usernameTarget))
            return;
        File file = new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt");
        File tmp = new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/tmp.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp, true));
        String line;
        String[] separator;
        while ((line = br.readLine()) != null) {
            separator = line.split(",");
            if (separator[0].equals(usernameTarget)) {
                bw.write(line.trim() + client.getName() + ";\n");
            } else {
                bw.write(line + "\n");
            }
        }
        /* si el cliente al que se manda está conectado, se añade la solicitud a su lista en la interfaz gráfica y a su vector de solicitudes */
        for (IClient iClient : clientsList){
            if (iClient.getName().equals(usernameTarget)){
                ArrayList<String> friendRequest = iClient.getFriendRequests();
                friendRequest.add(client.getName());
                iClient.setFriendRequests(friendRequest);
                iClient.addRequest(client.getName());
            }
        }
        /* borrado de archivo original y renombrado del archivo temporal */
        bw.flush();
        bw.close();
        file.delete();
        tmp.renameTo(new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt"));
    }

    //Función que permite a un usuario aceptar una solicitud de amistad
    public void acceptFriendRequest(IClient client, String usernameRequest) throws IOException{
        /* comprobación de que la solicitud existe */
        if (!client.getFriendRequests().contains(usernameRequest)) {
            return;
        }
        File file = new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt");
        File tmp = new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/tmp.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
        BufferedReader br = new BufferedReader(new FileReader(file));
        String formatedUsername = usernameRequest + ";";
        String line;
        String[] separator;
        while ((line = br.readLine()) != null){
            separator = line.split(",");
            if (separator[0].equals(client.getName())){
                line = line.replaceAll(formatedUsername, "");
                separator = line.split(",");
                separator[2] = separator[2] + formatedUsername;
                line = separator[0] + "," + separator[1] +  "," + separator[2] + "," + separator[3];
                bw.write(line + "\n");
            } else if (separator[0].equals(usernameRequest)){
                line = separator[0] + "," + separator[1] +  "," + separator[2] + client.getName() + ";" + "," + separator[3];
                bw.write(line + "\n");
            } else {
                bw.write(line + "\n");
            }
        }
        /* se añade al cliente el nuevo amigo en su vector de amistades*/
        ArrayList<String> friends = client.getFriends();
        friends.add(usernameRequest);
        client.setFriends(friends);
        /* se elimina la solicitud aceptada */
        ArrayList<String> friendsRequests = client.getFriendRequests();
        friendsRequests.remove(usernameRequest);
        client.setFriendRequests(friendsRequests);

        /*  si el nuevo amigo está conectado, se actualiza su vector de amigos y su lista de amigos conectados en la interfaz
         *  y se actualizan también estas propiedades para el cliente que acepta la solicitud
         */
        ArrayList<IClient> onlineFriends;
        for (IClient iClient : clientsList){
            if (iClient.getName().equals(usernameRequest)){
                friends = iClient.getFriends();
                friends.add(client.getName());
                iClient.setFriends(friends);
                onlineFriends = iClient.getOnlineFriends();
                onlineFriends.add(client);
                iClient.setOnlineFriends(onlineFriends);
                onlineFriends = client.getOnlineFriends();
                onlineFriends.add(iClient);
                client.setOnlineFriends(onlineFriends);
                iClient.add(client.getName());
            }
        }
        client.add(usernameRequest);
        /* borrado de archivo original y renombrado del archivo temporal */
        bw.flush();
        bw.close();
        br.close();
        file.delete();
        tmp.renameTo(new File("/Users/aaronblancolopez/IdeaProjects/P2P/src/users.txt"));
    }

    /////////////// Getters y Setters ///////////////
    private void setClientsList(ArrayList<IClient> clientsList) {
        this.clientsList = clientsList;
    }
    private ArrayList<IClient> getClientsList() {
        return clientsList;
    }

}
