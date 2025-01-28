package com.ludogorieSoft.budgetnik.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  private Type type;

  @Enumerated(EnumType.STRING)
  private Regularity regularity;

  private LocalDate creationDate;

  private BigDecimal sum;

  private String oneTimeExpense;

  private LocalDate dueDate;

  private boolean autoCreate = false;

  @OneToOne
  @JoinColumn(name = "related_expense_id")
  private Expense relatedExpense;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private ExpenseCategory category;

  @ManyToOne
  @JoinColumn(name = "owner_id")
  @JsonIgnore
  private User owner;
}
