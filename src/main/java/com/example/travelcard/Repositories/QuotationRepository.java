package com.example.travelcard.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.travelcard.Entities.Quotation;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    
}
