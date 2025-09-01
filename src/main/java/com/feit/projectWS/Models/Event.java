package com.feit.projectWS.Models;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.feit.projectWS.Models.enums.EventStatus;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "length")
    private int length;

    @Column(name = "elevationGain")
    private int elevationGain;

    @Column(name = "description", length = 1500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "eventStatus")
    private EventStatus eventStatus;

    @Column(name = "eventDate")
    private Date eventDate;

    @Column(name = "startLocation")
    private String startLocation;
    
    @Column(name = "finishLocation")
    private String finishLocation;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @ManyToMany (fetch = FetchType.EAGER)
    @JoinTable(
        name = "participants",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> participants = new ArrayList<User>();
    
    @Column(name = "created_at")
    @CreationTimestamp
    private Date created_at;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updated_at;
}
