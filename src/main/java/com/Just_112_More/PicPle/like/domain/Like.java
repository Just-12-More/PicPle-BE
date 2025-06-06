package com.Just_112_More.PicPle.like.domain;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = "photo_like")
public class Like {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;
}
