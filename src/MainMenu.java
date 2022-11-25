import javax.swing.*;
import java.awt.event.ActionEvent;

public class MainMenu extends JFrame {

    private final IServer iServer;
    private final IClient iClient;
    private JTextField usernameTextField;
    private JButton signUpButton;
    private JButton loginButton;
    private JPanel jPanel;
    private JPasswordField passwordField;

    public MainMenu(IServer iServer, IClient iClient) {
        this.iServer = iServer;
        this.iClient = iClient;

        signUpButton.addActionListener(this::signUpButtonActionPerformed);
        loginButton.addActionListener(this::loginButtonActionPerformed);

        this.addWindowListener(new WindowEventHandler(iServer, iClient));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(jPanel);
        setSize(200, 200);
        setResizable(false);
        setLocation(100,100);
        setVisible(true);
    }

    //Manejador del botón de registro
    private void signUpButtonActionPerformed(ActionEvent actionEvent) {
        try {
            if (iServer.signUp(iClient, usernameTextField.getText().trim(), String.valueOf(passwordField.getPassword())))
                JOptionPane.showMessageDialog(null, "Usuario registrado con éxito!");
            else{
                JOptionPane.showMessageDialog(null, "Ya existe un usuario registrado con este nombre.");
            }
        } catch (Exception e){e.printStackTrace();}
    }

    // Manejador del botón de inicio de sesión
    private void loginButtonActionPerformed(ActionEvent actionEvent) {
        try {
            if (!iServer.logIn(iClient, usernameTextField.getText().trim(), String.valueOf(passwordField.getPassword())))
                JOptionPane.showMessageDialog(null, "El usuario o la contraseña son incorrectos.");
            else {
                /* se crea la pantalla de sesión iniciada y se oculta la actual*/
                LoggedMenu loggedMenu = new LoggedMenu(iServer, iClient);
                iClient.setLoggedMenu(loggedMenu);
                this.setVisible(false);
            }
        } catch (Exception e){e.printStackTrace();}
    }

}
