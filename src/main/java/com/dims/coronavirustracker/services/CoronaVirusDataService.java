package com.dims.coronavirustracker.services;

import com.dims.coronavirustracker.models.LocationStat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStat> allStats = new ArrayList<>();

    public List<LocationStat> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 0,5,11,17,23 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        HttpResponse<String> httpResponse;

        httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(httpResponse.body());

        StringReader in = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        List<LocationStat> newStats = new ArrayList<>();
        for (CSVRecord record : records) {
            LocationStat locationStat = new LocationStat();

            String state = record.get("Province/State");
            String country = record.get("Country/Region");
            String totalCases = record.get(record.size() - 1);
            String previousDayTotalCases = record.get(record.size() - 2);

            locationStat.setState(state);
            locationStat.setCountry(country);
            locationStat.setLatestTotalCases(Integer.parseInt(totalCases));
            locationStat.setDiffFromPreviousDay(Integer.parseInt(totalCases) - Integer.parseInt(previousDayTotalCases));

            System.out.println(locationStat.toString());

            newStats.add(locationStat);
        }
        allStats = newStats;
    }
}
