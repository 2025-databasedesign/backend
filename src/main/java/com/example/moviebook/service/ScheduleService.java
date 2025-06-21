package com.example.moviebook.service;

import com.example.moviebook.dto.ScheduleDto;
import com.example.moviebook.entity.MovieEntity;
import com.example.moviebook.entity.ScheduleEntity;
import com.example.moviebook.entity.TheaterEntity;
import com.example.moviebook.repository.MovieRepository;
import com.example.moviebook.repository.ReservationSeatRepository;
import com.example.moviebook.repository.ScheduleRepository;
import com.example.moviebook.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private ReservationSeatRepository reservationSeatRepository;

    public void registerSchedule(ScheduleDto dto) {
        List<ScheduleEntity> result = new ArrayList<>();

        for (ScheduleDto.MovieSchedule movieDto : dto.getSchedules()) {
            MovieEntity movie = movieRepository.findById(movieDto.getMovieId())
                    .orElseThrow(() -> new IllegalArgumentException("영화 ID 오류: " + movieDto.getMovieId()));

            for (ScheduleDto.TheaterSchedule theaterDto : movieDto.getTheaters()) {
                TheaterEntity theater = theaterRepository.findById(Long.valueOf(theaterDto.getTheaterId()))
                        .orElseThrow(() -> new IllegalArgumentException("극장 ID 오류: " + theaterDto.getTheaterId()));

                List<String> startList = theaterDto.getStartTimes();
                if (startList == null || startList.isEmpty()) continue;

                String firstStartTimeStr = dto.getDate() + "T" + startList.get(0);
                LocalDateTime currentStart = LocalDateTime.parse(firstStartTimeStr);

                int runningTime = movie.getRunningTime();
                int intervalMinutes = runningTime + 30;
                LocalDateTime limitTime = LocalDateTime.parse(dto.getDate() + "T23:00");

                while (currentStart.isBefore(limitTime)) {
                    ScheduleEntity schedule = new ScheduleEntity();
                    schedule.setMovie(movie);
                    String format = (theaterDto.getFormat() == null || theaterDto.getFormat().isBlank())
                            ? theater.getFormat()
                            : theaterDto.getFormat();
                    schedule.setFormat(format);
                    schedule.setTheater(theater);
                    schedule.setStartTime(currentStart);
                    schedule.setSubDub(theaterDto.getSubDub());  // 자막/더빙은 회차마다 다를 수 있음

                    result.add(schedule);
                    currentStart = currentStart.plusMinutes(intervalMinutes);
                }
            }
        }

        scheduleRepository.saveAll(result);
    }

    public ScheduleDto getScheduleByDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        List<ScheduleEntity> schedules = scheduleRepository.findByStartTimeBetween(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay()
        );

        return convertToDtoWithAvailableSeats(schedules, dateStr);
    }

    public List<ScheduleDto.SimpleSchedule> getSchedulesByMovie(Long movieId) {
        List<ScheduleEntity> schedules = scheduleRepository.findByMovie_MovieId(movieId);

        return schedules.stream()
                .map(this::convertToSimpleDto)
                .collect(Collectors.toList());
    }

    public List<ScheduleDto> updateSchedulesFrom(
            Long baseScheduleId,
            Long newMovieId,
            Long newTheaterId,
            LocalDateTime newStartTime,
            String newSubDub
    ) {
        ScheduleEntity baseSchedule = scheduleRepository.findById(baseScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("기준 스케줄을 찾을 수 없습니다."));

        MovieEntity newMovie = movieRepository.findById(newMovieId)
                .orElseThrow(() -> new IllegalArgumentException("영화를 찾을 수 없습니다."));
        TheaterEntity newTheater = theaterRepository.findById(newTheaterId)
                .orElseThrow(() -> new IllegalArgumentException("상영관을 찾을 수 없습니다."));

        LocalDate date = baseSchedule.getStartTime().toLocalDate();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();

        // 상영관의 하루치 일정 모두 가져옴
        List<ScheduleEntity> allSchedules = scheduleRepository.findByTheaterAndDate(
                baseSchedule.getTheater().getTheaterId(), startOfDay, startOfNextDay
        );

        // 기준 시간 이전의 일정은 보존
        List<ScheduleEntity> preservedSchedules = allSchedules.stream()
                .filter(s -> s.getStartTime().isBefore(baseSchedule.getStartTime()))
                .collect(Collectors.toList());

        // 기준 시간 이후의 일정은 제거
        List<ScheduleEntity> toDelete = allSchedules.stream()
                .filter(s -> !s.getStartTime().isBefore(baseSchedule.getStartTime()))
                .collect(Collectors.toList());

        scheduleRepository.deleteAll(toDelete);

        // 새로운 일정 생성 (기준 스케줄 기준으로 이후 시간들)
        List<ScheduleEntity> newSchedules = new ArrayList<>();
        LocalDateTime currentStart = newStartTime;
        int runningTime = newMovie.getRunningTime();
        int intervalMinutes = runningTime + 30;
        LocalDateTime limitTime = date.atTime(23, 0);

        while (currentStart.isBefore(limitTime)) {
            ScheduleEntity s = new ScheduleEntity();
            s.setMovie(newMovie);
            s.setTheater(newTheater);
            s.setStartTime(currentStart);
            s.setSubDub(newSubDub);
            s.setFormat(newTheater.getFormat());

            newSchedules.add(s);
            currentStart = currentStart.plusMinutes(intervalMinutes);
        }

        // 새로운 일정 저장
        scheduleRepository.saveAll(newSchedules);

        // 이전 일정과 새 일정 합침
        List<ScheduleEntity> finalList = new ArrayList<>();
        finalList.addAll(preservedSchedules);
        finalList.addAll(newSchedules);

        return finalList.stream()
                .map(this::convertToDtoSimple)
                .collect(Collectors.toList());
    }

    public void deleteSchedulesByMovie(Long movieId) {
        List<ScheduleEntity> schedules = scheduleRepository.findByMovie_MovieId(movieId);
        if (schedules.isEmpty()) {
            throw new IllegalArgumentException("삭제할 스케줄이 없습니다.");
        }

        for (ScheduleEntity schedule : schedules) {
            long count = reservationSeatRepository.countBySchedule(schedule);
            if (count > 0) {
                throw new IllegalStateException("예약이 존재하는 스케줄은 삭제할 수 없습니다.");
            }
        }

        scheduleRepository.deleteAll(schedules);
    }

    private ScheduleDto convertToDtoSimple(ScheduleEntity schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setDate(schedule.getStartTime().toLocalDate().toString());

        ScheduleDto.MovieSchedule movieSchedule = new ScheduleDto.MovieSchedule();
        movieSchedule.setMovieId(schedule.getMovie().getMovieId());
        movieSchedule.setMovieName(schedule.getMovie().getTitle());
        movieSchedule.setDurationMinutes(schedule.getMovie().getRunningTime());
        movieSchedule.setGrade(schedule.getMovie().getGrade());

        ScheduleDto.TheaterSchedule theaterSchedule = new ScheduleDto.TheaterSchedule();
        theaterSchedule.setTheaterId(String.valueOf(schedule.getTheater().getTheaterId()));
        theaterSchedule.setTheaterName(schedule.getTheater().getTheaterName());
        theaterSchedule.setFormat(schedule.getFormat());
        theaterSchedule.setSubDub(schedule.getSubDub());
        theaterSchedule.setStartTimes(List.of(schedule.getStartTime().toLocalTime().toString()));
        theaterSchedule.setEndTimes(List.of(schedule.getEndTime().toLocalTime().toString()));
        theaterSchedule.setScheduleIds(List.of(schedule.getScheduleId()));
        theaterSchedule.setTotalSeat(schedule.getTheater().getTotalSeats());
        theaterSchedule.setAvailSeat(schedule.getTheater().getTotalSeats()); // 예매 정보 미포함 버전

        movieSchedule.setTheaters(List.of(theaterSchedule));
        dto.setSchedules(List.of(movieSchedule));

        return dto;
    }

    private ScheduleDto convertToDtoWithAvailableSeats(List<ScheduleEntity> scheduleEntities, String date) {
        ScheduleDto dto = new ScheduleDto();
        dto.setDate(date);

        Map<Long, Long> reservedSeatMap = reservationSeatRepository
                .countReservedSeatsGrouped(scheduleEntities)
                .stream()
                .collect(Collectors.toMap(r -> (Long) r[0], r -> (Long) r[1]));

        Map<MovieEntity, List<ScheduleEntity>> byMovie = scheduleEntities.stream()
                .collect(Collectors.groupingBy(ScheduleEntity::getMovie));

        List<ScheduleDto.MovieSchedule> movieSchedules = new ArrayList<>();

        for (Map.Entry<MovieEntity, List<ScheduleEntity>> movieEntry : byMovie.entrySet()) {
            MovieEntity movie = movieEntry.getKey();
            List<ScheduleEntity> movieSchedulesList = movieEntry.getValue();

            ScheduleDto.MovieSchedule movieSchedule = new ScheduleDto.MovieSchedule();
            movieSchedule.setMovieId(movie.getMovieId());
            movieSchedule.setMovieName(movie.getTitle());
            movieSchedule.setDurationMinutes(movie.getRunningTime());
            movieSchedule.setGrade(movie.getGrade());

            Map<TheaterEntity, List<ScheduleEntity>> byTheater = movieSchedulesList.stream()
                    .collect(Collectors.groupingBy(ScheduleEntity::getTheater));

            List<ScheduleDto.TheaterSchedule> theaterSchedules = new ArrayList<>();

            for (Map.Entry<TheaterEntity, List<ScheduleEntity>> theaterEntry : byTheater.entrySet()) {
                TheaterEntity theater = theaterEntry.getKey();
                List<ScheduleEntity> scheduleList = theaterEntry.getValue();

                ScheduleDto.TheaterSchedule theaterSchedule = new ScheduleDto.TheaterSchedule();
                theaterSchedule.setTheaterId(String.valueOf(theater.getTheaterId()));
                theaterSchedule.setTheaterName(theater.getTheaterName());
                theaterSchedule.setFormat(scheduleList.get(0).getFormat());// ← 여기서 포맷 가져옴
                theaterSchedule.setSubDub(scheduleList.get(0).getSubDub());
                theaterSchedule.setTotalSeat(theater.getTotalSeats());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                List<String> startTimes = new ArrayList<>();
                List<String> endTimes = new ArrayList<>();
                List<Long> scheduleIds = new ArrayList<>();

                int totalReserved = 0;

                for (ScheduleEntity schedule : scheduleList) {
                    startTimes.add(schedule.getStartTime().toLocalTime().format(formatter));
                    endTimes.add(schedule.getEndTime().toLocalTime().format(formatter));
                    scheduleIds.add(schedule.getScheduleId());  // ← 추가

                    Long reservedCount = reservedSeatMap.getOrDefault(schedule.getScheduleId(), 0L);
                    totalReserved += reservedCount.intValue();
                }

                theaterSchedule.setStartTimes(startTimes);
                theaterSchedule.setEndTimes(endTimes);
                theaterSchedule.setScheduleIds(scheduleIds);
                theaterSchedule.setAvailSeat(theater.getTotalSeats() - totalReserved);

                theaterSchedules.add(theaterSchedule);
            }

            movieSchedule.setTheaters(theaterSchedules);
            movieSchedules.add(movieSchedule);
        }

        dto.setSchedules(movieSchedules);
        return dto;
    }

    private ScheduleDto.SimpleSchedule convertToSimpleDto(ScheduleEntity entity) {
        ScheduleDto.SimpleSchedule dto = new ScheduleDto.SimpleSchedule();
        dto.setScheduleId(entity.getScheduleId());
        dto.setMovieTitle(entity.getMovie().getTitle());
        dto.setTheaterName(entity.getTheater().getTheaterName());
        dto.setStartTime(entity.getStartTime());
        dto.setFormat(entity.getFormat());
        dto.setSubDub(entity.getSubDub());
        return dto;
    }
}