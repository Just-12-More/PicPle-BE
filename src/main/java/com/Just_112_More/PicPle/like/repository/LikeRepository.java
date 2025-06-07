package com.Just_112_More.PicPle.like.repository;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private final EntityManager em;

    public void save(Like like) {
        if(like.getId()==null){
            em.persist(like);
        } else {
            em.merge(like);
        }
    }

    public void deleteByUserAndPhoto(User user, Photo photo) {
        String jpql = "SELECT l FROM Like l WHERE l.user = :user AND l.photo = :photo";
        Like like = em.createQuery(jpql, Like.class)
                .setParameter("user", user)
                .setParameter("photo", photo)
                .getSingleResult();
        em.remove(like);
    }

    public List<Like> findLikesWithPhotoByUserId(Long userId) {
        return em.createQuery(
                        "SELECT l FROM Like l JOIN FETCH l.photo WHERE l.user.id = :userId", Like.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
