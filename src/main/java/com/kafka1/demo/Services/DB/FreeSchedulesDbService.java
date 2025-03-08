package com.kafka1.demo.Services.DB;

import com.kafka1.demo.DTO.DoctorSchedulesDTO;
import com.kafka1.demo.Entity.Session;
import com.kafka1.demo.Models.TimeInterval;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FreeSchedulesDbService {
    private final SessionDbService sessionDbService;
    private int indexSession;
    private int indexSchedule;
    private List<TimeInterval> freeTimeIntervals;
    private boolean hasNoSessionBeforeCurrentTimeInterval;
    private List<Session> sessions = new ArrayList<>();
    private DoctorSchedulesDTO doctorSchedulesDTO;
    private final int MIN_MINUTE_BETWEEN_SESSION = 5;


    public FreeSchedulesDbService(SessionDbService sessionDbService) {
        this.sessionDbService = sessionDbService;
    }

    public DoctorSchedulesDTO getFreeTime(LocalDate date, int doctorId,DoctorSchedulesDTO doctorSchedules,LocalTime startTime){
        init(date,doctorId,doctorSchedules,startTime);

        if (doctorSchedulesDTO==null)return null;
        setActualDoctorSchedules(startTime);
        setFreeDoctorSchedules();
        return doctorSchedulesDTO;
    }

    private void init(LocalDate date, int doctorId, DoctorSchedulesDTO doctorSchedules, LocalTime startTime) {
        sessions = sessionDbService.findByDoctorAndDateAndTimeEndAfterOrderByTimeStart(doctorId, date, startTime);
        doctorSchedulesDTO = doctorSchedules;
        indexSession = initIndexSession(startTime);
        indexSchedule = 0;
        freeTimeIntervals = new ArrayList<>();
        hasNoSessionBeforeCurrentTimeInterval = true;
    }

    private int initIndexSession(LocalTime startTime) {
        return isFirstSessionExpired(startTime)?1:0;
    }

    private boolean isFirstSessionExpired(LocalTime startTime) {
        return sessions.isEmpty() || !sessions.get(0).getTimeEnd().isAfter(startTime);
    }

    private void setActualDoctorSchedules(LocalTime startTime) {
        List<TimeInterval> timeIntervals = doctorSchedulesDTO.getTimeIntervals();
        TimeInterval firstTimeInterval = timeIntervals.get(0);
        
        if (isIntervalStartBeforeStartTime(firstTimeInterval, startTime)) {
            LocalTime adjustedStartTime = calculateAdjustedStartTime(startTime, firstTimeInterval);
            TimeInterval adjustedInterval = createAdjustedInterval(adjustedStartTime, firstTimeInterval);
            updateTimeIntervals(timeIntervals, adjustedInterval);
        }
    }

    private LocalTime calculateAdjustedStartTime(LocalTime startTime, TimeInterval firstTimeInterval) {
        LocalTime startInterval = startTime;
        if(sessions.isEmpty())return startInterval;

        Session session = sessions.get(0);
        if (isSessionOverlappingWithInterval(session,firstTimeInterval,startTime)) {
            startInterval = session.getTimeEnd();
        }
        return startInterval;
    }

    private boolean isSessionOverlappingWithInterval(Session session, TimeInterval interval, LocalTime startTime) {
        return session.getTimeStart().isBefore(interval.end()) && session.getTimeEnd().isAfter(startTime);
    }

    private TimeInterval createAdjustedInterval(LocalTime startTime, TimeInterval originalInterval) {
        return new TimeInterval(startTime, originalInterval.end(), originalInterval.id());
    }

    private void updateTimeIntervals(List<TimeInterval> timeIntervals, TimeInterval adjustedInterval) {
        timeIntervals.set(0, adjustedInterval);
        doctorSchedulesDTO.setTimeIntervals(timeIntervals);
    }

    private boolean isIntervalStartBeforeStartTime(TimeInterval interval, LocalTime startTime) {
        return interval.start().isBefore(startTime);
    }

    private void setFreeDoctorSchedules() {
        List<TimeInterval> timeIntervals = doctorSchedulesDTO.getTimeIntervals();
        while (indexSession < sessions.size()){
            Session session = sessions.get(indexSession);
            TimeInterval timeInterval = timeIntervals.get(indexSchedule);

            if (isSessionOutsideInterval(session, timeInterval)) {
                addFreeTimeInterval(timeInterval);
                incIndexSchedule();
                continue;
            }

            if (hasNoSessionBeforeCurrentTimeInterval && isEnoughTimeBetween(timeInterval.start(),session.getTimeStart())){
                addFreeTimeInterval(new TimeInterval(timeInterval.start(),session.getTimeStart()));
            }
            hasNoSessionBeforeCurrentTimeInterval = true;

            if (!isEnoughTimeBetween(session.getTimeEnd(),timeInterval.end())){
                incrementIndices();
                continue;
            }

            if (areSessionsInSameInterval(timeInterval)) {
                Session nextSession = sessions.get(indexSession+1);
                if (isEnoughTimeBetween(session.getTimeEnd(),nextSession.getTimeStart())){
                    freeTimeIntervals.add(new TimeInterval(session.getTimeEnd(), nextSession.getTimeStart()));
                }
                incIndexSession();
                hasNoSessionBeforeCurrentTimeInterval = false;
                continue;
            }

            addRemainingFreeIntervalOnThisTimeInterval(session,timeInterval);
            incrementIndices();
        }

        addRemainingFreeTimeIntervals(timeIntervals,timeIntervals.size());

        doctorSchedulesDTO.setTimeIntervals(freeTimeIntervals);
    }

    private void addFreeTimeInterval(TimeInterval freeInterval){
        freeTimeIntervals.add(freeInterval);
    }

    private boolean isSessionOutsideInterval(Session session, TimeInterval timeInterval) {
        LocalTime sessionStart = session.getTimeStart();
        LocalTime intervalStart = timeInterval.start();
        LocalTime intervalEnd = timeInterval.end();

        return sessionStart.isBefore(intervalStart) || sessionStart.isAfter(intervalEnd);
    }

    private boolean isEnoughTimeBetween(LocalTime firstTime, LocalTime secondTime) {
        LocalTime earliestNextSessionTime = firstTime.plusMinutes(MIN_MINUTE_BETWEEN_SESSION);
        return !earliestNextSessionTime.isAfter(secondTime);
    }

    private void incrementIndices() {
        incIndexSession();
        incIndexSchedule();
    }

    private void incIndexSession() {
        indexSession++;
    }

    private void incIndexSchedule() {
        indexSchedule++;
    }

    public boolean areSessionsInSameInterval(TimeInterval timeInterval){
        if (sessions.size()<=indexSession+1) return false;
        Session nextSession = sessions.get(indexSession+1);
        return !nextSession.getTimeEnd().isAfter(timeInterval.end());
    }

    private void addRemainingFreeIntervalOnThisTimeInterval(Session session, TimeInterval timeInterval) {
        freeTimeIntervals.add(new TimeInterval(session.getTimeEnd(),timeInterval.end()));
    }

    private void addRemainingFreeTimeIntervals(List<TimeInterval> timeIntervals, int timeIntervalsSize) {
        if (areFreeTimeIntervalsRemaining(timeIntervalsSize)){
            freeTimeIntervals.addAll(timeIntervals.subList(indexSchedule, timeIntervalsSize));
        }
    }

    private boolean areFreeTimeIntervalsRemaining(int sizeTimeIntervals) {
        return sizeTimeIntervals-1>=indexSchedule;
    }
}
