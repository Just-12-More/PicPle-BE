package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.domain.Tag;
import com.Just_112_More.PicPle.photo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VectorService {

    private final VectorStore vectorStore;
    private final TagRepository tagRepository;

    public void addPhoto(Photo photo) {
        List<Tag> tags = photo.getTags();

        String tagComb = getTagCombination(tags);

        List<Long> tagIds = tags.stream()
                .map(Tag::getId)
                .toList();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("tag_ids", tagIds);
        metadata.put("photo_id", photo.getId());

        Document document = new Document(tagComb, metadata);
        vectorStore.add(List.of(document));
    }

    public List<Long> findPhotoIdsByTagIds(List<Long> tagIds) {
        List<Tag> tags = tagRepository.findByIds(tagIds);
        String tagComb = getTagCombination(tags);

        SearchRequest request = SearchRequest.builder()
                .query(tagComb)
                .topK(5)
                .build();

        List<Document> results = vectorStore.similaritySearch(request);

        return results.stream()
                .map(Document::getMetadata)
                .map(metadata -> Long.valueOf(metadata.get("photo_id").toString()))
                .toList();
    }

    public String getTagCombination(List<Tag> tags) {
        return tags.stream()
                .sorted(Comparator.comparing(Tag::getName))
                .map(Tag::getName)
                .collect(Collectors.joining(", "));
    }
}
