package com.ghostload.api.assessment.domain.model;

// Esta es la entidad de DOMINIO. Ojo: NO tiene @Entity, NO tiene @Id,
// NO sabe nada de bases de datos. Es una clase Java pura que representa
// al operador dentro de las reglas del negocio.
public final class Operator {

    private final OperatorId id;
    private final String firstName;
    private final String lastName;
    private final Email email;
    private final String companyName;
    private final String position;   // puede ser null, es opcional
    private final String country;    // puede ser null, es opcional

    private Operator(OperatorId id, String firstName, String lastName, Email email,
                      String companyName, String position, String country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.companyName = companyName;
        this.position = position;
        this.country = country;
    }

    // "Factory method": así se crea un Operator nuevo. Ponemos las validaciones
    // acá para que sea imposible crear un Operator en un estado inválido.
    public static Operator register(String firstName, String lastName, Email email,
                                     String companyName, String position, String country) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (companyName == null || companyName.length() < 2) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio");
        }
        return new Operator(OperatorId.newId(), firstName, lastName, email, companyName, position, country);
    }

    // Para reconstruir un Operator que YA existía en la base de datos
    // (con su id original, no uno nuevo generado).
    public static Operator reconstruct(OperatorId id, String firstName, String lastName, Email email,
                                        String companyName, String position, String country) {
        return new Operator(id, firstName, lastName, email, companyName, position, country);
    }

    public OperatorId id() { return id; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public Email email() { return email; }
    public String companyName() { return companyName; }
    public String position() { return position; }
    public String country() { return country; }
}
