package ru.practicum.event.model;

import lombok.*;
import ru.practicum.categories.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "events")
@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "is_paid")
    private Boolean paid;
    @Column(name = "title")
    private String title;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "locat_lat")
    private Double lat;
    @Column(name = "locat_lon")
    private Double lon;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EventState state;

}
