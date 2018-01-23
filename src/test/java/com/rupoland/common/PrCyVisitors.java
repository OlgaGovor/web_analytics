package com.rupoland.common;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

public class PrCyVisitors {

    String csvFile = "website.csv";
    String resultFile = "results.csv";

    String csvFilePath;
    String resultFilePath;
    private String baseUrl = "https://a.pr-cy.ru/";

    public PrCyVisitors() {
        File resourcesDirectory = new File("src/test/resources");

        csvFilePath = new File(resourcesDirectory.getAbsolutePath() + "/" + csvFile).getPath();
        resultFilePath = new File(resourcesDirectory.getAbsolutePath() + "/" + resultFile).getPath();

        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium/chromedriver/chromedriver.exe");
        Configuration.browser = "chrome";
    }

    public List<String> getWebsiteNames() throws IOException {

        String line = "";
        String cvsSplitBy = ",";

        BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
        List<String> websites = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] str = line.split(cvsSplitBy);
            websites.add(str[0]);
        }
        return websites;
    }

    public void saveResults(HashMap<String, String> siteStatistics) throws IOException {

        try (PrintWriter writer = new PrintWriter(new File(resultFilePath))) {
            siteStatistics.entrySet().stream().forEach(e -> writer.print(e.getKey() + "," + e.getValue() + "\n"));
        }

    }

    public String checkWebsite(String websiteName) throws InterruptedException {
        submitRequest(websiteName);
        Thread.sleep(3000);

        checkAndClosePopUp();


        SelenideElement el = getElement(By.xpath("//div[@description-id='publicStatisticsDescription'][@test-status='info']"));
        String numberOfUsers = "";
        if (el.exists()) {
            WebElement numberOfUsersPerDay = getElement(By.xpath("//table[contains(@class,'table-content-test')]/tbody/tr[2]/td[2]"));
            Thread.sleep(300);
            numberOfUsers = numberOfUsersPerDay.getText().replaceAll(" ", "");
        } else {
            numberOfUsers = "null";
        }

        return numberOfUsers;
    }

    private void checkAndClosePopUp() {
        try {
            SelenideElement closeBtn = getElement(By.cssSelector("button.close"));
            if (closeBtn.exists()) {
                closeBtn.click();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitRequest(String websiteName) {
        WebElement input = getElement(By.xpath("//form[@id='analysis']//input"));
        input.clear();
        input.sendKeys(websiteName);
        try {
            getElement(By.xpath("//form[@id='analysis']//button[@type='submit']")).click();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void retrieveVisitorsForSites() throws IOException, InterruptedException {
        List<String> websiteName = getWebsiteNames();
        HashMap<String, String> numberOfUsersPerSite = new LinkedHashMap<>();

        login();

        open(baseUrl);

        for (int i = 0; i < websiteName.size(); i++) {
            String numberOfUsers = "";
            numberOfUsers = checkWebsite(websiteName.get(i));

            Boolean flag = false;
            while (!flag) {
                if (!numberOfUsers.equals("")) {
                    flag = true;
                } else {
                    numberOfUsers = checkWebsite(websiteName.get(i));
                }
            }

            numberOfUsersPerSite.put(websiteName.get(i), numberOfUsers);
            System.out.println(websiteName.get(i) + " - " + numberOfUsers + "");
            saveResults(numberOfUsersPerSite);
        }

        close();
    }

    private void login() {
        open("https://id.pr-cy.ru/signup/login");

        getElement(By.id("login_email")).sendKeys("rigaya@ua.fm");
        getElement(By.id("password")).sendKeys("1q2w3e4r5t");
        getElement(By.xpath("//button[@type='submit']")).click();
    }
}