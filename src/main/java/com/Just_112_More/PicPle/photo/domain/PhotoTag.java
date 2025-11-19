package com.Just_112_More.PicPle.photo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="photo_tag",
        uniqueConstraints = {
            @UniqueConstraint(
                    name= "UK_photo_tag_photo_id_tag_id",
                    columnNames = {"photo_id", "tag_id"}
            )
        }
)
public class PhotoTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_tag_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private Photo photo;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public PhotoTag(Photo photo, Tag tag) {
        this.photo = photo;
        this.tag = tag;
    }
}
