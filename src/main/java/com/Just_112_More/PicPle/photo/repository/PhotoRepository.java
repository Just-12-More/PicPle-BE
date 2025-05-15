package com.Just_112_More.PicPle.photo.repository;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PhotoRepository {

    private final EntityManager em;

    public void save(Photo photo) {
        if(photo.getId()==null){
            em.persist(photo);
        } else {
            em.merge(photo);
        }
    }

    // 위치와 반경을 기준으로 근처 이미지들 조회
    public List<Photo> findPhotosByLocation(double latitude, double longitude, double radius){
        String jpql = "SELECT p FROM Photo p WHERE ST_DWithin(p.latitude, p.longitude, :latitude, :longitude, :radius)";
        return em.createQuery(jpql, Photo.class)
                .setParameter("latitude", latitude)
                .setParameter("longitude", longitude)
                .setParameter("radius", radius)
                .getResultList();
    }

    // 이미지 삭제 (DB 레코드에서만 삭제처리)
    public void deleteById(Long photoId) {
        Photo photo = em.find(Photo.class, photoId);
        if (photo != null) {
            em.remove(photo);
        }
    }

}
