import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//CLASE MANEJADORA DEL CIERRE DE LA VENTANA PRINCIPAL
public class WindowEventHandler extends WindowAdapter {
    IServer iServer;
    IClient iClient;

    public WindowEventHandler(IServer iServer, IClient iClient) {
        this.iServer = iServer;
        this.iClient = iClient;
    }

    //Función manejadora. Al cerrar la ventana, primero se asegura el cierre de sesión del usuario
    public void windowClosing(WindowEvent evt) {
        try {
            iServer.logOut(iClient);
        } catch (Exception e){e.printStackTrace();}
    }
}

