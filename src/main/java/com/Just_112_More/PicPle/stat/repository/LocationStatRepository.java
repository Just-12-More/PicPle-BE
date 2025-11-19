package com.Just_112_More.PicPle.stat.repository;

import com.Just_112_More.PicPle.stat.domain.LocationStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationStatRepository extends JpaRepository<LocationStat, Long> {

    @Query(value = "SELECT * FROM location_stat ORDER BY photo_cnt DESC LIMIT 10", nativeQuery = true)
    List<LocationStat> findTop10ByOrderByPhotoCntDesc();

    Optional<LocationStat> findByLocationLabel(String locationLabel);
}
