package pl.sebastianklimas.idp.users;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "roles")
@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
