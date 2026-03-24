package ru.bauman.tigerbank.common.command;

public interface Command<T> {
    T execute();
}