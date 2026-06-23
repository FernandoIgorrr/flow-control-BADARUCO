package br.com.midnightsyslabs.flow_control.service;

import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

@Service
public class NavigationService {

    private final ConfigurableApplicationContext springContext;
    private Pane mainContent; // O container onde as telas vão aparecer

    public NavigationService(ConfigurableApplicationContext springContext) {
        this.springContext = springContext;
    }

    // Define qual é o container principal (chamado no initialize do MainController)
    public void setMainContainer(Pane container) {
        this.mainContent = container;
    }

    public void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent newScreen = loader.load();
            
            // Limpa o que tinha antes e coloca a nova tela
            mainContent.getChildren().setAll(newScreen);
            
            // Faz a nova tela ocupar todo o espaço do container
            if (newScreen instanceof Region region) {
                region.prefWidthProperty().bind(mainContent.widthProperty());
                region.prefHeightProperty().bind(mainContent.heightProperty());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}