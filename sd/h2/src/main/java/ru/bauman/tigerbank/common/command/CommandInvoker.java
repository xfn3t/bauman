package ru.bauman.tigerbank.common.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandInvoker {

    public <T> T invoke(Command<T> command) {
        long start = System.currentTimeMillis();
        T result = command.execute();
        log.info("Command {} completed in {} ms",
                command.getClass().getSimpleName(),
                System.currentTimeMillis() - start);
        return result;
    }
}