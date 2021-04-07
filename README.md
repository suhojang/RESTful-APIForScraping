### Scraping을 위한 RESTFull API Server

+ INTRO
  + Client(Client Side)와 정보 동기화 및 Task 조회/수행결과 정보 동기화를 목적으로 개발

+ ETC
  + Developer 환경
    + 전자정부 프레임워크 3.8.0
    + jdk 1.8
    + tomcat9

  + Server 환경
    + jdk 1.8
    + tomcat9
    + docker
  
  + Deploy 방식
    + docker images push/pull
    + docker-compose up

  + 스케쥴 정보
    + Quarz Cron 식 사용
      => [참조 사이트](https://www.freeformatter.com/cron-expression-generator-quartz.html)

