package com.lotus.booking.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lotus.booking.Entity.Report;
import com.lotus.booking.Entity.User;
import com.lotus.booking.Repository.ReportRepository;

import java.util.List;

@Service
public class ReportServiceImpl {
    @Autowired
    private ReportRepository reportRepository;

    public void saveReport(User user, Report report) {
        Report report1 = reportRepository.findByUserIdAndDate(user.getId(), report.getDate());
        if(report1 == null) {
            Report newReport = new Report();
            newReport.setDate(report.getDate());
            newReport.setSent(report.isSent());
            newReport.setUser(user);
            reportRepository.save(newReport);
            System.out.println("Report added.");
        } else {
            System.out.println("There was record saved for this date.");
        }
    }

    public void updateReport(User user, Report report) {
        Report newReport = reportRepository.findById(report.getId()).get();
        newReport.setDate(report.getDate());
        newReport.setSent(report.isSent());
        newReport.setUser(user);
        reportRepository.save(newReport);

        System.out.println("Report updated.");
    }

    public Report getReport(User user, String date) {
        Long userId = user.getId();
        Report newReport = reportRepository.findByUserIdAndDate(userId,date);
        System.out.println("Report retrieved.");
        return newReport;
    }

    public List<Report> getReportByMonth(User user, String date){
        Long userId = user.getId();
        return reportRepository.getReportByMonth(userId, date);
    }
}
