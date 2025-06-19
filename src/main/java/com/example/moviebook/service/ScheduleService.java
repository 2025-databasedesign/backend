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
                    schedule.setFormat(theaterDto.getFormat());
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
}