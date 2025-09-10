package com.kursova.bll.mappers;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Common mapper for date time conversions
 */
@Mapper(componentModel = "spring")
public interface DateTimeMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    default String mapLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

    default LocalDateTime mapStringToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }
}
