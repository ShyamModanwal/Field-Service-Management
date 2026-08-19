package com.fieldservicemanagement.fieldservicemanagement.service;

import com.fieldservicemanagement.fieldservicemanagement.dto.PartRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.PartResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Part;
import com.fieldservicemanagement.fieldservicemanagement.repository.PartRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    // =========================================================
    // CREATE PART
    // =========================================================

    public PartResponseDTO createPart(
            PartRequestDTO requestDTO) {

        // Part number duplicate check
        if (partRepository
                .findByPartNumber(requestDTO.getPartNumber())
                .isPresent()) {

            throw new RuntimeException(
                    "Part with this part number already exists");
        }

        // Stock negative nahi ho sakta
        if (requestDTO.getStockQuantity() == null
                || requestDTO.getStockQuantity() < 0) {

            throw new RuntimeException(
                    "Stock quantity cannot be negative");
        }

        // Price negative nahi ho sakti
        if (requestDTO.getUnitPrice() == null
                || requestDTO.getUnitPrice() < 0) {

            throw new RuntimeException(
                    "Unit price cannot be negative");
        }

        Part part = new Part();

        part.setPartNumber(
                requestDTO.getPartNumber());

        part.setName(
                requestDTO.getName());

        part.setDescription(
                requestDTO.getDescription());

        part.setUnitPrice(
                requestDTO.getUnitPrice());

        part.setStockQuantity(
                requestDTO.getStockQuantity());

        Part savedPart = partRepository.save(part);

        return convertToResponseDTO(savedPart);
    }

    // =========================================================
    // GET ALL PARTS
    // =========================================================

    public List<PartResponseDTO> getAllParts() {

        return partRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    // =========================================================
    // GET PART BY ID
    // =========================================================

    public PartResponseDTO getPartById(Long id) {

        Part part = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Part not found with id: " + id));

        return convertToResponseDTO(part);
    }

    // =========================================================
    // UPDATE PART
    // =========================================================

    public PartResponseDTO updatePart(
            Long id,
            PartRequestDTO requestDTO) {

        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Part not found with id: " + id));

        // Agar part number change ho raha hai
        if (!existingPart.getPartNumber()
                .equals(requestDTO.getPartNumber())) {

            if (partRepository
                    .findByPartNumber(
                            requestDTO.getPartNumber())
                    .isPresent()) {

                throw new RuntimeException(
                        "Part with this part number already exists");
            }
        }

        if (requestDTO.getStockQuantity() == null
                || requestDTO.getStockQuantity() < 0) {

            throw new RuntimeException(
                    "Stock quantity cannot be negative");
        }

        if (requestDTO.getUnitPrice() == null
                || requestDTO.getUnitPrice() < 0) {

            throw new RuntimeException(
                    "Unit price cannot be negative");
        }

        existingPart.setPartNumber(
                requestDTO.getPartNumber());

        existingPart.setName(
                requestDTO.getName());

        existingPart.setDescription(
                requestDTO.getDescription());

        existingPart.setUnitPrice(
                requestDTO.getUnitPrice());

        existingPart.setStockQuantity(
                requestDTO.getStockQuantity());

        Part updatedPart = partRepository.save(existingPart);

        return convertToResponseDTO(updatedPart);
    }

    // =========================================================
    // DELETE PART
    // =========================================================

    public void deletePart(Long id) {

        if (!partRepository.existsById(id)) {

            throw new RuntimeException(
                    "Part not found with id: " + id);
        }

        partRepository.deleteById(id);
    }

    // =========================================================
    // ENTITY → RESPONSE DTO
    // =========================================================

    private PartResponseDTO convertToResponseDTO(
            Part part) {

        PartResponseDTO responseDTO = new PartResponseDTO();

        responseDTO.setId(
                part.getId());

        responseDTO.setPartNumber(
                part.getPartNumber());

        responseDTO.setName(
                part.getName());

        responseDTO.setDescription(
                part.getDescription());

        responseDTO.setUnitPrice(
                part.getUnitPrice());

        responseDTO.setStockQuantity(
                part.getStockQuantity());

        return responseDTO;
    }
}