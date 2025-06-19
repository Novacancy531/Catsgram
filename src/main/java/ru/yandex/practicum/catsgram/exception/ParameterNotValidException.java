package ru.yandex.practicum.catsgram.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParameterNotValidException extends IllegalArgumentException {

    private final String parameter;
    private final String reason;
}
