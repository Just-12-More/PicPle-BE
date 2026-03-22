package com.Just_112_More.PicPle.photo.domain;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Table(name = "photo", indexes = {
        @Index(name = "idx_location", columnList = "latitude, longitude")
})
@Setter
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
    private String roadAddress;

    private int likeCount = 0;
    private LocalDateTime photoCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
    private List<Like> photoLikes = new ArrayList<>();

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhotoTag> photoTags = new HashSet<>();

    @Builder
    public Photo(String photoTitle, String photoDesc, String photoUrl,
                 Double latitude, Double longitude, String locationLabel, String roadAddress) {
        this.photoTitle = photoTitle;
        this.photoDesc = photoDesc;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationLabel = locationLabel;
        this.roadAddress = roadAddress;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void updateAddress(String locationLabel, String roadAddress ) {
        this.locationLabel = locationLabel;
        this.roadAddress = roadAddress;
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

    public List<Tag> getTags() {
        return this.photoTags.stream()
                .map(PhotoTag::getTag)
                .collect(Collectors.toList());
    }

    public void addTag(Tag tag) {
        if (tag == null || tag.getId() == null) {
            throw new IllegalArgumentException("Tag must be persisted.");
        }

        PhotoTag photoTag = new PhotoTag(this, tag);
        this.photoTags.add(photoTag);
    }

    public void addTags(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) return;
        for (Tag tag : tags) {
            addTag(tag);
        }
    }

    public void removeTag(Tag tag) {
        if(tag == null || tag.getId() == null) return;
        this.photoTags.removeIf(photoTag -> photoTag.getTag().getId().equals(tag.getId()));
    }

    public String getMapUrl() {
        return "https://www.google.com/maps?q=" + this.latitude + "," + this.longitude;
    }
}