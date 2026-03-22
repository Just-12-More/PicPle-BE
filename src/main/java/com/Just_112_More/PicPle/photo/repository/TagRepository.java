package com.Just_112_More.PicPle.photo.repository;

import com.Just_112_More.PicPle.photo.domain.Tag;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    private final EntityManager em;

    @Transactional
    public Tag findById(Long id) {
        return em.find(Tag.class, id);
    }

    @Transactional
    public List<Tag> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return em.createQuery("FROM Tag t WHERE t.id IN :ids", Tag.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public List<Tag> findAll() {
        return em.createQuery("from Tag", Tag.class).getResultList();
    }
}
