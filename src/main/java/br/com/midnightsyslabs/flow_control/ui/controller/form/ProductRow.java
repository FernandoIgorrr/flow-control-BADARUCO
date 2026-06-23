package br.com.midnightsyslabs.flow_control.ui.controller.form;

import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ProductRow {
    public ComboBox<Product> productComboBox;
    public TextField productQuantitySoldField;

    ProductRow(ComboBox<Product> productComboBox, TextField productQuantitySoldField) {
        this.productComboBox = productComboBox;
        this.productQuantitySoldField = productQuantitySoldField;
    }
}
