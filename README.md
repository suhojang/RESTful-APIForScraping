**2020.09.08**
- docker-compose / Dockerfile 배포
  1) build(서버소스 재배포할 경우)
    - 해당 경로의 docker실행가이드 참조
  2) init(초기 was와db를 설치할 경우)
    - 해당 경로의 docker실행가이드 참조
  3) update(올라온 이미지를 pull받아 docker를 구성할 경우)
    - 해당 경로의 docker실행가이드 참조


**2020.08.31**
- docker 배포 관련 문서 수정
  1) 0.Docker_기본_가이드_1.0.1.pdf
  2) 0.Docker_배포_가이드_1.0.1.pdf

- agent exe file path를 docker volume 설정으로 변경
  => 해당 가이드는 0.Docker_배포_가이드_1.0.1.pdf 참조


**2020.08.28 최초 작성**

- 프로젝트 환경
  1) 전자정부 프레임워크 3.8.0
  2) jdk 1.8
  3) tomcat9


- 배포 환경
  1) jdk 1.8
  2) tomcat9
  3) docker
  
  
- 배포 방식
  1) docker images push/pull
  2) docker repository
    => docker-repository.scraping.co.kr:5000/agentapiserver


- 스케쥴 정보
  1) Quarz Cron 식 사용
    => 참조 사이트 : https://www.freeformatter.com/cron-expression-generator-quartz.html
  "# RESTful-APIForScraping" 
