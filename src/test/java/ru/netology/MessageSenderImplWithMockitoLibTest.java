package ru.netology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSenderImpl;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class MessageSenderImplWithMockitoLibTest {

    private Location location;
    private String ip;

    @Mock
    private GeoService geoService;
    @Mock
    private LocalizationServiceImpl localizationService;

    private MessageSenderImpl messageSender;

    @BeforeEach
    void setUp() {

        localizationService = new LocalizationServiceImpl();
        messageSender = new MessageSenderImpl(geoService, localizationService);
    }

    @Test
    void byIp_RUSSIA() {
        geoService = new GeoServiceImpl();
        ip = "172.0.32.11";
        location = new Location("Moscow", Country.RUSSIA, "Lenina", 15);
        Location actual = geoService.byIp(ip);
        Assertions.assertEquals(location.getCountry(), actual.getCountry());
    }

    @Test
    void byIp_USA() {
        geoService = new GeoServiceImpl();
        ip = "96.44.183.149";
        location = new Location("New York", Country.USA, " 10th Avenue", 32);
        Location actual = geoService.byIp(ip);
        Assertions.assertEquals(location.getCountry(), actual.getCountry());
    }

    @Test
    void byIp_LOCAL() {
        geoService = new GeoServiceImpl();
        ip = "127.0.0.1";
        location = new Location(null, null, null, 0);
        Location actual = geoService.byIp(ip);
        Assertions.assertEquals(location.getCountry(), actual.getCountry());
    }

    @Test
    void byIp_RUSSIA1() {
        geoService = new GeoServiceImpl();
        ip = "172.0.32.11";
        if (ip.startsWith("172.")) {
            ip = "172.0.00.11";
            location = new Location("Moscow", Country.RUSSIA, null, 0);
            Location actual = geoService.byIp(ip);
            Assertions.assertEquals(location.getCountry(), actual.getCountry());
        }
    }

    @Test
    void byIp_USA1() {
        geoService = new GeoServiceImpl();
        ip = "96.44.183.149";
        if (ip.startsWith("96.")) {
            ip = "96.00.000.149";
            location = new Location("New York", Country.USA, null, 0);
            Location actual = geoService.byIp(ip);
            Assertions.assertEquals(location.getCountry(), actual.getCountry());
        }
    }

    @Test
    void send_RUSSIA() {
        ip = "172.0.32.11";
        location = new Location("Moscow", Country.RUSSIA, "Lenina", 15);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);
        Mockito.when(geoService.byIp(ip))
                .thenReturn(location);

        String expected = "Добро пожаловать";
        Assertions.assertEquals(expected, messageSender.send(headers));
    }

    @Test
    void send_USA() {
        ip = "96.44.183.149";
        location = new Location("New York", Country.USA, " 10th Avenue", 32);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, ip);
        Mockito.when(geoService.byIp(ip))
                .thenReturn(location);
        String expected = localizationService.locale(location.getCountry());
        Assertions.assertEquals(expected, messageSender.send(headers));
    }

}