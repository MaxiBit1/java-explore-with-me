package ru.practicum.complication.model;

import lombok.*;
import ru.practicum.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Table(name = "complication")
@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Complication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "is_pinned")
    private Boolean pinned;
    @ManyToMany
    @JoinTable(name = "complication_events",
            joinColumns = {@JoinColumn(name = "complication_id")},
            inverseJoinColumns = {@JoinColumn(name = "events_id")})
    private List<Event> events;
}
