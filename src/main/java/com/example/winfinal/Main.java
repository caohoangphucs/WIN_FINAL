package com.example.winfinal;

import com.example.winfinal.dao.BaseDAO;
import com.example.winfinal.view.MainView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
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
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }

    private static EntityManagerFactory checkDatabaseConnection() {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("WinFinalPU");
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
