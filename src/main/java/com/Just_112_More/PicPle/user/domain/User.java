package com.Just_112_More.PicPle.user.domain;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.security.oauth.OAuthUserInfo;
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
@Table(name = "picple_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LoginProvider provider;

    private String userName;

    private String providerId;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String userImageUrl;

    // @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isDeleted = false;

    private LocalDateTime userCreate;

    private LocalDateTime deletedAt;

    private String profileUrl;

    @OneToMany(mappedBy="user")
    private List<Photo> userPhotos = new ArrayList<>();

    @OneToMany(mappedBy="user")
    private List<Like> userLikes = new ArrayList<>();

    private User(Long id, String email, String providerId, LoginProvider provider, Role role, LocalDateTime joinedAt) {
        this.id = id;
        this.email = email;
        this.providerId = providerId;
        this.provider = provider;
        this.role = role;
        this.userCreate = joinedAt;
    }

    protected User() {
    }

    public static User fromOAuth(OAuthUserInfo info) {
        return new User(
                null,
                info.getEmail(),
                info.getProviderId(),
                info.getProvider(),
                Role.USER,
                LocalDateTime.now()
        );
    }

    // 엔티티가 저장되기 전에 userCreate 자동 설정
//    @PrePersist
//    public void prePersist() {
//        if (this.userCreate == null) {
//            this.userCreate = LocalDateTime.now();
//        }
//    }

    //논리 삭제 메서드
    public void deleteUser() {
        if (this.isDeleted) {
            throw new IllegalStateException("User is already deleted");
        }
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // 재가입
    public void reactivate() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    public boolean isValid() {
        return !isDeleted || (deletedAt != null && deletedAt.isBefore(LocalDateTime.now()));
    }

}
