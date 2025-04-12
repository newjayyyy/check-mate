package Capstone.checkmate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Model {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id", updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<Inspection> inspections = new ArrayList<>();

}
