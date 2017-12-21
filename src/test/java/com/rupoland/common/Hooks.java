package com.rupoland.common;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.rupoland.BaseTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.*;
import java.util.*;

import static com.codeborne.selenide.Selenide.*;

public class Hooks extends BaseTest {

    String csvFile = "c:/olga.govor/website.csv";

    public List<String> getWebsiteNames() throws IOException {

        String line = "";
        String cvsSplitBy = ",";

        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        List<String> websites = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] str = line.split(cvsSplitBy);
            websites.add(str[0]);
        }
        return websites;
    }

    public void putUsers(HashMap webs) throws IOException {

        FileWriter fw = new FileWriter(csvFile);
        BufferedWriter bw = new BufferedWriter(fw);
        Iterator it = webs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String str = pair.getKey() + "," + pair.getValue() + "\n";
            it.remove();
            bw.write(str);
        }
        bw.close();
        fw.close();
    }

    public String checkWebsite(String websiteName) throws InterruptedException {
        WebElement input = getElement(By.xpath("//form[@id='analysis']//input"));
        input.clear();
        input.sendKeys(websiteName);
        getElement(By.xpath("//form[@id='analysis']//button[@type='submit']")).click();
        Thread.sleep(3000);
        SelenideElement closeBtn = getElement(By.cssSelector("button.close"));
        if (closeBtn.exists()) {
            closeBtn.click();
        }
        String str = getElement(By.cssSelector("h1 > a")).getText();
        //if (websiteName.get(i).contains(str)) {
        SelenideElement el = getElement(By.xpath("//div[@description-id='publicStatisticsDescription'][@test-status='info']"));
        String numberOfUsers = "";
        if (el.exists()) {
            WebElement numberOfUsersPerDay = getElement(By.xpath("//table[contains(@class,'table-content-test')]/tbody/tr[2]/td[2]"));
            Thread.sleep(300);
            numberOfUsers = numberOfUsersPerDay.getText();
        } else {
            numberOfUsers = "data not exist";
        }
        //}
        return numberOfUsers;
    }

    @Test
    public void testFirst() throws IOException, InterruptedException {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/selenium/chromedriver/chromedriver.exe");
        Configuration.browser = "chrome";
        open("https://id.pr-cy.ru/signup/login");

        getElement(By.id("login_email")).sendKeys("rigaya@ua.fm");
        getElement(By.id("password")).sendKeys("1q2w3e4r5t");
        getElement(By.xpath("//button[@type='submit']")).click();

        open("https://a.pr-cy.ru/");
        List<String> websiteName = getWebsiteNames();
        HashMap<String, String> numberOfUsersPerSite = new HashMap<>();

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

        }
        putUsers(numberOfUsersPerSite);
        close();
    }
}