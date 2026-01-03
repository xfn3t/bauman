package service;

import model.event.Exhibition;

import java.util.List;

public interface ExhibitionService {
	// Основные операции с выставками
	void registerExhibition(Exhibition exhibition);

	// Управление участниками
	void registerAnimalForExhibition(String animalId, String exhibitionId);
	void cancelAnimalParticipation(String animalId, String exhibitionId, String reason);

	// Проверки и отчеты
	void checkExhibitionReadiness(String exhibitionId);

	// Поиск и фильтрация
	List<Exhibition> getUpcomingExhibitions();
	List<Exhibition> getTodaysExhibitions();
	int getUpcomingExhibitionCount();
}