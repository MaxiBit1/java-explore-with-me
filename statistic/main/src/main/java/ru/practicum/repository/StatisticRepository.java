package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.StatisticDtoEnd;
import ru.practicum.entity.StatisticEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<StatisticEntity, Long> {
    @Query("select new ru.practicum.dto.StatisticDtoEnd(s.app, s.uri, count(s.ip)) from StatisticEntity as s " +
            "where s.timestamp between ?1 and ?2 group by s.app, s.uri order by count(s.ip) desc")
    List<StatisticDtoEnd> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatisticDtoEnd(s.app, s.uri, count(distinct s.ip)) from StatisticEntity as s " +
            "where s.timestamp between ?1 and ?2 group by s.app, s.uri order by count(s.ip) desc")
    List<StatisticDtoEnd> findAllByTimestampBetweenDistinct(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatisticDtoEnd(s.app, s.uri, count(s.ip)) from StatisticEntity as s " +
            "where s.uri IN ?1 and s.timestamp between ?2 and ?3 group by s.app, s.uri order by count(s.ip) desc")
    List<StatisticDtoEnd> findAllByTimestampBetweenAndUris(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatisticDtoEnd(s.app, s.uri, count(distinct s.ip)) from StatisticEntity as s " +
            "where s.uri IN ?1 and s.timestamp between ?2 and ?3 group by s.app, s.uri order by count(s.ip) desc")
    List<StatisticDtoEnd> findAllByTimestampBetweenDistinctAndUris(List<String> uris, LocalDateTime start, LocalDateTime end);

}
