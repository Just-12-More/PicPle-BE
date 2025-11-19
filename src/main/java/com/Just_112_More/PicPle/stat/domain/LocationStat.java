package com.Just_112_More.PicPle.stat.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "LOCATION_STAT")
public class LocationStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String locationLabel;

    private String roadAddress;

    private int photoCnt = 0;

    private LocalDateTime lastUpdateTime;

    private String representativePhotoUrl;

    public void increasePhotoCnt(){
        this.photoCnt++;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void decreasePhotoCnt(){
        if(this.photoCnt > 0) photoCnt--;
        this.lastUpdateTime = LocalDateTime.now();
    }
}
