# 지도깨비(Map-goblin Backend)
<img width='200' src="https://user-images.githubusercontent.com/33109677/121631660-77594900-caba-11eb-8e8d-c2057911ee5c.png">
<누구나 지도를 커스텀하고 공유할 수 있는 오픈소스 커뮤니티>


## 설치 / 빌드

---------------
```bash
    $ git clone https://github.com/MapHackers/map-goblin-backend.git
    $ cd map-goblin-backend
    $ ./gradlew bootJar
```

## 실행

------------------------------
```bash
    $ cd build/libs
    $ java -jar map-goblin-0.0.1-SNAPSHOT.jar
```

## 도메인 설계

<img width="854" alt="Screen Shot 2021-05-21 at 9 24 28 PM" src="https://user-images.githubusercontent.com/33109677/119136833-15895e80-ba7b-11eb-8b47-e34426b1226e.png">

## 객체 매핑

<img width="897" alt="Screen Shot 2021-05-21 at 9 24 37 PM" src="https://user-images.githubusercontent.com/33109677/119136836-191ce580-ba7b-11eb-8877-4a85e4d522ce.png">

## 데이터베이스 테이블 설계

<img width="894" alt="Screen Shot 2021-05-21 at 9 24 46 PM" src="https://user-images.githubusercontent.com/33109677/119136849-1b7f3f80-ba7b-11eb-9b16-d20369d7ad67.png">

## Rest API 설계

### 회원 API

`HTTP GET /api/members/`

```json
200 OK
{
  "data" : [
             {
               "id": 1,
               "userId" : "gildong123",
               "name" : "홍길동",
               "email" : "gildong@gmail.com",
               "description" : "나는 홍길동",
               "img" : "profile.png"
             },
             {
               "id": 2,
               "userId" : "cjftn333",
               "name" : "김철수",
               "email" : "cjftn@gmail.com",
               "description" : "나는 김철수",
               "img" : "profile.png"
             }
           ]
}
```
<br/>

`HTTP GET /api/members/{id}`

| Parameter | Type | Description |
|---|---|---|
| id | Long | user pk |

```json
200 OK
{
  "id": 1,
  "userId" : "gildong123",
  "name" : "홍길동",
  "email" : "gildong@gmail.com",
  "description" : "나는 홍길동",
  "img" : "profile.png"
}
```
<br/>

`HTTP POST /api/members/{id}`

| Parameter | Type | Description |
|---|---|---|
| id | Long | user pk |
| name | String | user name |
| description | String | user description |
| img | String | image file name |


```json
회원 정보 수정 성공
200 OK

회원 정보 임의 접근
400 Bad Request
{
  "message" : "잘못된 접근입니다.",
}
```
<br/>

### 지도 레포지토리 API

`HTTP GET /api/spaces`

```json
200 OK
{
  "data" : [
             {
               "id": 1,
               "name" : "꼬북칩 매장",
               "thumbnail" : "thumbnail.png",
               "description" : "꼬북칩 파는 곳 모음",
               "likeCount" : 10,
               "dislikeCount" : 2,
               "owners" : [
                            {
                              "id" : 1,
                              "userId" : "gildong123"
                            },
                            {
                              "id" : 2,
                              "userId" : "test123"
                            },
                          ]
             },
             {
               "id": 2,
               "name" : "드라이브 코스",
               "thumbnail" : "thumbnail.png",
               "description" : "경치 좋은 드라이브 코스",
               "likeCount" : 15,
               "dislikeCount" : 2,
               "owners" : [
                            {
                              "id" : 1,
                              "userId" : "gildong123"
                            },
                            {
                              "id" : 2,
                              "userId" : "test123"
                            },
                          ]
             }
           ]
}
```

<br/>

`HTTP POST /api/spaces`

| Parameter | Type | Description |
|---|---|---|
| name | String | 지도 이름 |
| thumbnail | String | 썸네일 이미지 파일 이름 |
| description | String | 지도 설명 |
| categories | String[] | 지도 카테고리 |


```json
레포지토리 생성 성공
200 OK

레포지토리 생성 실패
400 Bad Request
{
  "message" : "관련 에러 메세지",
}
```

<br/>

`HTTP GET /api/spaces/{id}`

| Parameter | Type | Description |
|---|---|---|
| id | Long | 지도 레포지토리 pk |


```json
레포지토리 조회 성공
200 OK
{
  "id": 1,
  "name" : "꼬북칩 매장",
  "thumbnail" : "thumbnail.png",
  "description" : "꼬북칩 파는 곳 모음",
  "likeCount" : 10,
  "dislikeCount" : 2,
  "owners" : [
              {
                "id" : 1,
                "userId" : "gildong123"
              },
              {
                "id" : 2,
                "userId" : "test123"
              },
            ],
  "categories" : [
                {
                  "id" : 1,
                  "name" : "동작구"
                },
                {
                  "id" : 2,
                  "name" : "맛집"
                },
              ],
}