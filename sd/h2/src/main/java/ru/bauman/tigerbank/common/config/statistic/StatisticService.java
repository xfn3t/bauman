package ru.bauman.tigerbank.common.config.statistic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatisticService implements StatisticServiceInterface {
	@Override
	public void printStatistics() {
		log.info("Statistics collected. See logs for execution times.");
		// TODO: soon...
	}
}