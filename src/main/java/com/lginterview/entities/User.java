package com.lginterview.entities;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue
    private long Id;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
}
