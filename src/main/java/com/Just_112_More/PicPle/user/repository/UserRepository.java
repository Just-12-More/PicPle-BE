package com.Just_112_More.PicPle.user.repository;

import com.Just_112_More.PicPle.user.domain.LoginProvider;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public User save(User user) {
        if(user.getId()==null){
            em.persist(user);
            return user;
        } else {
            return em.merge(user);  // 실제 반영된 영속 엔티티 반환
        }
    }

    public Optional<User> findOne(Long id){
        return Optional.ofNullable(em.find(User.class, id));
    }

    public Optional<User> findByProviderAndProviderId(LoginProvider provider, String providerId) {
        List<User> resultList = em.createQuery(
                        "SELECT u FROM User u WHERE u.provider = :provider AND u.providerId = :providerId",
                        User.class
                )
                .setParameter("provider", provider)
                .setParameter("providerId", providerId)
                .getResultList();
        return resultList.stream().findFirst();
    }

    public Optional<User> findeByIdAndIsDeletedFalse(Long id) {
        List<User> resultList = em.createQuery("SELECT u From User u WHERE u.id = :id AND u.isDeleted = false", User.class)
                .setParameter("id", id)
                .getResultList();
        return resultList.stream().findFirst();
    }

    public Optional<ProfileDto> findUsernameAndProfile(Long userId) {
        List<ProfileDto> results = em.createQuery(
                "select new com.Just_112_More.PicPle.user.dto.ProfileDto(u.userName, u.profileUrl)" +
                        "from User u where u.id = :userId", ProfileDto.class)
                .setParameter("userId", userId)
                .getResultList();
        return results.stream().findFirst();
    }
}
