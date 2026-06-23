package br.com.midnightsyslabs.flow_control.ui.controller.form;

import br.com.midnightsyslabs.flow_control.view.PurchaseView;
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