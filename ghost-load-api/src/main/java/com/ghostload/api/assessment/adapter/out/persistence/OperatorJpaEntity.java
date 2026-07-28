package com.ghostload.api.assessment.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

// Esta es la entidad de PERSISTENCIA (con @Entity), separada de la clase de
// dominio Operator.java. Vive en el adaptador porque es un detalle técnico
// de "cómo lo guardamos en PostgreSQL", no una regla de negocio.
@Entity
@Table(name = "operators")
public class OperatorJpaEntity {

    @Id
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "company_name", nullable = false, length = 160)
    private String companyName;

    @Column(length = 120)
    private String position;

    @Column(length = 100)
    private String country;

    protected OperatorJpaEntity() {
        // JPA necesita un constructor vacío
    }

    public OperatorJpaEntity(UUID id, String firstName, String lastName, String email,
                              String companyName, String position, String country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.companyName = companyName;
        this.position = position;
        this.country = country;
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getCompanyName() { return companyName; }
    public String getPosition() { return position; }
    public String getCountry() { return country; }
}
