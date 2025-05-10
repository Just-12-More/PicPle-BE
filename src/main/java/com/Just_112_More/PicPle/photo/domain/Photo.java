package com.Just_112_More.PicPle.photo.domain;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString(exclude = "user, photoLikes")
public class Photo {

    @Id @GeneratedValue
    @Column(name="photo_id")
    private Long id;

    private String photoTitle;

    @Column(columnDefinition = "TEXT")
    private String photoDesc;

    private String photoUrl;

    private Double latitude;  // 위도

    private Double longitude; // 경도

    private String locationLabel;

    private int likeCount = 0;

    private LocalDateTime photoCreate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL)
    private List<Like> photoLikes = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.photoCreate == null) {
            this.photoCreate = LocalDateTime.now();
        }
    }

    // 좋아요 수 계산 메서드
    public void calculateLikeCount() {
        // 좋아요 리스트의 사이즈로 자동 계산
        this.likeCount = this.photoLikes.size();
    }

    // 좋아요 추가
    public void addLike(Like like) {
        this.photoLikes.add(like);
        calculateLikeCount();  // 좋아요 수 동기화
    }

    // 좋아요 삭제
    public void removeLike(Like like) {
        this.photoLikes.remove(like);
        calculateLikeCount();  // 좋아요 수 동기화
    }

    // 사진위치완성
    public String getMapUrl() {
        return "https://www.google.com/maps?q=" + this.latitude + "," + this.longitude;
    }

}
