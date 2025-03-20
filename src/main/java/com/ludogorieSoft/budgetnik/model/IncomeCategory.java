package com.ludogorieSoft.budgetnik.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "income_category")
public class IncomeCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String name;
  private String bgName;

  @Column(columnDefinition = "jsonb")
  private String translations;

  @OneToMany(
      mappedBy = "incomeCategory",
      fetch = FetchType.EAGER,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<Subcategory> subcategories;
}
