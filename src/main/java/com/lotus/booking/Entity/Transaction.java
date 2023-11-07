package com.lotus.booking.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@Entity
@Table(name="transactions")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double amount;
    private String payBy;
    private int tip;
    private String note;
    private String date;
    private int count;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
