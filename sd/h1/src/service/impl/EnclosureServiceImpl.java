package service.impl;

import exception.NotFoundException;
import model.employee.Employee;
import model.enclosure.Enclosure;
import repository.EnclosureRepository;
import service.EnclosureService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EnclosureServiceImpl implements EnclosureService {

	private final EnclosureRepository enclosureRepository;

	public EnclosureServiceImpl(EnclosureRepository enclosureRepository) {
		this.enclosureRepository = enclosureRepository;
	}

	@Override
	public void registerEnclosure(Enclosure enclosure) {
		enclosureRepository.save(enclosure);
		System.out.printf("Зарегистрирован вольер: %s (площадь: %.1f м²)%n",
				enclosure.getName(), enclosure.getArea());
	}

	@Override
	public void cleanEnclosure(String enclosureId, Employee employee) {
		Enclosure enclosure = enclosureRepository.findById(enclosureId)
				.orElseThrow(() -> new NotFoundException("Вольер не найден: " + enclosureId));

		enclosure.clean();
		enclosureRepository.update(enclosure);

		System.out.printf("%s очистил(а) вольер: %s%n",
				employee.getName(), enclosure.getName());
	}

	@Override
	public void markEnclosureAsDirty(String enclosureId, String reason) {
		Enclosure enclosure = enclosureRepository.findById(enclosureId)
				.orElseThrow(() -> new NotFoundException("Вольер не найден: " + enclosureId));

		enclosure.markAsDirty(reason);
		enclosureRepository.update(enclosure);

		System.out.printf("Вольер %s помечен как грязный: %s%n",
				enclosure.getName(), reason);
	}

	@Override
	public List<Enclosure> getDirtyEnclosures() {
		List<Enclosure> dirty = new ArrayList<>();
		for (Enclosure enclosure : enclosureRepository.findAll()) {
			if (enclosure.needsCleaning()) {
				dirty.add(enclosure);
			}
		}
		return dirty;
	}

	@Override
	public int getCleanedTodayCount() {
		int count = 0;
		LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

		for (Enclosure enclosure : enclosureRepository.findAll()) {
			if (enclosure.getLastCleaningTime() != null &&
					enclosure.getLastCleaningTime().isAfter(todayStart)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public List<Enclosure> getAllEnclosures() {
		return enclosureRepository.findAll();
	}

	@Override
	public Enclosure findEnclosure(String id) {
		return enclosureRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Вольер не найден: " + id));
	}
}