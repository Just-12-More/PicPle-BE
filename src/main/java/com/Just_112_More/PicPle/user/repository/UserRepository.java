package com.Just_112_More.PicPle.user.repository;

import com.Just_112_More.PicPle.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user) {
        if(user.getId()==null){
            em.persist(user);
        } else {
            em.merge(user);
        }
    }

    public User findOne(Long id){
        return em.find(User.class, id);
    }

    public Optional<User> findByUsername(String username) {
        List<User> result = em.createQuery("SELECT u FROM User u WHERE u.userName = :username", User.class)
                .setParameter("username", username)
                .getResultList();
        return result.stream().findAny();
    }

}
