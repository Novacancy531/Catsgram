package ru.yandex.practicum.catsgram.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Service
@Data
public class PostService {
    private final HashMap<Long, Post> posts;
    private final UserService userService;


    public Collection<Post> findAll(String sort, Long from, Long size) {
        Stream<Map.Entry<Long, Post>> stream = posts.entrySet().stream()
                .filter(entry -> entry.getKey() > from && entry.getKey() < (from + 1 + size));

        if ("asc".equalsIgnoreCase(sort)) {
            return stream
                    .sorted(Comparator.comparing(entry -> entry.getValue().getPostDate()))
                    .map(Map.Entry::getValue)
                    .toList();
        } else {
            return stream
                    .sorted(Comparator.comparing((Map.Entry<Long, Post> entry) -> entry.getValue().getPostDate())
                            .reversed())
                    .map(Map.Entry::getValue)
                    .toList();
        }
    }

    public Post findPost(Long id) {
        if (id == null || !posts.containsKey(id)) {
            throw new NotFoundException("Пост не найден.");
        } else {
            return posts.get(id);
        }
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Не существующий id автора поста.");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
