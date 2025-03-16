package com.kafka1.demo.Services.Tests.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Models.Role;
import com.kafka1.demo.Services.DB.ChatDbService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Services.Controller.ChatControllerService;
import com.kafka1.demo.Services.TestHelper.ControllerService.ChatControllerServiceTH;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.*;

public class ChatControllerServiceTest {
    @InjectMocks
    private ChatControllerService chatControllerService;
    @Mock
    private ChatDbService chatDbService;
    @Mock
    private DoctorDbService doctorDbService;
    @Mock
    private UserDbService userDbService;
    @Mock
    private BindingResult bindingResult;

    private ChatControllerServiceTH chatControllerServiceTH;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chatControllerServiceTH = new ChatControllerServiceTH(chatControllerService, doctorDbService, userDbService, chatDbService, bindingResult);
    }

    @ParameterizedTest
    @MethodSource("provideTestCasesForSendMessage")
    @DisplayName("Order Consultation - Parameterized Test")
    public void sendMessage(boolean hasErrors, boolean existsRecipient, int idSender, int idRecipient, Role roleSender, Role roleRecipient, HttpStatus exceptedStatus) {

        HttpResponseBody responseBody = chatControllerServiceTH.sendMessage(hasErrors, existsRecipient, idSender, idRecipient, roleSender, roleRecipient);

        assertEquals(exceptedStatus, responseBody.getHttpStatus());
    }

    private static Stream<Arguments> provideTestCasesForSendMessage() {
        return Stream.of(
                Arguments.of(true, false,1,1, null, null, BAD_REQUEST),
                Arguments.of(false, false,1,1, null, null, NOT_FOUND),
                Arguments.of(false, true, 1, 1, null, null, FORBIDDEN),
                Arguments.of(false, true, 1, 2, Role.USER, Role.USER, FORBIDDEN),
                Arguments.of(false, true, 1, 2, Role.DOCTOR, Role.DOCTOR, FORBIDDEN),
                Arguments.of(false, true, 1, 2, Role.DOCTOR, Role.USER, OK)
        );
    }
}
