package com.Just_112_More.PicPle.photo.domain;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = {"user", "photoLikes"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Photo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="photo_id")
    private Long id;

    private String photoTitle;

    @Column(columnDefinition = "TEXT")
    private String photoDesc;

    private String photoUrl;

    private Double latitude;
    private Double longitude;
    private String locationLabel;

    private int likeCount = 0;
    private LocalDateTime photoCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
    private List<Like> photoLikes = new ArrayList<>();

    @Builder
    public Photo(String photoTitle, String photoDesc, String photoUrl,
                 Double latitude, Double longitude, String locationLabel) {
        this.photoTitle = photoTitle;
        this.photoDesc = photoDesc;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationLabel = locationLabel;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        if (this.photoCreate == null) {
            this.photoCreate = LocalDateTime.now();
        }
    }

    public void calculateLikeCount() {
        this.likeCount = this.photoLikes.size();
    }

    public void addLike(Like like) {
        this.photoLikes.add(like);
        calculateLikeCount();
    }

    public void removeLike(Like like) {
        this.photoLikes.remove(like);
        calculateLikeCount();
    }

    public String getMapUrl() {
        return "https://www.google.com/maps?q=" + this.latitude + "," + this.longitude;
    }
}