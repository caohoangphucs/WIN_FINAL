package com.example.winfinal;

import com.example.winfinal.dao.BaseDAO;
import com.example.winfinal.view.LoginView;
import com.example.winfinal.view.MainView;
import com.formdev.flatlaf.FlatLightLaf;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Apply modern FlatLaf theme (must be before any Swing component creation)
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 6);
        } catch (Exception ignored) {}

        EntityManagerFactory emf = checkDatabaseConnection();
        if (emf == null) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "chua bat database ong noi oi, chay db bang docker di",
                        "Database Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            });
            return;
        }

        // Share the EMF with DAOs
        BaseDAO.setEntityManagerFactory(emf);

        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView(() -> {
                MainView mainView = new MainView();
                mainView.setVisible(true);
            });
            loginView.setVisible(true);
        });
    }

    private static EntityManagerFactory checkDatabaseConnection() {
        try {
            java.util.Map<String, String> properties = new java.util.HashMap<>();
            String dbUrl = System.getenv("DB_URL");
            if (dbUrl != null && !dbUrl.isEmpty()) {
                properties.put("jakarta.persistence.jdbc.url", dbUrl);
            }
            
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("WinFinalPU", properties);
            EntityManager em = emf.createEntityManager();
            em.createNativeQuery("SELECT 1").getSingleResult();
            em.close();
            return emf;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
