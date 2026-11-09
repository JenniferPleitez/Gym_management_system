package com.app.gym.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRole;

    @Column(unique = true, nullable = false)
    private String roleName; // e.g. ROLE_USER, ROLE_ADMIN


    // getters / setters
    public Long getIdRole() { 
        return idRole; 
    }

    public void setIdRole(Long idRole) { 
        this.idRole = idRole; 
    }

    public String getRoleName() { 
        return roleName; 
    }

    public void setRoleName(String roleName) { 
        this.roleName = roleName; 
    }
}
