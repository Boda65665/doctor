package com.kafka1.demo.Services.Tests.DB;

import com.kafka1.demo.DemoApplication;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.TestHelper.DB.CleanerBD;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.kafka1.demo.Repositoryes.UserRepository;
import com.kafka1.demo.Services.TestHelper.DB.UserServiceTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;

@SpringBootTest(classes = {DemoApplication.class})
class UserDbServiceTest {
    @Autowired
    private UserDbService userDbService;
    @Autowired
    private UserServiceTH userServiceTH;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CleanerBD cleanerBd;
    private final String TEST_MAIL = "test_email";

    @BeforeEach
    void clearBd() {
        cleanerBd.clearBd();
    }

    @Test
    @DisplayName("Saving a user to the database")
    void save() {
        assertEquals(0, userRepository.count());
        userServiceTH.createUser();
        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("Checking if a user exists by email")
    void existsByEmail() {
        String email1 = "user_1@mail";
        String email2 = "user_2@mail";
        String nonExistentEmail = "user_3@mail";

        userServiceTH.createUser(email1);
        assertTrue(userDbService.existsByEmail(email1));

        userServiceTH.createUser(email2);
        assertTrue(userDbService.existsByEmail(email2));

        assertFalse(userDbService.existsByEmail(nonExistentEmail));
    }

    @Test
    @DisplayName("Updating a user's email")
    void update() {
        userServiceTH.createUser("old@mail");
        User user = userDbService.findUserByEmail("old@mail");
        user.setEmail("update@mail");
        userDbService.update(user);

        assertNotNull(userDbService.findUserByEmail("update@mail"));
    }

    @Test
    @DisplayName("Finding a user by ID")
    void findById() {
        userServiceTH.createUser("user_1@mail");
        assertNotNull(userDbService.findUserById(1));
        userServiceTH.createUser("user_2@mail");
        assertNotNull(userDbService.findUserById(2));

        assertNull(userDbService.findUserById(3));
    }

    @Test
    @DisplayName("Finding a user by email")
    void findUserByEmail() {
        String email1 = "user_1@mail";
        String email2 = "user_2@mail";
        String nonExistentEmail = "user_3@mail";

        userServiceTH.createUser(email1);
        assertEquals(1,userDbService.findUserByEmail(email1).getId());
        userServiceTH.createUser(email2);
        assertEquals(2,userDbService.findUserByEmail(email2).getId());

        assertNull(userDbService.findUserByEmail(nonExistentEmail));
    }

    @Test
    @DisplayName("Checking if the password is correct")
    void isCorrectPassword() {
        String password = userServiceTH.createUserWithRandomPassword();
        assertTrue(userDbService.isCorrectPassword(TEST_MAIL,password));

        assertFalse(userDbService.isCorrectPassword(TEST_MAIL,"beleberda"));
    }

    @Test
    @DisplayName("Checking if the email validation code is correct")
    void isCorrectInputValidEmailCode() {
        int secretKey = userServiceTH.createUserWithRandomEmailKey();
        assertTrue(userDbService.isCorrectInputValidEmailCode(1, String.valueOf(secretKey)));
        assertFalse(userDbService.isCorrectInputValidEmailCode(1,"beleberda"));
    }

    @Test
    @DisplayName("Finding a user by secret key")
    void findUserBySecretKey() {
        String secretKey = userServiceTH.createUserWithRandomSecretKey();
        assertNotNull(userDbService.findUserBySecretKey(secretKey));

        assertNull(userDbService.findUserBySecretKey("beleberda"));
    }

    @Test
    @DisplayName("Checking if resending email is allowed")
    void isResendEmailAllowed() {
        User user = userServiceTH.createUserWithTimeLastSendCode(TEST_MAIL,LocalDateTime.now());
        assertFalse(userDbService.isResendEmailAllowed(TEST_MAIL));

        user.setTimeLastSendCode(LocalDateTime.now().minusMinutes(2));
        userDbService.save(user);
        assertTrue(userDbService.isResendEmailAllowed(TEST_MAIL));
    }
}