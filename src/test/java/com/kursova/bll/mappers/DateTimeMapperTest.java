package com.kursova.bll.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for DateTimeMapper
 * Tests date time conversion methods without Spring context
 */
@DisplayName("Date Time Mapper Tests")
class DateTimeMapperTest {

    private DateTimeMapper dateTimeMapper;
    private DateTimeFormatter formatter;

    @BeforeEach
    void setUp() {
        // Since DateTimeMapper is an interface with default methods,
        // we can create a simple implementation for testing
        dateTimeMapper = new DateTimeMapper() {};
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    @Test
    @DisplayName("Should map LocalDateTime to string successfully")
    void shouldMapLocalDateTimeToString() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2023, 10, 15, 14, 30, 45);
        String expected = "2023-10-15 14:30:45";

        // When
        String result = dateTimeMapper.mapLocalDateTime(dateTime);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should return null when mapping null LocalDateTime")
    void shouldReturnNullWhenMappingNullLocalDateTime() {
        // When
        String result = dateTimeMapper.mapLocalDateTime(null);

        // Then
        assertThat(result).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("Should return null when mapping null, empty, or blank string")
    void shouldReturnNullWhenMappingNullOrEmptyString(String input) {
        // When
        LocalDateTime result = dateTimeMapper.mapStringToLocalDateTime(input);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map string to LocalDateTime successfully")
    void shouldMapStringToLocalDateTime() {
        // Given
        String dateTimeString = "2023-10-15 14:30:45";
        LocalDateTime expected = LocalDateTime.of(2023, 10, 15, 14, 30, 45);

        // When
        LocalDateTime result = dateTimeMapper.mapStringToLocalDateTime(dateTimeString);

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Should throw exception when parsing invalid date string")
    void shouldThrowExceptionWhenParsingInvalidDateString() {
        // Given
        String invalidDateString = "invalid-date-string";

        // When & Then
        assertThatThrownBy(() -> dateTimeMapper.mapStringToLocalDateTime(invalidDateString))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Should handle different date formats correctly")
    void shouldHandleDifferentValidDateFormats() {
        // Test various valid date strings
        String[] validDates = {
            "2023-01-01 00:00:00",
            "2023-12-31 23:59:59",
            "2023-06-15 12:30:45",
            "2023-02-28 14:15:16"
        };

        for (String dateStr : validDates) {
            // When
            LocalDateTime result = dateTimeMapper.mapStringToLocalDateTime(dateStr);

            // Then
            assertThat(result).isNotNull();
            String formattedBack = result.format(formatter);
            assertThat(formattedBack).isEqualTo(dateStr);
        }
    }

    @Test
    @DisplayName("Should handle round trip conversion correctly")
    void shouldHandleRoundTripConversion() {
        // Given
        LocalDateTime original = LocalDateTime.of(2023, 10, 15, 14, 30, 45);

        // When
        String stringResult = dateTimeMapper.mapLocalDateTime(original);
        LocalDateTime dateTimeResult = dateTimeMapper.mapStringToLocalDateTime(stringResult);

        // Then
        assertThat(dateTimeResult).isEqualTo(original);
    }
}
