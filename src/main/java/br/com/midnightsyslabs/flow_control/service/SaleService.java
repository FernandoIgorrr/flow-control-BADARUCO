package br.com.midnightsyslabs.flow_control.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.partner.Partner;
import br.com.midnightsyslabs.flow_control.domain.entity.product.Product;
import br.com.midnightsyslabs.flow_control.domain.entity.sale.Sale;
import br.com.midnightsyslabs.flow_control.domain.entity.sale.SaleProduct;
import br.com.midnightsyslabs.flow_control.dto.SaleDTO;
import br.com.midnightsyslabs.flow_control.exception.SaleNotFoundException;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.product.ProductRepository;
import br.com.midnightsyslabs.flow_control.repository.sale.SaleRepository;
import br.com.midnightsyslabs.flow_control.repository.view.SaleProductViewRepository;
import br.com.midnightsyslabs.flow_control.repository.view.SaleViewRepository;
import br.com.midnightsyslabs.flow_control.ui.controller.form.ProductRow;
import br.com.midnightsyslabs.flow_control.view.SaleView;
import jakarta.transaction.Transactional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleViewRepository saleViewRepository;

    @Autowired
    private SaleProductViewRepository saleProductViewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Transactional
    public void saveSale(
            List<ProductRow> productsRow,
            Partner partner,
            LocalDate date) {

        var sale = new Sale();
        List<SaleProduct> salesProduct = new ArrayList<>();

        partner.setConfirmed(true);

        sale.setClient(partner);
        sale.setDate(date);
        sale.setCreatedAt(OffsetDateTime.now());

        try {
            for (var row : productsRow) {
                SaleProduct saleProduct = new SaleProduct();
                saleProduct.setSale(sale);

                Product product = row.productComboBox.getValue();

                product.setConfirmed(true);
                saleProduct.setProduct(product);
                saleProduct
                        .setQuantity(new BigDecimal(UtilsService.solveComma(row.productQuantitySoldField.getText())));

                salesProduct.add(saleProduct);
                productRepository.save(product);
            }
        } catch (Exception e) {
            throw e;
        }
        sale.setSaleProducts(salesProduct);
        partnerRepository.save(partner);
        saleRepository.save(sale);
    }

    public Optional<Sale> findById(Integer id) {
        return saleRepository.findById(id);
    }

    @Transactional
    public void deleteSale(Sale sale) {
        sale.setDeletedAt(OffsetDateTime.now());
        saleRepository.save(sale);
    }

    @Transactional
    public void confirmSale(Sale sale) {
        sale.setConfirmed(true);
        saleRepository.save(sale);
    }

    public List<SaleDTO> getSalesDTO() {

        var salesView = saleViewRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SaleView::getDate)
                        .reversed()
                        .thenComparing(SaleView::getId, Comparator.reverseOrder()));

        var salesDTO = salesView.map(SaleDTO::new).toList();

        salesDTO.forEach(sDTO -> {

            var spvs = saleProductViewRepository.findAllBySaleId(sDTO.getId());
            sDTO.setSaleProductsView(spvs);
        });

        return salesDTO;
    }

    public List<SaleDTO> searchBetween(LocalDate start, LocalDate end) {
        return getSalesDTO().stream().filter(sDTO -> !sDTO.getDate().isBefore(start) && !sDTO.getDate().isAfter(end))
                .toList();
    }

    public BigDecimal calculateTotalRevenue(List<SaleDTO> SalesDTO) {
        return SalesDTO.stream()
                .map(SaleDTO::getRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
