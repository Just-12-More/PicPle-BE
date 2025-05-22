package com.Just_112_More.PicPle.user.domain;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.photo.domain.Photo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@ToString(exclude = "userLikes")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoginProvider provider;

    private String userName;

    private String userImageUrl;

    private boolean isDeleted = false;

    private LocalDateTime userCreate;

    private LocalDateTime deletedAt;

    private String profileUrl;

    @OneToMany(mappedBy="user")
    private List<Photo> userPhotos = new ArrayList<>();

    @OneToMany(mappedBy="user")
    private List<Like> userLikes = new ArrayList<>();

    // 엔티티가 저장되기 전에 userCreate 자동 설정
    @PrePersist
    public void prePersist() {
        if (this.userCreate == null) {
            this.userCreate = LocalDateTime.now();
        }
    }

    //논리 삭제 메서드
    public void deleteUser() {
        if (this.isDeleted) {
            throw new IllegalStateException("User is already deleted");
        }
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return !isDeleted || (deletedAt != null && deletedAt.isBefore(LocalDateTime.now()));
    }

}
