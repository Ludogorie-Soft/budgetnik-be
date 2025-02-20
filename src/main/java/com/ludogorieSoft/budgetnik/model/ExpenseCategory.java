package com.ludogorieSoft.budgetnik.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class ExpenseCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;
  private String bgName;

  @OneToMany(
      mappedBy = "expenseCategory",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Subcategory> subcategories;
}
