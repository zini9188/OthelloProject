# OthelloProject
해당 프로젝트는 네트워크 프로그래밍 수업에서 Java 소켓 프로그래밍 실습으로 진행한 1인 텀프로젝트입니다.

메이플 스토리의 배틀 리버스를 참고하여 이미지를 구성했습니다.

GameSever 실행 후, 해당 서버에 접속하는 클라이언트는 게임 방을 생성하고 참여하여 오셀로 게임을 진행할 수 있습니다.

---

## 기능

- 로비 기능

- 방 생성 (8x8, 10x10, 12x12)
- 채팅 기능
- 무르기 기능
- 자신과 상대방의 돌의 개수를 표시하는 기능

|로비|방 생성|
|---|---|
|![image](https://user-images.githubusercontent.com/83208807/231812928-2924ba13-8194-4aa6-bbbb-98262e32bb27.png) ![image](https://user-images.githubusercontent.com/83208807/231813017-3d6033c7-d36c-45a9-803a-a9e264d8bb79.png) | ![image](https://user-images.githubusercontent.com/83208807/231813465-d58dc204-f546-4630-880b-dfd746abef5f.png) ![image](https://user-images.githubusercontent.com/83208807/231813633-e956db1c-d472-481d-b996-ffd57394fa74.png)
|유저가 접속하는 순서대로 로비에 입장 메세지가 전달됩니다.| 방 생성을 누르면 방의 이름을 설정할 수 있고 방의 사이즈를 고를 수 있습니다.


| 방 생성이 완료되는 경우 | 방 인원의 제한|
|---|---|
| ![image](https://user-images.githubusercontent.com/83208807/231813915-a6fc86a8-ac2e-48e1-b5a3-796e94344da6.png) ![image](https://user-images.githubusercontent.com/83208807/231813991-308d98f9-c1fe-41a8-9708-06266cdbc701.png) | ![image](https://user-images.githubusercontent.com/83208807/231811891-03211346-ca89-4188-b12b-1a03e6480726.png)
| 방은 접속해있는 유저에게 모두 보여지며 유저는 빈 방에 입장할 수 있습니다. | 방의 인원이 모두 차면 유저는 해당 방에 들어갈 수 없습니다.

| 게임 입장 | 돌 두기|
|---|---|
|![image](https://user-images.githubusercontent.com/83208807/231815240-702dbb07-c544-4dd9-a6c3-75a4caef71ef.png) ![image](https://user-images.githubusercontent.com/83208807/231815126-9cbb6f17-18fd-43bb-ab36-e0f8209c1a64.png) |![image](https://user-images.githubusercontent.com/83208807/231814795-b548cb12-3792-45c5-883f-4f7862695c1b.png) ![image](https://user-images.githubusercontent.com/83208807/231814913-b8d6c43c-f02c-4871-a683-0eadd370fea7.png)
|빨간색 동그라미로 표시된 구역에 방의 이름이 표시되며 초기 게임은 슬라임 돌 2개, 핑크빈 돌 2개로 이루어집니다. <br> 게임은 핑크빈 돌을 가지는 유저가 선공을 얻게 됩니다. |  돌을 두게 되면 8방향으로 자신의 돌을 검사하게 되고, 사이에 있는 상대방의 돌은 자신의 돌이 됩니다.

|무르기 | 무르기 수락 |
|---|---|
|![image](https://user-images.githubusercontent.com/83208807/231816154-9125b260-39c3-43c8-adf4-021aeb0aa028.png) ![image](https://user-images.githubusercontent.com/83208807/231816270-5cb8a0e7-e8e0-47ca-b321-928cb02d57e4.png) | ![image](https://user-images.githubusercontent.com/83208807/231816433-380c15b0-dbd3-42c3-a06a-47431954a000.png) ![image](https://user-images.githubusercontent.com/83208807/231816575-5f67e0d3-c585-47f9-ba66-4404319a2ee2.png)
|자신의 차례인 경우 무르기를 요청할 수 있습니다. | 무르기 요청을 수락하는 경우 이전 상태로 돌아가게 됩니다. 이후 돌을 다시 둘 수 있게 됩니다.

|게임 나가기 |
|---|
|![image](https://user-images.githubusercontent.com/83208807/231817507-5e87850e-af63-42e4-a038-8ff255b0fa39.png) <br> ![image](https://user-images.githubusercontent.com/83208807/231817584-f7dd4117-7cc4-4bb4-a16a-d6e9271d6449.png) ![image](https://user-images.githubusercontent.com/83208807/231817646-862002ce-16b5-4088-8cec-a66ba574d755.png)
|1번 방의 유저들이 모두 나가기를 눌러 게임을 나가는 경우 게임 방은 삭제됩니다. 



## 기획

- 시스템 구성도

![](https://user-images.githubusercontent.com/83208807/231809120-60eb20ce-4631-4f3c-ad61-90eb8fa050a5.png)

- 시스템 흐름도

![](https://user-images.githubusercontent.com/83208807/231809702-3b619438-5835-494c-8cd4-fdddc0e42ee0.png)

## 아쉬운 점

- 게임의 승패를 구현하지 못한 것

- 복기 기능을 구현하지 못한 것

- 관전 기능이 없는 것

- UI 품질이 별로인 점
