package com.Just_112_More.PicPle.photo.repository;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class PhotoRepository {

    private final EntityManager em;

    @Transactional
    public void save(Photo photo) {
        if(photo.getId()==null){
            em.persist(photo);
        } else {
            em.merge(photo);
        }
    }

    // 위치와 반경을 기준으로 근처 이미지들 조회
    @Transactional
    public List<Photo> findPhotosByLocation(double latitude, double longitude, double radiusKm) {
        String sql = "SELECT p FROM Photo p WHERE " +
                "(6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * " +
                "cos(radians(p.longitude) - radians(:lon)) + sin(radians(:lat)) * sin(radians(p.latitude)))) < :radius";

        return em.createQuery(sql, Photo.class)
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

}
