package com.dolog.controller;


import com.dolog.domain.Post;
import com.dolog.request.PostCreate;
import com.dolog.response.PostResponse;
import com.dolog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
/**
 Http Method (https://developer.mozilla.org/ko/docs/Web/HTTP/Methods)
 Status Code (https://developer.mozilla.org/en-US/docs/Web/HTTP/Status)
 GET, HEAD, PUT, POST, DELETE, CONNECT, OPTIONS, TRACE, PATCH
 글 등록
 POST Method

 @RequestMapping(method = RequestMethod.GET, path = "v1/pass") 로 사용했었으나

 SSR을 많이 사용하시고 = jsp, thymeleaf, mustache
    html rendering
 SPA 방식도 많이 = javascript <> API(JSON)
    vue , nuxt(vue + SSR)
    react , next(react + SSR)
 */

/** 데이터를 검증하는 이유
 1. client 개발자가 깜빡할 수 있다. 실수로 값을 안보낼 수 있다.
 2. client bug로 값이 누락될 수 있다.
 3. 외부에 나쁜 사람이 값을 임의로 조작해서 보낼 수 있다.
 4. DB에 값을 저장할 때, 의도치 않은 오류가 발생할 수 도 있다.
 5. 서버 개발자의 편의를 위해서
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // BindingResult result를 제거 해줘야 controller adive로 연결가능
    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
        postService.write(request);
        // db.save(params)
    }

    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable(name = "postId") Long postId) {
        return postService.get(postId);
    }

    // 글이 1억개다 가정
    // -> 비용이 너무 많이 든다.
    // -> DB글 모두 조회하는 경우 -> DB가 뻗을 수 있다.
    // -> DB에서 서버로 전달하는 시간, 트래픽비용 많이 소비된다.
    // 따라서 페이징 처리가 필요하다.


    @GetMapping("/posts")
    public List<PostResponse> getList() {
        return postService.getList();
    }

}


/**  Case1. 저장한 데이터 Entity -> response로 응답하기
 Case2. 저장한 데이터의 primary_id -> response로 응답하기
 Client에서는 수신한 id를 post 조회 API를 통해서 글 데이터를 수신
 Case3. 응답 필요없음 -> Client에서 모든 POST 데이터 context를 관리함.
 Bad Case: 서버에서 -> 반드시 이렇게 할껍니다! fix
 -> 서버에서 차라리 유연하게 대응하는게 좋습니다.
 -> 한번에 일괄적으로 잘 처리되는 케이스가 없다.
 잘 관리하는 형태가 중요하다.*/



/**
 String title = params.getTitle();
 if (title == null || title.equals("")) {
 {"title": "  "}
 {"title": "...수십억글자..."}
 throw new Exception("타이틀 값이 없습니다");
 이렇게 검증을 하면
 1. 노가다
 2. 무언가 3번 이상 반복작업할 때 잘못한건 아닐지 의심한다.
 3. 누락될 염려가 있다.
 4. 다양한 측면의 검증을 필요로 한다.
 5. 개발자스럽지 않다.
 }
 */

/**
@PostMapping("/posts")
public Map<String, String> post(@RequestBody @Valid PostCreate params, BindingResult result) {

 이렇게 검증하면
    1. 매번 메서드마다 값을 검증 해야한다.
         > 개발자가 까먹을 수 있다. 버그 발생 여지 높다.
         > 지겹다. (간지가 안난다.
    2. 응답값에 HashMap -> 응답 클래스를 만들어 주는게 좋다.
    3. 여러개의 에러처리 힘듬
    4. 세 번 이상의 반복작업은 피해야한다.

    if (result.hasErrors()) {
        List<FieldError> fieldErrors = result.getFieldErrors();
        FieldError firstFieldError = fieldErrors.get(0);
        String fieldName = firstFieldError.getField(); //title
        String errorMessage = firstFieldError.getDefaultMessage(); // 에러 메시지

        Map<String, String> error = new HashMap<>();
        error.put(fieldName, errorMessage);
        return error;
    }
    // {"title": "타이틀 값이 없습니다"}
    return Map.of();
}
}
*/
