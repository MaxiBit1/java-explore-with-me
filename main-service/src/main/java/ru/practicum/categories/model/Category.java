package ru.practicum.categories.model;

import lombok.*;

import javax.persistence.*;

@Table(name = "categories")
@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name_category", unique = true)
    private String name;
}
