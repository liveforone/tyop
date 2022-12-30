# TYOP
> Tell me Your Opinion, 짧은 글 기반의 social media.

# 1. 사용 기술 스택
* Spring Boot 3.0.1
* Language : Java17
* DB : MySql
* ORM : Spring Data Jpa
* Spring Security
* Spring validation
* apache commons-lang3
* LomBok
* Gradle
* jwt(api, impl, jackson)
* Junit5

# 2. 설명
* 서로 의견을 나누는 커뮤니티이다.
* 짧게 짧게 자신의 생각을 툭툭 던지듯 작성하면된다.
* 게시글은 최대 500자, 댓글은 최대 100자까지 작성가능하다.
* 게시글은 태그를 작성할 수 있다.
* 게시글 검색은 두 종류가 있는데,
* 태그를 이용하여 검색하거나, 제목으로 검색할 수 있다.
* 핫 게시판에는 하루동안 순위(조회수순)가 높은 순서대로 나열된다.
* 사용자가 작성하는 게시글과 댓글, 닉네임은 필터링 봇을 통하여 욕설과 음담패설이 걸러진다.
* 필터링 봇을 통해 분위기가 깔끔하고, 핫게시판 시스템을 통해서 화두인 글들을 쉽게 볼 수있도록 하는 것에 중심을 둔 커뮤니티라고 생각하면된다.
* 다른 사용자가 신고하여 신고된 게시글은, 문의 게시판에 올리면 운영자가 확인 후 정상적인 글이라면 복구해준다.

# 3. 설계
* 권한은 ADMIN, MEMBER, BLOCK이 있다.
* 회원관리는 jwt 토큰 기반으로 한다.
* 토큰은 1시간이 지나면 만료된다.
* 모든 유저들은 게시글 작성과 댓글 작성이 가능하다.
* 게시글은 NORMAL, BLOCK, INQUIRY 가 있다.
* 게시글에 사진은 최대 4장까지 첨부 가능하다.
* 게시글은 북마크가 가능하다.
* 게시글은 하나의 태그만 달 수 있다.
* 게시글의 조회수 순으로 핫게시판에 순위가 반영되나,
* 게시글은 하루가 지나면 핫게시판에서 사라진다.
* 댓글은 무분별한 댓글을 막기위해 필터링봇을 통해서 필터링을 거친다.
* 필터링에 통과된 댓글만 작성이 가능하며, 필터링에 걸릴경우 정지점수는 증가된다.
* 활동점수는 정지점수는 복구가 불가능하며 10점이 되는 순간 계정은 영구 정지된다.
* 정지된 계정은 복구가 불가능하다.
* 또한 필터링봇이 필터링 하지 못한 댓글과 게시글은 신고가 가능하며 신고 상태로 전환된다. 
* 댓글에는 유저를 언급할 수 있다.(프론트에서 구현, DB에 @저장가능)
* 파일 테이블과 게시글 테이블은 서로 분류하여 구분하고
* 게시글 테이블과 좋아요 테이블도 서로 분류한다.(중복 등 이유로)
* 작성자는 기본닉네임(무작위)에서 닉네임 변경시 필터링 봇으로 필터링 된다. 
* 댓글, 게시글과 마찬가지고 적발시 정지 점수가 올라간다.
* 신고된 게시글은 작성자도 보지 못하며, 문의 게시판에 문의를 올려서 복구가 가능하다.
* 이때 게시글의 번호(id)를 같이 기재해야한다.
* 운영자는 해당 게시글을 조회하여 보고 문제가 없다면 복구시킬 수 있다.
* 문의 게시글은 작성자만 볼 수 있고, 문의 게시판에서도 작성자가 작성한 모든 문의 게시글만 볼 수 있다.(다른 사람은 못봄)

## Api 설계
### Member
```
[GET] / : 홈(토큰 불필요)
[GET/POST] /member/signup : 회원가입(토큰 불필요)
[GET/POST] /member/login : 로그인(토큰 불필요)
[GET] /member/logout : 로그아웃, get으로 받아도 정상 작동(토큰 불필요)
[GET] /member/my-page : 마이페이지(토큰 필요)
[POST] /member/change-nickname : 닉네임 변경(토큰 필요), text 형식 문자열 닉네임 필요
[POST] /member/change-email : 이메일 변경, ChangeEmailRequest 형식 필요
[POST] /member/change-password : 비밀번호 변경, ChangePasswordRequest 형식 필요
[POST] /member/change-introduction : 한줄소개 변경, text 형식 문자열 한줄소개 필요
[POST] /member/withdraw : 회원탈퇴(토큰 필요), text 형식 문자열 비밀번호 필요
[GET] /admin : 어드민 페이지(토큰 필요)
[GET] /member/prohibition : 403 페이지(토큰 불필요)
```
### Board
```
[GET] /board/hot
[GET] /board/search-title
[GET] /board/search-tag
[GET] /board/my-board
[GET/POST] /board/post
[GET] /board/{id}
[POST] /board/report/{id}
[GET/POST] /board/edit/{id}
[GET] /board/inquiry
[GET/POST] /board/inquiry/post
[GET] /board/inquiry/{id}
[POST] /board/delete/{id}
```
## Json 바디 설계
### Member
```
[signup/login]
{
  "email": "yc1234@gmail.com",
  "password": "1234"
}
{
  "email": "admin@tyop.com",
  "password": "8888"
}

[change nickname] - text
new

[change password]
{
  "oldPassword": "test_488788830154",
  "newPassword": "test_56d3eedeb3b9"
}
```
### Board
```
{
  "title": "test_title",
  "content": "test_content",
  "tag": "test_tag"
}
```

# 4. 데이터베이스 설계
## 제약 조건
## 생성 및 변경 쿼리
## ER Diagram
## 테스트용 더미데이터
```
[게시글 테스트를 위한 더미데이터]
*조건* : 유저를 먼저 생성하고 해당 쿼리를 날려야한다.
insert into board(board_state, content, created_date, hit, member_id, tag, title) values("NORMAL", "content1", "2022-12-11", 1, 1, "tag1", "title1");
insert into board(board_state, content, created_date, hit, member_id, tag, title) values("NORMAL", "content2", "2022-12-27", 2, 1, "tag2", "title2");
insert into board(board_state, content, created_date, hit, member_id, tag, title) values("NORMAL", "content3", "2022-12-27", 3, 1, "tag3", "title3");
insert into board(board_state, content, created_date, hit, member_id, tag, title) values("NORMAL", "content4", "2022-12-27", 4, 1, "tag4", "title4");
insert into board(board_state, content, created_date, hit, member_id, tag, title) values("NORMAL", "content5", "2022-12-27", 5, 1, "tag5", "title5");
```

# 5. 스타일 가이드
* 함수와 긴 변수의 경우 [줄바꿈 가이드](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/b.%20%EC%A4%84%EB%B0%94%EA%BF%88%EC%9C%BC%EB%A1%9C%20%EA%B0%80%EB%8F%85%EC%84%B1%EC%9D%84%20%ED%96%A5%EC%83%81%ED%95%98%EC%9E%90.md)를 지켜 작성하라.
* 유저를 제외한 모든 객체의 [널체크](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/c.%20%EA%B0%9D%EC%B2%B4%EC%9D%98%20Null%EA%B3%BC%20%EC%A4%91%EB%B3%B5%EC%9D%84%20%EC%B2%B4%ED%81%AC%ED%95%98%EB%9D%BC.md) + 중복 체크를 꼭 하라.
* 분기문은 반드시 [게이트웨이](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/d.%20%EB%B6%84%EA%B8%B0%EB%AC%B8%EC%9D%80%20gate-way%20%EC%8A%A4%ED%83%80%EC%9D%BC%EB%A1%9C%20%ED%95%98%EB%9D%BC.md) 스타일로 하라.
* [Mapper 클래스](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/e.%20Mapper%20%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A5%BC%20%EB%A7%8C%EB%93%A4%EC%96%B4%20Entity%EC%99%80%20Dto%EB%A5%BC%20%EC%83%81%ED%98%B8%20%EB%B3%80%ED%99%98%ED%95%98%EB%9D%BC.md)를 만들어 entity와 dto를 상호 변환하라.
* 단순 for-each문은 [람다](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/f.%20%EB%8B%A8%EC%88%9C%20for-each%EB%AC%B8%EC%9D%84%20%EB%9E%8C%EB%8B%A4%EB%A1%9C%20%EB%B0%94%EA%BE%B8%EC%9E%90.md)로 작성하고, 람다식을 적극 활용하라.
* 가능하면 [dto직접조회하라](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/g.%20Dto%20%EC%A7%81%EC%A0%91%EC%A1%B0%ED%9A%8C%EB%A5%BC%20%EC%95%A0%EC%9A%A9%ED%95%98%EC%9E%90.md).
* 매직넘버는 전부 [enum](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/h.%20%EB%A7%A4%EC%A7%81%EB%84%98%EB%B2%84%EB%A5%BC%20enum%EC%9C%BC%EB%A1%9C%20%ED%95%B4%EA%B2%B0%ED%95%98%EB%9D%BC.md)으로 처리하라.
* 스프링 시큐리티에서 권한 체크 필요한것만 매핑하고 나머지(anyRequest)는 authenticated 로 설정해 코드를 줄이고 가독성 향상하라.
* [Utils 클래스](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/i.%20Util%20%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A5%BC%20%EB%A7%8C%EB%93%A4%EC%96%B4%20%ED%8E%B8%EC%9D%98%EC%84%B1%EC%9D%84%20%EB%86%92%EC%97%AC%EB%9D%BC.md)를 적극 활용하고, 서비스로직에서 트랜잭션이 걸리지 않는 로직은 Utils 클래스에 담아서 모듈화하라.
* [네이밍은 직관적이게 하라](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/j.%20%EB%84%A4%EC%9D%B4%EB%B0%8D%EC%9D%80%20%EC%A7%81%EA%B4%80%EC%A0%81%EC%9D%B4%EA%B2%8C%20%ED%95%98%EB%9D%BC.md)
* 주석은 c언어 스타일 주석으로 선언하라.
* [함수 규칙](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/k.%20%ED%95%A8%EC%88%98%20%EA%B7%9C%EC%B9%99.md)을 지켜라.
* [좋은 테스트 코드 작성법](https://github.com/liveforone/study/blob/main/%5B%EB%82%98%EB%A7%8C%EC%9D%98%20%EC%8A%A4%ED%83%80%EC%9D%BC%20%EA%B0%80%EC%9D%B4%EB%93%9C%5D/l.%20%EC%A2%8B%EC%9D%80%20%ED%85%8C%EC%8A%A4%ED%8A%B8%20%EC%9E%91%EC%84%B1%ED%95%98%EA%B8%B0.md)

# 6. 상세 설명
## Authorization 헤더 설정하면 안되는 api
* 회원가입, 로그인, 로그아웃
* 프론트엔드와 postman에서 테스트 할때 모두 해당 api들에는 Authorization 헤더를 넣지말아라.
따라서 위의 api들은 꼭! authorization 헤더를 넣지말자.
## 파일 생성 전략
```
파일이름(saveFileName) = random uuid + "_" + originalFileName
경로 : c:\temp\upload
```
## 로그아웃
* Http Method는 get이다.
## 회원가입
* 회원가입을 하면 바로 로그인이 자동으로 이루어진다.

# 7. 나의 고민
## 양방향 관계 및 OneToMany
* 양방향 관계를 최대한 지양하고 OneToMany를 사용하는 것을 지양하도록 했다.
* 이유는 양방향관계를 사용하면 어플리케이션이 더 복잡해지고,
* 뷰에 값을 보낼때 원하는 방식으로 보내기 어려워서이다.
* 나는 오로지 다대일 단방향만을 사용하고,
* 클라이언트에게 값을 보낼때에는 각각의 리파지토리에서 값을 가져온후 dto형태로 변환하고
* map으로 모아서 보내는 방식을 선택했다.
* 이렇게 하면 {객체1, {객체2,{객체3..}}} 과 같은 복잡하고 계속 참조해야하는 json방식에서
* {객체1}, {객체2}... 과 같은 형식으로 복잡하지 않고 단일한 객체 형식의 json을 유지할 수 있기 때문이다.
## 조회 쿼리 우선순위
* 조회쿼리에서는 우선순위가 중요하다.
* 특히 like 쿼리로 검색을 할때에 더욱 그렇다.
* 이번 프로젝트에서 게시글은 3가지 상태(state)를 가진다.
* like 쿼리는 풀스캔을 하기 때문에 성능상 떨어진다.
* 모든 데이터를 like로 키워드 검색을 하는 것보단
* state가 NORMAL, 즉 일반적인 상태(신고나, 문의 게시글이 아닌)의 글만 가져온 후에 
* 키워드 검색을 하게 된다면 더욱 성능상에 큰 이점을 가져온다.
* 이런 사례가 아니더라도 like처럼 성능에 영향을 미치거나 불필요하게 많은 데이터를 가져올 때에는
* and 를 이용해서 걷어낼 조건을 붙이고 필요한 데이터만 가져오는 것이 좋다.
## 다중 파일 - List<MultipartFile>
* 다중파일을 매개변수로 넣고 클라이언트로부터 입력을 받으면
* 아무 값이 입력되지 않아도 empty 상태가 아니다.
* 0번째 인덱스가 add 되어있고, 오리지날네임은 "" 비어있고,
* 컨텐츠 타입은 null로 나온다.
* 그러나 size로 배열의 길이를 출력하면 1이 출력된다.
* 즉 빈 값이 0번째 인덱스에 기본적으로 들어가게 된다.
* 따라서 배열이 비어있는지, 즉 사용자가 파일을 업로드 하지 않았는지를 체크하려면
* originalName == "" 으로 체크하거나, contentType == null 로 체크하면된다.
* 나는 이중에 contentType == null로 체크하는 함수를 만들어 사용했다.
## cascade와 같은 옵션
* cascade 옵션은 사실 좀 위험하다.
* 그리고 코드로 해결할 수 있는 일은 코드로 해결하는 것이 좋다.
* 데이터베이스에서 constraints를 거는것은 별로 좋지 않은 방법인것 같다.
* 또한 db자체에서 무언가를 수정하고 변경하고 삭제하는 것은 위험한 일이니 지양하자.
* cascade를 대체하는 방법은 간단하다.
* 삭제할 one 엔티티를 삭제하고 해당 id를 fk로 가지고 있는 데이터를 모두 찾아 삭제하면된다.


- 엔티티 네이밍
member : 활동 점수, 신고 수마다 카운트, 이름, 한줄소개
board : (오늘의 순위로 뽑는것, 그룹핑 쿼리로 날짜 처리하고 조회수 desc로 가져오면됨), 신고기능, 게시글 삭제시 파일도 같이 삭제해야한다.
comment : 신고 기능, 필터링, 언급 기능
Bookmark : 북마크 기능

```
SET foreign_key_checks = 0;
drop table 테이블명;
SET foreign_key_checks = 1;
으로 외래키 제약조건 해제하고 테이블 삭제 가능
```


댓글
댓글 조회시 상태로 조회하는 것 잊지말기(신고 댓글을 가져오면 안된다.)
댓글 신고 -> state가 신고로 변경
게시글이 삭제되면 댓글도 삭제됨