package com.kafka1.demo.Services.ApiService;

import com.kafka1.demo.Body.HttpResponseBody;
import com.kafka1.demo.Body.SchedulesBody;
import com.kafka1.demo.DTO.DoctorDTO;
import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Entity.Doctor;
import com.kafka1.demo.Entity.DoctorSchedules;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Entity.User;
import com.kafka1.demo.Models.DayOfWeek;
import com.kafka1.demo.Models.TimeInterval;
import com.kafka1.demo.Repositoryes.DoctorSchedulesRepository;
import com.kafka1.demo.Services.DB.DoctorDbService;
import com.kafka1.demo.Services.DB.SessionDbService;
import com.kafka1.demo.Services.DB.UserDbService;
import com.kafka1.demo.Zoom.ZoomApi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
public class DoctorControllerService {
    private final DoctorDbService doctorDbService;
    private final SessionDbService sessionDbService;
    private final UserDbService userDbService;
    private final DoctorSchedulesRepository doctorSchedulesRepository;
    private final ZoomApi zoomApi;
    private static final int MIN_SESSION_DURATION = 5;
    private static final int MAX_SESSION_DURATION = 200;

    public DoctorControllerService(DoctorDbService doctorDbService, SessionDbService sessionDbService,
                                   UserDbService userDbService, DoctorSchedulesRepository doctorSchedulesRepository, ZoomApi zoomApi) {
        this.doctorDbService = doctorDbService;
        this.sessionDbService = sessionDbService;
        this.userDbService = userDbService;
        this.doctorSchedulesRepository = doctorSchedulesRepository;
        this.zoomApi = zoomApi;
    }

    public HttpResponseBody orderConsultation(int doctorId, String email, String timeStartString,
                                              String longSessionInMinutesString, LocalDate dateOrder) {
        HttpResponseBody responseBody = new HttpResponseBody();
        DoctorDTO doctorDTO = doctorDbService.findDoctorDtoById(doctorId);
        DoctorSchedulesDTO freeTime = doctorDbService.getFreeTime(dateOrder, doctorId);
        responseBody.putData("doctor", doctorDTO);
        responseBody.putData("doctorSchedules", freeTime);

        HttpResponseBody errorResponse = validOrderConsultation(freeTime, timeStartString, longSessionInMinutesString, email, doctorDTO);
        if (errorResponse!=null) return errorResponse;

        Doctor doctor = doctorDbService.findDoctorById(doctorId);
        LocalTime timeStartSession = parseTime(timeStartString);
        int longSessionInMinutes = parseDurationSession(longSessionInMinutesString);
        User user = userDbService.findUserByEmail(email);
        String url = zoomApi.createZoomSession();

        saveSession(timeStartSession, longSessionInMinutes, user, doctor, url);
        return createResponse(responseBody, "Успешно", OK);
    }

    private HttpResponseBody validOrderConsultation(DoctorSchedulesDTO freeTime, String timeStartString, String longSessionInMinutesString, String email ,DoctorDTO doctorDTO) {
        HttpResponseBody responseBody = new HttpResponseBody();

        if (isNoAvailableTime(freeTime)) return createResponse(responseBody, "У доктора нет свободного времени", CONFLICT);

        LocalTime timeStartSession = parseTime(timeStartString);
        if (timeStartSession == null) return createResponse(responseBody, "Время не может быть пустым", BAD_REQUEST);

        int longSessionInMinutes = parseDurationSession(longSessionInMinutesString);
        if (longSessionInMinutes == -1) return createResponse(responseBody, "Длительность сеанса должна быть указана", BAD_REQUEST);

        if (isInvalidDurationSession(longSessionInMinutes))
            return createResponse(responseBody, "Длина сеанса должна быть от 5 до 200 минут", BAD_REQUEST);

        if (isUserChoosingUnavailableTime(timeStartSession, longSessionInMinutes, freeTime)) return createResponse(responseBody, "Выбрано недоступное время!", BAD_REQUEST);

        User user = userDbService.findUserByEmail(email);
        if (isInsufficientBalance(user.getBalance(), doctorDTO.getPricePerMinute(), longSessionInMinutes))
            return createResponse(responseBody, "Недостаточно средств", PAYMENT_REQUIRED);

        String url = zoomApi.createZoomSession();
        if (url == null) return createResponse(responseBody, "Ошибка сервера", BAD_GATEWAY);

        return null;
    }

    private boolean isNoAvailableTime(DoctorSchedulesDTO freeTime) {
        return freeTime == null;
    }

    private HttpResponseBody createResponse(HttpResponseBody responseBody, String message, HttpStatus status) {
        responseBody.setMessage(message);
        responseBody.setHttpStatus(status);
        return responseBody;
    }

    private LocalTime parseTime(String timeReceptionString) {
        try {
            return LocalTime.parse(timeReceptionString);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseDurationSession(String longSessionInMinutesString) {
        try {
            return Integer.parseInt(longSessionInMinutesString);
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean isInvalidDurationSession(int longSessionInMinutes) {
        return longSessionInMinutes < MIN_SESSION_DURATION || longSessionInMinutes > MAX_SESSION_DURATION;
    }

    private boolean isUserChoosingUnavailableTime(LocalTime newSessionTime, int minutes, DoctorSchedulesDTO freeTime) {
        for (TimeInterval timeInterval : freeTime.getTimeIntervals()) {
            LocalTime startSchedule = timeInterval.start();
            LocalTime endSchedule = timeInterval.end();
            LocalTime startConsultation = newSessionTime;
            LocalTime endConsultation = newSessionTime.plusMinutes(minutes);
            if (!startConsultation.isBefore(startSchedule) && !endSchedule.isBefore(endConsultation)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInsufficientBalance(double balance, int pricePerMinute, int longSessionInMinutes) {
        return balance < pricePerMinute * longSessionInMinutes;
    }

    private void saveSession(LocalTime timeStartSession, int longSessionInMinutes, User user, Doctor doctor, String url) {
        Session session = new Session(timeStartSession, timeStartSession.plusMinutes(longSessionInMinutes), user, doctor, url, LocalDate.now());
        sessionDbService.save(session);
    }

    public HttpResponseBody editSchedules(SchedulesBody schedulesBody, String prefix, BindingResult bindingResult, DoctorDTO doctorDTO) {
        HttpResponseBody responseBody = new HttpResponseBody();

        HttpResponseBody errorResponse = validEditSchedules(bindingResult, schedulesBody, prefix,  doctorDTO);
        if (errorResponse != null) return errorResponse;

        LocalTime timeStart = LocalTime.parse(schedulesBody.getTimeStartString());
        LocalTime timeEnd = LocalTime.parse(schedulesBody.getTimeEndString());
        int dayCode = Integer.parseInt(schedulesBody.getDayOfWeekString());
        if (prefix.equals("add")) {
            addRecordToSchedules(dayCode-1, timeStart, timeEnd, doctorDTO);
            return createResponse(responseBody, "Successfully added", OK);
        }else {
            deleteRecordFromSchedules(schedulesBody.getId());
            return createResponse(responseBody, "Successfully deleted", OK);
        }
    }

    private HttpResponseBody validEditSchedules(BindingResult bindingResult, SchedulesBody schedulesBody, String prefix, DoctorDTO doctorDTO) {
        HttpResponseBody responseBody = new HttpResponseBody();
        if (bindingResult.hasErrors()){
            return createResponse(responseBody, "Some required fields are empty", BAD_REQUEST);
        }
        if (prefix==null) return createResponse(responseBody, "prefix is null", BAD_REQUEST);

        if (invalidFormatTime(schedulesBody.getTimeStartString(), schedulesBody.getTimeEndString())){
            return createResponse(responseBody, "Incorrect time format", BAD_REQUEST);
        }

        LocalTime timeStart = LocalTime.parse(schedulesBody.getTimeStartString());
        LocalTime timeEnd = LocalTime.parse(schedulesBody.getTimeEndString());
        if (validDurationSession(timeStart, timeEnd)){
            return createResponse(responseBody, "duration session is less 5 minutes", BAD_REQUEST);
        }

        if (invalidDayOfWeek(schedulesBody.getDayOfWeekString())){
            return createResponse(responseBody, "incorrect format day code", BAD_REQUEST);
        }

        int dayCode = Integer.parseInt(schedulesBody.getDayOfWeekString());
        DoctorSchedulesDTO doctorSchedulesDTO = doctorDbService.findSchedulesByDoctorAndDayAndTimeAfter(doctorDTO.getId(),dayCode-1,LocalTime.now());
        if (isInterfereOtherTimeInterval(doctorSchedulesDTO, timeStart, timeEnd)) {
            return createResponse(responseBody, "Interfere Other Time Interval", BAD_REQUEST);
        }
        return null;
    }

    private boolean invalidFormatTime(String timeStartString, String timeEndString) {
        try {
            LocalTime.parse(timeStartString);
            LocalTime.parse(timeEndString);
            return false;
        } catch (Exception err) {
            return true;
        }
    }

    private boolean validDurationSession(LocalTime timeStart, LocalTime timeEnd) {
        return timeEnd.minusMinutes(MIN_SESSION_DURATION).isBefore(timeStart);
    }

    private boolean invalidDayOfWeek(String dayOfWeekString) {
        try {
            int dayCode = Integer.parseInt(dayOfWeekString);
            DayOfWeek.fromDayCode(dayCode-1);
            return false;
        }
        catch (Exception err){
            return true;
        }
    }

    private boolean isInterfereOtherTimeInterval(DoctorSchedulesDTO doctorSchedulesDTO, LocalTime startNewInterval, LocalTime endNewInterval) {
        if (!doctorSchedulesDTO.getTimeIntervals().isEmpty()) {
            for (int i = 0; i < doctorSchedulesDTO.getTimeIntervals().size(); i++) {

                LocalTime startInterval = doctorSchedulesDTO.getTimeIntervals().get(i).start();
                LocalTime endInterval = doctorSchedulesDTO.getTimeIntervals().get(i).end();

                boolean isStartNewIntervalInterfereExitingInterval = startNewInterval.isAfter(startInterval) && startNewInterval.isBefore(endInterval);
                boolean isEndNewIntervalInterfereExitingInterval = endNewInterval.isAfter(startInterval) && endNewInterval.isBefore(endInterval);
                boolean isNewIntervalOverlapExistedInterval = startNewInterval.compareTo(startInterval) <= 0 && endNewInterval.compareTo(endInterval) >= 0;

                if (isStartNewIntervalInterfereExitingInterval || isEndNewIntervalInterfereExitingInterval || isNewIntervalOverlapExistedInterval){
                    return true;
                }
            }
        }
        return false;
    }

    private void addRecordToSchedules(int dayCode, LocalTime timeStart, LocalTime timeEnd, DoctorDTO doctorDTO) {
        DayOfWeek dayOfWeek = DayOfWeek.fromDayCode(dayCode);

        DoctorSchedulesDTO doctorSchedulesDTO = doctorDbService.findSchedulesByDoctorAndDayAndTimeAfter(doctorDTO.getId(), dayCode, LocalTime.now());
        if (doctorSchedulesDTO!=null) {
            List<TimeInterval> timeIntervals = doctorSchedulesDTO.getTimeIntervals();
            for (TimeInterval timeInterval : timeIntervals) {
                if (timeInterval.start().isBefore(timeStart)) {
                    if (timeInterval.end().plusMinutes(2).isAfter(timeStart)) {
                        DoctorSchedules doctorSchedules = doctorSchedulesRepository.findById(timeInterval.id());
                        doctorSchedules.setEndTime(timeEnd);
                        doctorSchedulesRepository.save(doctorSchedules);
                    }
                }
                else {
                    if (timeInterval.start().minusMinutes(2).isBefore(timeEnd)) {
                        DoctorSchedules doctorSchedules = doctorSchedulesRepository.findById(timeInterval.id());
                        doctorSchedules.setStartTime(timeStart);
                        doctorSchedulesRepository.save(doctorSchedules);
                    }
                }
            }
        }
        DoctorSchedules doctorSchedules = new DoctorSchedules(timeStart, timeEnd, dayOfWeek, doctorDTO.getId());
        doctorDbService.addTimeToSchedules(doctorSchedules);
    }



    private void deleteRecordFromSchedules(int idSchedules){
        DoctorSchedules doctorSchedules = doctorSchedulesRepository.findById(idSchedules);
        int dayCode = doctorSchedules.getDayOfWeek().dayCode;
        LocalDate deletingDay = LocalDate.now();
        while (deletingDay.getDayOfWeek() != java.time.DayOfWeek.of(dayCode+1)){
            deletingDay = deletingDay.plusDays(1);
        }
        doctorDbService.deleteSchedulesById(idSchedules);
        Doctor doctor = doctorDbService.findDoctorById(doctorSchedules.getDoctorId());
        sessionDbService.deleteByDateAndDoctor(deletingDay, doctor, new TimeInterval(doctorSchedules.getStartTime(),doctorSchedules.getEndTime()));
    }
}
