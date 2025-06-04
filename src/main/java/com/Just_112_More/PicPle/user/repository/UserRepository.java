package com.Just_112_More.PicPle.user.repository;

import com.Just_112_More.PicPle.user.domain.LoginProvider;
import com.Just_112_More.PicPle.user.domain.User;
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

    public Optional<User> findByUsername(String username) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.userName = :username", User.class)
                .setParameter("username", username)
                .getResultList();
        return result.stream().findAny();
    }

    public Optional<User> findByProviderAndProviderId(LoginProvider provider, String providerId) {
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.provider = :provider AND u.providerId = :providerId",
                            User.class
                    )
                    .setParameter("provider", provider)
                    .setParameter("providerId", providerId)
                    .getSingleResult();

            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findeByIdAndIsDeletedFalse(Long id){
        List<User> resultList = em.createQuery("SELECT u From User u WHERE u.id = :id AND u.isDeleted = false", User.class)
                .setParameter("id", id)
                .getResultList();
        return resultList.stream().findFirst();
    }
}
