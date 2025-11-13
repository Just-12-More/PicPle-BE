package com.Just_112_More.PicPle.stat.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "LOCATION_STAT")
public class LocationStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String locationLabel;

    private int photoCnt = 0;

    private LocalDateTime lastUpdateTime;

    public void increasePhotoCnt(){
        this.photoCnt++;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void decreasePhotoCnt(){
        if(this.photoCnt > 0) photoCnt--;
        this.lastUpdateTime = LocalDateTime.now();
    }
}
