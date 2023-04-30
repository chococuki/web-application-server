package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	InputStreamReader inputStreamReader = new InputStreamReader(in);
        	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        	
        	String line = bufferedReader.readLine();
        	
        	if(line == null) {
        		return;
        	}
        	
        	String[] tokens = line.split(" ");
        	
        	String uri = tokens[1];
        	
        	if(checkLink(uri)) {
        		return;
        	}
        	
        	log.debug("connect URL : {}", uri);
        	
        	Path path = Paths.get("./webapp", uri);
        	if(!Files.exists(path)) {
        		log.debug("path not exist : {}", path);
        		return;
        	}
        	
        	byte[] bytes = Files.readAllBytes(new File("./webapp" + uri).toPath());
        	
        	DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, bytes.length);
            responseBody(dos, bytes);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private boolean checkLink(String uri) {
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
    	
    	return false;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
