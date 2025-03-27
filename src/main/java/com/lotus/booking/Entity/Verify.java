package com.lotus.booking.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "verify")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Verify {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private boolean verified;

    @ManyToOne(targetEntity = User.class, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
