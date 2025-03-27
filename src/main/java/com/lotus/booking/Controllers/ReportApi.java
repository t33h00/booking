package com.lotus.booking.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lotus.booking.Entity.Report;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Service.ReportServiceImpl;

import java.util.List;

@RestController
@CrossOrigin(maxAge = 3600)
public class ReportApi {
    @Autowired
    private ReportServiceImpl reportService;
    
    @PostMapping("/user/report")
    public void saveReport(@AuthenticationPrincipal User user, @RequestBody Report report) {
        reportService.saveReport(user, report);
    }

    @PutMapping("/user/report")
    public void updateReport(@AuthenticationPrincipal User user ,@RequestBody Report report) {
        reportService.updateReport(user, report);
    }

    @GetMapping("/user/report")
    public Report getReport(@AuthenticationPrincipal User user,String date) {
        return reportService.getReport(user, date);
    }

    @GetMapping("/user/reportmonth")
    public List<Report> getReportByMonth(@AuthenticationPrincipal User user, String date){
        return reportService.getReportByMonth(user,date);
    }
}
