package com.ludogorieSoft.budgetnik.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Date date;
    private String title;
    private Date fromDate;
    private Date toDate;
    private String message;
    private double discount;
    private String link;
}
