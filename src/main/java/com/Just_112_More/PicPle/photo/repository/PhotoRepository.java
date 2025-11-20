package com.Just_112_More.PicPle.photo.repository;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.domain.Tag;
import com.Just_112_More.PicPle.photo.dto.HotTagDto;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class PhotoRepository {

    private final EntityManager em;

    @Transactional
    public Photo save(Photo photo) {
        if(photo.getId()==null){
            em.persist(photo);
            return photo;
        } else {
            return em.merge(photo);
        }
    }

    // 위치와 반경을 기준으로 근처 이미지들 조회
//    @Transactional
//    public List<Photo> findPhotosByLocation(double latitude, double longitude, double radiusKm) {
//        String sql = "SELECT p FROM Photo p WHERE " +
//                "(6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * " +
//                "cos(radians(p.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(p.latitude)))) < :radius";
//
//        return em.createQuery(sql, Photo.class)
//                .setParameter("lat", latitude)
//                .setParameter("lon", longitude)
//                .setParameter("radius", radiusKm)
//                .getResultList();
//    }

    @Transactional
    public List<Photo> findPhotosByLocation(double latitude, double longitude, double radiusKm) {
        // 1도 ≈ 111km, 위도 1도는 항상 동일하지만 경도는 위도에 따라 달라짐
        double latRange = radiusKm / 111.0;
        double lonRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));

        String sql = "SELECT p FROM Photo p WHERE " +
                "p.latitude BETWEEN :minLat AND :maxLat " +
                "AND p.longitude BETWEEN :minLon AND :maxLon " +
                "AND (6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * " +
                "cos(radians(p.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(p.latitude)))) < :radius";

        return em.createQuery(sql, Photo.class)
                // Bounding Box 파라미터
                .setParameter("minLat", latitude - latRange)
                .setParameter("maxLat", latitude + latRange)
                .setParameter("minLon", longitude - lonRange)
                .setParameter("maxLon", longitude + lonRange)
                // 거리 계산용 파라미터
                .setParameter("lat", latitude)
                .setParameter("lon", longitude)
                .setParameter("radius", radiusKm)
                .getResultList();
    }


    @Transactional
    public List<Photo> findPhotosByLocation(String locationLabel, long photo_id) {
        String sql = "SELECT p FROM Photo p WHERE p.locationLabel = :locationLabel AND p.id != :photo_id";

        return em.createQuery(sql, Photo.class)
                .setParameter("locationLabel", locationLabel)
                .setParameter("photo_id", photo_id)
                .getResultList();
    }


    @Transactional
    public Photo getPhotoById(long id) {
        String sql = "SELECT p FROM Photo p WHERE p.id = :id";
        try {
            return em.createQuery(sql, Photo.class)
                    .setParameter("id", id)
                    .getSingleResult(); // 단일 결과 반환
        } catch (NoResultException e) {
            return null; // ID로 찾는 결과가 없으면 null 반환
        }
    }

    // 이미지 삭제 (DB 레코드에서만 삭제처리)
    @Transactional
    public void deleteById(Long photoId) {
        Photo photo = em.find(Photo.class, photoId);
        if (photo != null) {
            em.remove(photo);
        }
    }

    public List<Photo> findByLocationLabel(String locationLabel) {
        String sql = "SELECT p FROM Photo p WHERE p.locationLabel = :locationLabel";
        return em.createQuery(sql, Photo.class)
                .setParameter("locationLabel", locationLabel).getResultList();
    }

    public List<Photo> findByTagIdsIn(List<Long> tagIds) {
        if(tagIds==null || tagIds.isEmpty()){
            return List.of();
        }

        return em.createQuery(
                "select distinct p " +
                        "from Photo p " +
                        "join p.photoTags pt " +
                        "join pt.tag t " +
                        "where t.id in :tagIds", Photo.class)
                .setParameter("tagIds", tagIds)
                .getResultList();
    }

    public List<HotTagDto> countPhotoTags() {
        List<Object[]> result = em.createQuery(
                "select t, count(t) " +
                        "from Tag t, PhotoTag pt " +
                        "where t.id = pt.tag.id " +
                        "group by t.id " +
                        "order by count(t) desc " +
                        "limit 3", Object[].class)
                .getResultList();

        return result.stream()
                .map(o -> new HotTagDto((Tag)o[0], ((Long)o[1]).intValue()))
                .collect(Collectors.toList());
    }
}
