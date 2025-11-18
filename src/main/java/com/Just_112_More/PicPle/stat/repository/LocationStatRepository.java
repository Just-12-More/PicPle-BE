package com.Just_112_More.PicPle.stat.repository;

import com.Just_112_More.PicPle.stat.domain.LocationStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationStatRepository extends JpaRepository<LocationStat, Long> {

    @Query(value = "SELECT * FROM location_stat ORDER BY photo_cnt DESC LIMIT 10", nativeQuery = true)
    List<LocationStat> findTop10ByOrderByPhotoCntDesc();

    Optional<LocationStat> findByLocationLabel(String locationLabel);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO location_stat (location_label, road_address, photo_cnt, last_update_time)
        VALUES (:locationLabel, :roadAddress, 1, NOW())
        ON DUPLICATE KEY UPDATE
        photo_cnt = photo_cnt + 1,
        last_update_time = NOW()
    """, nativeQuery = true)
    void upsertStat(
            @Param("locationLabel") String locationLabel,
            @Param("roadAddress") String roadAddress
    );
}
