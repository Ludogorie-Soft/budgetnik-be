package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.ContactForm;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<ContactForm, UUID> {}
