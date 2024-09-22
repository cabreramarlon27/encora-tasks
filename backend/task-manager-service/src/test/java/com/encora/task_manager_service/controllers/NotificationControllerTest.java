package com.encora.task_manager_service.controllers;

import com.encora.task_manager_service.models.Task;
import com.encora.task_manager_service.models.TaskStatus;
import com.encora.task_manager_service.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.messaging.simp.stomp.StompHeaders;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationControllerTest {
    @LocalServerPort
    private int port;
    @MockBean
    private TaskRepository taskRepository;
    @Autowired
    private NotificationController notificationController;
    private WebSocketStompClient stompClient;
    private Task task1;
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task1 = new Task("1", "Test Task 1", "Description 1", LocalDate.now(), TaskStatus.TODO, "testuser");
        authentication = new UsernamePasswordAuthenticationToken("testuser", "testpassword", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        this.stompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    //TODO: Complete this later
//    @Test
//    public void testSendPrivateNotification() throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        AtomicReference<Throwable> failure = new AtomicReference<>();
//
//        // --- Authentication Setup ---
//        StompHeaders connectHeaders = new StompHeaders();
//        connectHeaders.add("Authorization", "Bearer " + obtainJwtToken());
//        // ---------------------------
//
//        StompSessionHandler handler = new StompSessionHandlerAdapter() {
//            @Override
//            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                session.subscribe("/user/topic/notifications", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return String.class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        TestSessionHandler.this.latch.countDown();
//                    }
//                });
//                session.send("/app/private", "test message");
//            }
//
//            @Override
//            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
//                failure.set(exception);
//                latch.countDown();
//            }
//
//            @Override
//            public void handleTransportError(StompSession session, Throwable exception) {
//                failure.set(exception);
//                latch.countDown();
//            }
//
//            @Override
//            public void beforeConnect(StompSession session, StompHeaders connectHeaders) {
//                // Add the authentication headers here
//                connectHeaders.putAll(this.connectHeaders);
//            }
//        };
//
//        // Connect to the WebSocket endpoint with authentication headers
//        this.stompClient.connect("ws://localhost:" + port + "/ws", connectHeaders, handler);
//
//        if (latch.await(3, TimeUnit.SECONDS)) {
//            if (failure.get() != null) {
//                throw new AssertionError("", failure.get());
//            }
//        } else {
//            fail("Greeting not received");
//        }
//    }

    private String obtainJwtToken() throws Exception {
        // Assuming you have a /login endpoint that returns a JWT token
        String loginUrl = "http://localhost:" + port + "/api/auth/login"; // Adjust the path if needed

        // Create login request body
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser"); // Replace with a valid test user
        loginRequest.put("password", "testpassword"); // Replace with the correct password

        // Create RestTemplate for making the request
        RestTemplate restTemplate = new RestTemplate();

        // Set headers for the request (if needed)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity with body and headers
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(loginRequest, headers);

        // Send the login request
        ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, requestEntity, Map.class);

        // Check for successful login
        if (response.getStatusCode() == HttpStatus.OK) {
            // Extract JWT token from the response
            String token = response.getBody().get("token").toString();
            return token;
        } else {
            throw new Exception("Login failed with status code: " + response.getStatusCode());
        }
    }

}
