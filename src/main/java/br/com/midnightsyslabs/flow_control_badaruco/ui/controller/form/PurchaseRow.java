package br.com.midnightsyslabs.flow_control_badaruco.ui.controller.form;

import br.com.midnightsyslabs.flow_control_badaruco.view.PurchaseView;
import javafx.scene.control.TextField;
import lombok.Setter;

// Dentro da ProductionFormController
@Setter
public class PurchaseRow {
    public PurchaseView purchaseView;
    public TextField purchaseQuantityUsedField;

    PurchaseRow(PurchaseView purchaseView, TextField purchaseQuantityUsedField) {
        this.purchaseView = purchaseView;
        this.purchaseQuantityUsedField = purchaseQuantityUsedField;
    }


}