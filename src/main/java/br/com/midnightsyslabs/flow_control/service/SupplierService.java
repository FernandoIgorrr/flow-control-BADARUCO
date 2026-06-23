package br.com.midnightsyslabs.flow_control.service;

import java.time.OffsetDateTime;
import java.util.InputMismatchException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.config.Constants;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.City;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.CompanyPartner;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.PartnerRole;
import br.com.midnightsyslabs.flow_control.domain.entity.partner.PersonalPartner;
import br.com.midnightsyslabs.flow_control.exception.IllegalEmailArgumentException;
import br.com.midnightsyslabs.flow_control.exception.InvalidCNPJException;
import br.com.midnightsyslabs.flow_control.exception.InvalidCPFException;
import br.com.midnightsyslabs.flow_control.repository.partner.CompanyPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PartnerRoleRepository;
import br.com.midnightsyslabs.flow_control.repository.partner.PersonalPartnerRepository;
import br.com.midnightsyslabs.flow_control.repository.view.ProductFullRepository;
import br.com.midnightsyslabs.flow_control.repository.view.SupplierRepository;
import br.com.midnightsyslabs.flow_control.view.SupplierView;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final PersonalPartnerRepository personalPartnerRepository;
    private final CompanyPartnerRepository companyPartnerRepository;
    private final PartnerRoleRepository partnerRoleRepository;

    public SupplierService(
            SupplierRepository supplierRepository,
            CompanyPartnerRepository companyPartnerRepository,
            PersonalPartnerRepository personalPartnerRepository,
            PartnerRoleRepository partnerRoleRepository) {
        this.supplierRepository = supplierRepository;
        this.companyPartnerRepository = companyPartnerRepository;
        this.personalPartnerRepository = personalPartnerRepository;
        this.partnerRoleRepository = partnerRoleRepository;

    }

    public void saveSupplier(String name,
            String document,
            String phone,
            String email,
            City city,
            String partnerCategory) {

        PartnerRole role = partnerRoleRepository.findById(Constants.PARTNER_ROLE_SUPPLIER)
                .orElseThrow();

        dataValidations(
                document,
                email,
                partnerCategory);

        if (partnerCategory.equals(Constants.PERSONAL)) {
            var supplier = new PersonalPartner(null, name,
                    document.isBlank() ? null : document,
                    phone,
                    email,
                    city,
                    role,
                    null, null,false);
            personalPartnerRepository.save(supplier);
        } else {

            var supplier = new CompanyPartner(null, name,
                    document.isBlank() ? null : document,
                    phone,
                    email,
                    city,
                    role,
                    null, null,false);
            companyPartnerRepository.save(supplier);
        }
    }

    public void updatePersonalSupplier(
            PersonalPartner supplier,
            String name,
            String CPF,
            String phone,
            String email,
            City city) {

        dataValidations(
                CPF,
                email,
                Constants.PERSONAL);

        supplier.setName(name);
        supplier.setCpf(CPF.isBlank() ? null : CPF);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setCity(city);

        personalPartnerRepository.save(supplier);

    }

    public List<SupplierView> getSuppliers() {
        return supplierRepository.findAll();
    }

    public void updateCompanySupplier(
            CompanyPartner supplier,
            String name,
            String CNPJ,
            String phone,
            String email,
            City city) {

        dataValidations(
                CNPJ,
                email,
                Constants.COMPANY);

        supplier.setName(name);
        supplier.setCnpj(CNPJ.isBlank() ? null : CNPJ);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setCity(city);

        companyPartnerRepository.save(supplier);

    }

    public void deletePersonalSupplier(PersonalPartner supplier) {
        supplier.setDeletedAt(OffsetDateTime.now());
        personalPartnerRepository.save(supplier);

    }

    public void deleteCompanySupplier(CompanyPartner supplier) {
        supplier.setDeletedAt(OffsetDateTime.now());
        companyPartnerRepository.save(supplier);
    }

    private void dataValidations(String document,
            String email,
            String partnerCategory) {

        if (email != null && !email.isBlank() && !isValidEmail(email)) {
            throw new IllegalEmailArgumentException();
        }

        String documentWithoutMask = document.replaceAll("\\D", "");

        if (partnerCategory.equals(Constants.PERSONAL)) {

            if (!cPFValidator(documentWithoutMask)) {
                throw new InvalidCPFException();
            }

        } else {
            if (!cNPJValidator(documentWithoutMask)) {
                throw new InvalidCNPJException("CNPJ: " + documentWithoutMask);
            }
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean cPFValidator(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return true;
        }

        // Deve ter 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Não pode ser todos os dígitos iguais
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        try {
            int sum = 0;

            // Primeiro dígito verificador
            for (int i = 0; i < 9; i++) {
                int digit = cpf.charAt(i) - '0';
                sum += digit * (10 - i);
            }

            int firstCheck = (sum * 10) % 11;
            if (firstCheck == 10)
                firstCheck = 0;

            if (firstCheck != (cpf.charAt(9) - '0')) {
                return false;
            }

            // Segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                int digit = cpf.charAt(i) - '0';
                sum += digit * (11 - i);
            }

            int secondCheck = (sum * 10) % 11;
            if (secondCheck == 10)
                secondCheck = 0;

            return secondCheck == (cpf.charAt(10) - '0');

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean cNPJValidator(String CNPJ) {

        if (CNPJ == null || CNPJ.isBlank()) {
            return true;
        }

        // considera-se erro CNPJ's formados por uma sequencia de numeros iguais
        if (CNPJ.equals("00000000000000") || CNPJ.equals("11111111111111") ||
                CNPJ.equals("22222222222222") || CNPJ.equals("33333333333333") ||
                CNPJ.equals("44444444444444") || CNPJ.equals("55555555555555") ||
                CNPJ.equals("66666666666666") || CNPJ.equals("77777777777777") ||
                CNPJ.equals("88888888888888") || CNPJ.equals("99999999999999") ||
                (CNPJ.length() != 14))
            return (false);

        char dig13, dig14;
        int sm, i, r, num, peso;

        // "try" - protege o código para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                // converte o i-ésimo caractere do CNPJ em um número:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posição de '0' na tabela ASCII)
                num = (int) (CNPJ.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else
                dig13 = (char) ((11 - r) + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int) (CNPJ.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - r) + 48);

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13)))
                return (true);
            else
                return (false);
        } catch (InputMismatchException erro) {
            return (false);
        }

    }
}
