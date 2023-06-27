package com.dolog.service;

import com.dolog.domain.Post;
import com.dolog.repository.PostRepository;
import com.dolog.request.PostCreate;
import com.dolog.response.PostResponse;
import org.assertj.core.internal.Lists;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
class PostServiceTest {

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("글 작성")
    void test1() {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //when
        postService.write(postCreate);

        //then
        Assertions.assertEquals(1L, postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2() {
        //given
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();
        postRepository.save(requestPost);

        //when
        PostResponse response = postService.get(requestPost.getId());

        //then
        assertNotNull(response);
        assertEquals(1L, postRepository.count());
        assertEquals("foo", response.getTitle());
        assertEquals("bar", response.getContent());
    }


    @Test
    @DisplayName("글 여러개 조회")
    void getMultiTest() {
        //given
        Post requestPost1;
        postRepository.saveAll(List.of(
                Post.builder()
                        .title("foo")
                        .content("bar")
                        .build(),
                Post.builder()
                        .title("foo1")
                        .content("bar1")
                        .build()
        ));

        //when
        List<PostResponse> posts = postService.getList();

        //then
        assertNotNull(posts);
        assertEquals(2L, posts.size());
    }
}
