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

    /**
     * 등록 요청된 Dto를 ScheduleEntity로 변환 후 저장
     */
    public void registerSchedule(ScheduleDto dto) {
        List<ScheduleEntity> result = new ArrayList<>();

        for (ScheduleDto.MovieSchedule movieDto : dto.getSchedules()) {
            MovieEntity movie = movieRepository.findById(movieDto.getMovieId())
                    .orElseThrow(() -> new IllegalArgumentException("영화 ID 오류: " + movieDto.getMovieId()));

            for (ScheduleDto.TheaterSchedule theaterDto : movieDto.getTheaters()) {
                TheaterEntity theater = theaterRepository.findById(Long.valueOf(theaterDto.getTheaterId()))
                        .orElseThrow(() -> new IllegalArgumentException("극장 ID 오류: " + theaterDto.getTheaterId()));

                List<String> startList = theaterDto.getStartTimes();
                List<String> endList = theaterDto.getEndTimes();

                for (int i = 0; i < startList.size(); i++) {
                    String startTimeStr = dto.getDate() + "T" + startList.get(i);
                    LocalDateTime start = LocalDateTime.parse(startTimeStr);

                    ScheduleEntity schedule = new ScheduleEntity();
                    schedule.setMovie(movie);
                    schedule.setTheater(theater);
                    schedule.setStartTime(start);
                    schedule.setFormat(theaterDto.getFormat());
                    schedule.setSubDub(theaterDto.getSubDub());
                    schedule.setPrice(10000); // 기본값 또는 정책 적용

                    result.add(schedule);
                }
            }
        }

        scheduleRepository.saveAll(result);
    }

    /**
     * 지정한 날짜의 모든 상영 스케줄 조회 후 Dto로 변환
     */
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

        // 예약 좌석 수 미리 조회
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
                theaterSchedule.setFormat(scheduleList.get(0).getFormat());
                theaterSchedule.setSubDub(scheduleList.get(0).getSubDub());
                theaterSchedule.setTotalSeat(theater.getTotalSeats());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                List<String> startTimes = new ArrayList<>();
                List<String> endTimes = new ArrayList<>();
                int totalReserved = 0;

                for (ScheduleEntity schedule : scheduleList) {
                    startTimes.add(schedule.getStartTime().toLocalTime().format(formatter));
                    endTimes.add(schedule.getEndTime().toLocalTime().format(formatter));

                    Long reservedCount = reservedSeatMap.getOrDefault(schedule.getScheduleId(), 0L);
                    totalReserved += reservedCount.intValue();
                }

                theaterSchedule.setStartTimes(startTimes);
                theaterSchedule.setEndTimes(endTimes);
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
