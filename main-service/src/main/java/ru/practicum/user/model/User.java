package ru.practicum.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "users")
@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    @NotNull
    @NotBlank
    private String name;
    @Column(name = "email", unique = true)
    @NotNull
    @NotBlank
    @Email
    private String email;
    @ManyToMany
    @JoinTable(name = "followers",
            joinColumns = {@JoinColumn(name = "follower_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private List<User> follower;
    @ManyToMany(mappedBy = "follower")
    private List<User> follow;
}
