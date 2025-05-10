package com.ludogorieSoft.budgetnik.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class CommunityLink {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String link;
}
