package ru.bauman.telemetry.service;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SatelliteRegistry {
    // Start with default satellites
    private final ConcurrentHashMap<String, Boolean> satellites = new ConcurrentHashMap<>();

    public SatelliteRegistry() {
        satellites.put("Связь-1", true);
        satellites.put("Связь-2", true);
        satellites.put("ДЗЗ-1", true);
        satellites.put("ДЗЗ-2", true);
        satellites.put("ДЗЗ-3", true);
    }

    public void add(String name) { satellites.put(name, true); }
    public void remove(String name) { satellites.remove(name); }
    public List<String> getAll() { return satellites.keySet().stream().sorted().toList(); }
    public boolean contains(String name) { return satellites.containsKey(name); }
}
