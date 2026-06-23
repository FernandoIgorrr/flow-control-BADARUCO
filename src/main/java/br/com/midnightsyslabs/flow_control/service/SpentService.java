package br.com.midnightsyslabs.flow_control.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.domain.entity.spent.Spent;
import br.com.midnightsyslabs.flow_control.domain.entity.spent.SpentCategory;
import br.com.midnightsyslabs.flow_control.repository.spent.SpentRepository;
import jakarta.transaction.Transactional;

@Service
public class SpentService {

    @Autowired
    private SpentRepository spentRepository;

    @Transactional
    public void saveSpent(
            String amountPaid,
            SpentCategory spentCategory,
            String spentDescription,
            LocalDate date) {

        var spent = new Spent();
        spent.setAmountPaid(new BigDecimal(UtilsService.solveComma(amountPaid)));
        spent.setCategory(spentCategory);
        spent.setDescription(spentDescription);
        spent.setDate(date);
        spent.setCreatedAt(OffsetDateTime.now());

        spentRepository.save(spent);

    }
    @Transactional
    public void updateSpent(Spent spent,String amountPaid,SpentCategory category,String description, LocalDate date){
       
        var amountPaid_ = UtilsService.solveComma(amountPaid);

        try{
            spent.setAmountPaid(new BigDecimal(amountPaid_));
        } catch (Exception e){
            throw e;
        }

        spent.setCategory(category);
        spent.setDescription(description);
        spent.setDate(date);

        spentRepository.save(spent);
    }

    @Transactional
    public void deleteSpent(Spent spent){
        spent.setDeletedAt(OffsetDateTime.now());
        spentRepository.save(spent);
    }
    @Transactional
    public void confirmSpent(Spent spent){
        spent.setConfirmed(true);;
        spentRepository.save(spent);
    }

    public List<Spent> getSpents(){
        return spentRepository.findAll();
    }

    public Optional<Spent> findById(Integer id){
        return spentRepository.findById(id);
    }
}
