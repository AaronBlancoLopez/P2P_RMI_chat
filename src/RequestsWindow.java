import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class RequestsWindow extends JFrame {

    private final JList<String> jList;
    private final DefaultListModel<String> defaultListModel;

    public RequestsWindow(IClient iClient, IServer iServer) {

        JPanel jPanel = new JPanel();
        defaultListModel = new DefaultListModel<>();

        /* se añaden las solicitudes pentientes a la lista */
        try {
            for (String request : iClient.getFriendRequests()) {
                defaultListModel.addElement(request);
            }
        } catch (Exception e){e.printStackTrace();}

        jList = new JList<>(defaultListModel);
        jList.setBounds(100,100, 75,75);

        /* al hacer click en una celda se ofrece la opción de aceptar la solicitud */
        jList.addMouseListener( new MouseAdapter() {
            @Deprecated
            public void mousePressed(MouseEvent e) {
                if ( SwingUtilities.isLeftMouseButton(e) ) {
                    try {
                        int resp = JOptionPane.showConfirmDialog(null, "¿Aceptar solicitud?");
                        if (resp == 0) {
                            iServer.acceptFriendRequest(iClient, defaultListModel.getElementAt(getRow(e.getPoint())));
                            defaultListModel.removeElement(defaultListModel.getElementAt(getRow(e.getPoint())));
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        jPanel.add(jList);

        this.add(jPanel);
        setSize(500,500);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    //Función que añade un elemento a la JList
    public void addToList(String username){
        this.defaultListModel.addElement(username);
    }

    //Función que devuelve el índice de la JList en el que se encuentra el cursor
    private int getRow(Point point) {
        return jList.locationToIndex(point);
    }


}
