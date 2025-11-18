package com.Just_112_More.PicPle.photo.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tag",
        uniqueConstraints = {
            @UniqueConstraint(
                    name= "UK_tag_name_type",
                    columnNames = {"name", "type"}
            )
        },
        indexes = {
            @Index(name = "IDX_tag_name", columnList = "name")
        }
)
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TagType tagType;

    @Column(nullable = false, length = 50)
    private String name;
}
