package com.ludogorieSoft.budgetnik.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incomes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Type type;

  @Enumerated(EnumType.STRING)
  private Regularity regularity;

  private LocalDate creationDate;

  private BigDecimal sum;

  private String description;

  private LocalDate dueDate;

  @ManyToOne
  @JoinColumn(name = "related_income_id")
  private Income relatedIncome;

  @OneToMany(mappedBy = "relatedIncome")
  private List<Income> relatedIncomes;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private IncomeCategory category;

  @ManyToOne
  @JoinColumn(name = "subcategory_id")
  private Subcategory subcategory;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  @JsonBackReference
  private User owner;
}
