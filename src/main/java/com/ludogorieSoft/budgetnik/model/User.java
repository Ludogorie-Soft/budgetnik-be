package com.ludogorieSoft.budgetnik.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ludogorieSoft.budgetnik.model.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"incomes", "expenses"})
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @OneToOne(cascade = CascadeType.ALL)
  private Subscription subscription;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ExpoPushToken> exponentPushTokens;

  @NotNull(message = "The name should not be null!")
  private String name;

  @Email
  @NotNull(message = "The email should not be null!")
  @Column(unique = true)
  private String email;

  private String password;
  private LocalDateTime createdAt;
  private String customerId;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Role role;

  @Getter
  @Column(name = "activated")
  private boolean activated = false;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  @ToString.Exclude
  private List<Income> incomes;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  @ToString.Exclude
  private List<Expense> expenses;

  @ManyToMany
  @JoinTable(
      name = "user_promo_messages",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "promo_message_id"))
  List<Message> promoMessages;

  @ManyToMany
  @JoinTable(
      name = "user_system_messages",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "system_message_id"))
  private List<SystemMessage> systemMessages;

  private LocalDateTime lastLogin;
  private String appRating;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public String toString() {
    return "User{id=" + id + ", name=" + name + ", email=" + email + ", role=" + role + "}";
  }
}
