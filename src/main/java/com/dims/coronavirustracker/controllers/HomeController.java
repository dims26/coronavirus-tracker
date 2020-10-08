package com.dims.coronavirustracker.controllers;

import com.dims.coronavirustracker.models.LocationStat;
import com.dims.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService service;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStat> allStats = service.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(LocationStat::getLatestTotalCases).sum();
        int totalNewCases = allStats.stream().mapToInt(LocationStat::getDiffFromPreviousDay).sum();
        model.addAttribute("totalNewCases", totalNewCases);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("locationStats", allStats);
        return "home";
    }

}
