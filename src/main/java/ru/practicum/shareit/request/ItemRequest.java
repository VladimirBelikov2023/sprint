package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    @NotBlank
    @Column(name = "description", nullable = false, length = 4000)
    private String description;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;
    @NotNull
    @Column(name = "created", nullable = false)
    private LocalDateTime created = LocalDateTime.now();
}
