package com.kafka1.demo.Services.TestHelper.ControllerService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Body.SchedulesBody;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Repositoryes.DoctorSchedulesRepository;
import com.kafka1.demo.Services.ApiService.DoctorControllerService;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Zoom.ZoomApi;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


public class DoctorControllerServiceTH {
    private final DoctorControllerService doctorControllerService;
    private final DoctorDbService doctorDbService;
    private final UserDbService userDbService;
    private final ZoomApi zoomApi;
    private final BindingResult bindingResult;
    private final DoctorSchedulesRepository doctorSchedulesRepository;
    private final int TEST_DOCTOR_ID = 1;
    private final String TEST_USER_EMAIL = "test_mail";
    private final LocalDate DAY_ORDER = LocalDate.of(2025, 1, 1);

    public DoctorControllerServiceTH(DoctorControllerService doctorControllerService, DoctorDbService doctorDbService, UserDbService userDbService, ZoomApi zoomApi, BindingResult bindingResult, DoctorSchedulesRepository doctorSchedulesRepository) {
        this.doctorControllerService = doctorControllerService;
        this.doctorDbService = doctorDbService;
        this.userDbService = userDbService;
        this.zoomApi = zoomApi;
        this.bindingResult = bindingResult;
        this.doctorSchedulesRepository = doctorSchedulesRepository;
    }

    public HttpResponseBody orderConsultation(boolean haveFreeTime, String timeReceptionString, String longSessionInMinutesString, DoctorSchedulesDTO freeTime,
                                              int userBalance, boolean isSuccessfulSessionCreation){
        mockFindDoctorDtoById();
        mockGetFreeTime(haveFreeTime, freeTime);
        mockFindUserByEmail(userBalance);
        mockCreateZoomSession(isSuccessfulSessionCreation);
        return doctorControllerService.orderConsultation(TEST_DOCTOR_ID, TEST_USER_EMAIL, timeReceptionString, longSessionInMinutesString, DAY_ORDER);
    }

    private void mockCreateZoomSession(boolean isSuccessfulSessionCreation) {
        final String TEST_URL = "test_url";
        String url = null;
        if (isSuccessfulSessionCreation) url = TEST_URL;
        if (isSuccessfulSessionCreation) when(zoomApi.createZoomSession()).thenReturn(url);
    }

    private void mockFindDoctorDtoById() {
        final int TEST_PRICE = 1;
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setPricePerMinute(TEST_PRICE);
        when(doctorDbService.findDoctorDtoById(TEST_DOCTOR_ID)).thenReturn(doctorDTO);
    }

    private void mockGetFreeTime(boolean haveFreeTime, DoctorSchedulesDTO freeTime) {
        if (!haveFreeTime) freeTime = null;
        when(doctorDbService.getFreeTime(DAY_ORDER, TEST_DOCTOR_ID)).thenReturn(freeTime);
    }

    private void mockFindUserByEmail(int userBalance) {
        User user = new User();
        user.setBalance(userBalance);
        when(userDbService.findUserByEmail(TEST_USER_EMAIL)).thenReturn(user);
    }

    public HttpResponseBody validEditSchedules(boolean hasErrors, boolean isNullPrefix, String timeStartString, String timeEndString, String dayOfWeekString) {
        mockBindingResult(hasErrors);

        String prefix = getPrefix(isNullPrefix);
        SchedulesBody schedulesBody = generateTestSchedulesBody(timeStartString, timeEndString, dayOfWeekString);
        mockFindSchedulesByDoctorAndDayAndTimeAfter();
        mockFindById();

        DoctorDTO mockDoctor = generateMockDoctor();

        return doctorControllerService.editSchedules(schedulesBody, prefix, bindingResult, mockDoctor);
    }

    private void mockBindingResult(boolean hasErrors) {
        when(bindingResult.hasErrors()).thenReturn(hasErrors);
    }

    private String getPrefix(boolean isNullPrefix) {
        if (isNullPrefix) return null;
        return "mock_prefix";
    }

    private SchedulesBody generateTestSchedulesBody(String timeStartString, String timeEndString, String dayOfWeekString) {
        SchedulesBody schedulesBody = new SchedulesBody();

        schedulesBody.setTimeStartString(timeStartString);
        schedulesBody.setTimeEndString(timeEndString);
        schedulesBody.setDayOfWeekString(dayOfWeekString);

        return schedulesBody;
    }

    private void mockFindSchedulesByDoctorAndDayAndTimeAfter() {
        mockFindSchedulesByDoctorAndDayAndTimeAfter(new ArrayList<>());
    }

    private void mockFindSchedulesByDoctorAndDayAndTimeAfter(ArrayList<TimeInterval> timeIntervals) {
        DoctorSchedulesDTO doctorSchedulesDTO = new DoctorSchedulesDTO();
        doctorSchedulesDTO.setTimeIntervals(timeIntervals);

        when(doctorDbService.findSchedulesByDoctorAndDayAndTimeAfter(anyInt(),anyInt(), any())).thenReturn(doctorSchedulesDTO);
    }

    private void mockFindById() {
        DayOfWeek mockDayOfWeek = DayOfWeek.MONDAY;
        DoctorSchedules doctorSchedules = new DoctorSchedules();
        doctorSchedules.setDayOfWeek(mockDayOfWeek);

        when(doctorSchedulesRepository.findById(anyInt())).thenReturn(doctorSchedules);
    }

    private DoctorDTO generateMockDoctor() {
        final int TEST_DOCTOR_ID = 1;
        DoctorDTO doctorDTO = new DoctorDTO();
        doctorDTO.setId(TEST_DOCTOR_ID);

        return doctorDTO;
    }
        
    public HttpResponseBody validScheduleOverlap(String timeStart, String timeEnd, TimeInterval timeInterval) {
        mockBindingResult(false);
        
        String mockDayOfWeek = "1";
        SchedulesBody schedulesBody = generateTestSchedulesBody(timeStart, timeEnd, mockDayOfWeek);
        
        DoctorDTO mockDoctor = generateMockDoctor();

        ArrayList<TimeInterval> timeIntervals = generateListTimeIntervals(timeInterval);

        mockFindSchedulesByDoctorAndDayAndTimeAfter(timeIntervals);

        mockFindById();

        return doctorControllerService.editSchedules(schedulesBody, "mock",  bindingResult, mockDoctor);
    }

    private ArrayList<TimeInterval> generateListTimeIntervals(TimeInterval timeInterval) {
        if (timeInterval==null) return new ArrayList<>();

        ArrayList<TimeInterval> timeIntervals = new ArrayList<>();
        timeIntervals.add(timeInterval);

        return timeIntervals;
    }
}
