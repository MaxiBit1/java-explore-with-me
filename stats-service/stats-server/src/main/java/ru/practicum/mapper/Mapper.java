package ru.practicum.mapper;

import ru.practicum.dto.StatisticDto;
import ru.practicum.entity.StatisticEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mapper {

    public static StatisticEntity ToEntity(StatisticDto statisticDto) {
        return StatisticEntity.builder()
                .app(statisticDto.getApp())
                .ip(statisticDto.getIp())
                .uri(statisticDto.getUri())
                .timestamp(LocalDateTime.parse(statisticDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static StatisticDto toDto(StatisticEntity statisticEntity) {
        return StatisticDto.builder()
                .app(statisticEntity.getApp())
                .ip(statisticEntity.getIp())
                .uri(statisticEntity.getUri())
                .timestamp(statisticEntity.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
