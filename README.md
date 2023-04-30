# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* InputStreamReader를 통해 소켓의 InputStream을 읽음
* InputStreamReader로 읽은 것을 BufferedReader 클래스 객체 생성 시 생성자에 넣음
* 첫번째 라인에 http method, 요청 url이 담겨 있음
	* ex) GET/index.html HTTP/1.1
* line 변수를 split 함수를 사용해서 공백을 기준으로 나눠 String 배열인 tokens 변수에 할당.
* tokens 변수의 [1] 번째 인덱스에 있는 요청 URL만 String 자료형 url 변수에 할당.
* Files와 File 클래스를 사용해서 ./webapp 경로에 있는 파일들을 byte[] 형식으로 전부 읽음.
* 읽어온 bytes 값을 DataOutputStream 클래스를 사용해 write.

```java
try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
    	InputStreamReader inputStreamReader = new InputStreamReader(in);
    	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    	
    	String line = bufferedReader.readLine();
    	
    	if(line == null) {
    		return;
    	}
    	
    	String[] tokens = line.split(" ");
    	
    	String url = tokens[1];
    	
    	log.debug("connect URL : {}", url);
    	
    	byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());
    	
    	DataOutputStream dos = new DataOutputStream(out);
        response200Header(dos, bytes.length);
        responseBody(dos, bytes);
    } catch (IOException e) {
        log.error(e.getMessage());
}
```

	-> css 파일을 적용하지 못하고 있음

### 요구사항 2 - get 방식으로 회원가입
* 회원가입 버튼을 누르면 아래와 같은 형태로 서버에 전달됨
	* /user/create?userId=testid&password=1234&name=test&email=test@gmail.com
* uri에 user/create 가 포함되어있는지 확인후 유저 정보를 DataBase에 저장

```
if(uri.contains("/create")) {
	String queryString = uri.split("\\?")[1];

	Map<String, String> userInfo = HttpRequestUtils.parseQueryString(queryString);

	String userId = userInfo.getOrDefault("userId", "");
	String password = userInfo.getOrDefault("password", "");
	String name = userInfo.getOrDefault("name", "");
	String email = userInfo.getOrDefault("email", "");

	User user = new User(userId, password, name, email);

	DataBase.addUser(user);
	log.debug("add user : {}", user.toString());

	return true;
}
```
 * 

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
