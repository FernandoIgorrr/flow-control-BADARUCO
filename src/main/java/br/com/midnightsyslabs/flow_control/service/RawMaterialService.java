package br.com.midnightsyslabs.flow_control.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.midnightsyslabs.flow_control.repository.RawMaterialRepository;
import br.com.midnightsyslabs.flow_control.domain.entity.raw_material.RawMaterial;
import br.com.midnightsyslabs.flow_control.exception.RawMaterialNotFoundException;

@Service
public class RawMaterialService {
    private final RawMaterialRepository rawMaterialRepository;

    public RawMaterialService(
            RawMaterialRepository rawMaterialRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public void saveRawMaterial(
            String name,
            String description) {
        var rawMaterial = new RawMaterial();

        rawMaterial.setName(name);
        rawMaterial.setDescription(description);
        rawMaterial.setCreatedAt(OffsetDateTime.now());

        rawMaterialRepository.save(rawMaterial);
    }

    public void editRawMaterial(RawMaterial rawMaterial, String name, String description){
        rawMaterial.setName(name);
        rawMaterial.setDescription(description);

        rawMaterialRepository.save(rawMaterial);
    }

    public void deleteRawMaterial(RawMaterial rawMaterial){
        rawMaterial.setDeletedAt(OffsetDateTime.now());

        rawMaterialRepository.save(rawMaterial);
    }

    public List<RawMaterial> getRawMaterials(){
        return rawMaterialRepository.findAll();
    }

    public Optional<RawMaterial> getRawMaterialById(Short id){
        return rawMaterialRepository.findById(id);
    }

    public RawMaterial getRawMaterialByName(String name){
        return rawMaterialRepository.findByName(name).orElseThrow(RawMaterialNotFoundException::new);
    }

}
