package com.lotus.booking.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Data
@Entity
@Table(name="checkin")
@AllArgsConstructor
@NoArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private String phone;
    private String service;
    private String appt;
    private String request;
    @CreationTimestamp
    private Date date;
}
