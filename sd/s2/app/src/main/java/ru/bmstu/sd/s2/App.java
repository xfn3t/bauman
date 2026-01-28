package ru.bmstu.sd.s2;

import ru.bmstu.sd.s2.satellite.*;
import ru.bmstu.sd.s2.satellite.constellation.SatelliteConstellation;
import ru.bmstu.sd.s2.satellite.impl.CommunicationSatellite;
import ru.bmstu.sd.s2.satellite.impl.ImagingSatellite;

public class App {
    public static void main(String[] args) {
        System.out.println("ЗАПУСК СИСТЕМЫ УПРАВЛЕНИЯ СПУТНИКОВОЙ ГРУППИРОВКОЙ");
        System.out.println("=".repeat(60));

        System.out.println("\nСОЗДАНИЕ СПЕЦИАЛИЗИРОВАННЫХ СПУТНИКОВ:");
        System.out.println("-".repeat(45));

        CommunicationSatellite comm1 = new CommunicationSatellite("Связь-1", 0.85, 500.0);
        CommunicationSatellite comm2 = new CommunicationSatellite("Связь-2", 0.75, 1000.0);
        ImagingSatellite img1 = new ImagingSatellite("ДЗЗ-1", 0.92, 2.5);
        ImagingSatellite img2 = new ImagingSatellite("ДЗЗ-2", 0.45, 1.0);
        ImagingSatellite img3 = new ImagingSatellite("ДЗЗ-3", 0.15, 0.5);

        System.out.println("Создан спутник: " + comm1.getName() + " (заряд: " + (int)(comm1.getBatteryLevel() * 100) + "%)");
        System.out.println("Создан спутник: " + comm2.getName() + " (заряд: " + (int)(comm2.getBatteryLevel() * 100) + "%)");
        System.out.println("Создан спутник: " + img1.getName() + " (заряд: " + (int)(img1.getBatteryLevel() * 100) + "%)");
        System.out.println("Создан спутник: " + img2.getName() + " (заряд: " + (int)(img2.getBatteryLevel() * 100) + "%)");
        System.out.println("Создан спутник: " + img3.getName() + " (заряд: " + (int)(img3.getBatteryLevel() * 100) + "%)");

        System.out.println("-".repeat(45));
        System.out.println("Создана спутниковая группировка: RU Basic");
        System.out.println("-".repeat(45));

        SatelliteConstellation constellation = new SatelliteConstellation("RU Basic");

        System.out.println("\nФОРМИРОВАНИЕ ГРУППИРОВКИ:");
        System.out.println("-".repeat(35));

        constellation.addSatellite(comm1);
        constellation.addSatellite(comm2);
        constellation.addSatellite(img1);
        constellation.addSatellite(img2);
        constellation.addSatellite(img3);

        System.out.println("-".repeat(35));
        System.out.println(constellation.getSatellites());
        System.out.println("-".repeat(35));

        constellation.activateAllSatellites();
        constellation.executeAllMissions();

        System.out.println(constellation.getSatellites());

        System.out.println("\nФИНАЛЬНОЕ СОСТОЯНИЕ СПУТНИКОВ:");
        System.out.println("-".repeat(35));
        for (Satellite satellite : constellation.getSatellites()) {
            System.out.println(satellite);
        }
    }
}