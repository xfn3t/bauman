package service;

import model.employee.Employee;
import model.enclosure.Enclosure;

import java.util.List;

public interface EnclosureService {
	void registerEnclosure(Enclosure enclosure);
	void cleanEnclosure(String enclosureId, Employee employee);
	void markEnclosureAsDirty(String enclosureId, String reason);
	List<Enclosure> getDirtyEnclosures();
	List<Enclosure> getAllEnclosures();
	Enclosure findEnclosure(String id);
	int getCleanedTodayCount();
}