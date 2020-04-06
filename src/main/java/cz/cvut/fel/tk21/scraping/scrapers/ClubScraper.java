package cz.cvut.fel.tk21.scraping.scrapers;

import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.teams.Region;
import cz.cvut.fel.tk21.scraping.service.ClubScrapingService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class ClubScraper {

    private static final Logger logger = LoggerFactory.getLogger(ClubScraper.class);
    private final Region[] clubRegions = {Region.PRAHA, Region.STREDOCESKY, Region.JIHOCESKY,
            Region.ZAPADOCESKY, Region.SEVEROCESKY, Region.VYCHODOCESKY,
            Region.JIHOMORAVSKY, Region.SEVEROMORAVSKY};
    private static final String url = "http://cztenis.cz/adresar-klubu";

    private final ClubScrapingService clubScrapingService;

    @Autowired
    public ClubScraper(ClubScrapingService clubScrapingService) {
        this.clubScrapingService = clubScrapingService;
    }

    public void updateClubs(List<Club> toBeFound) throws IOException {
        logger.trace("Club scraping started");

        Document doc = Jsoup.connect(url).get();

        for (int i = 0; i <= 7; i++) {
            doc = docWithRegion(doc, i);

            Element clubTableBody = doc.select("table tbody").first();
            assertNonNullElement(clubTableBody, "Club Table");
            Elements rows = clubTableBody.select("tr");

            for (Element row : rows) {
                Elements cells = row.select("td");
                String webIdString = cells.get(0).select("a").html();
                int webId = Integer.parseInt(webIdString);

                Club found = loadClubFromId(webId);
                Club stored = toBeFound.stream().filter(c -> c.getWebId() == webId).findFirst().orElse(null);

                clubScrapingService.handleUpdate(found, stored);
                if(stored != null) toBeFound.remove(stored);
            }
        }

        clubScrapingService.handleNonFoundedClubs(toBeFound);

        logger.trace("Club scraping finished");
    }

    private Document docWithRegion(Document doc, int value) throws IOException {
        logger.trace("Club scraping at region " + clubRegions[value].toString());
        Element potentialForm = doc.select("form.well").first();
        assertNonNullElement(potentialForm, "Region selector");
        FormElement regionForm = (FormElement) potentialForm;

        Element selectRegion = regionForm.select("select").first();
        assertNonNullElement(selectRegion, "Form select");

        Elements options = selectRegion.select("option");
        options.forEach(o -> o.attr("selected", false));

        Element option = options.get(value);
        option.attr("selected", true);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {

        }

        return regionForm.submit().post();
    }

    private Club loadClubFromId(int id) throws IOException {
        Document doc = Jsoup.connect(url + "/" + id).get();

        Element informationTable = doc.select("table tbody").first();
        Elements rows = informationTable.select("tr");

        Elements nameCells = rows.get(0).select("td");
        String name = nameCells.get(1).select("strong").html();
        if (name == null || name.equals("")) return null;
        if (name.length() > 6 && name.substring(name.length() - 6).equals("zrušen")) return null;

        Elements streetCells = rows.get(6).select("td");
        String street = streetCells.get(1).html();
        Elements cityCells = rows.get(7).select("td");
        String city = cityCells.get(1).html();
        Address address = getAddress(street, city);
        if (address == null) return null;

        Elements emailCells = rows.get(11).select("td");
        String emailString = emailCells.get(1).html();
        Collection<String> emails = this.extractEmails(emailString);
        if (emails.isEmpty()) {
            Element personTable = doc.select("table tbody").last();
            Elements personRows = personTable.select("tr");
            Elements personEmailCells = personRows.get(4).select("td");
            String personEmailString = personEmailCells.get(1).html();
            emails = this.extractEmails(personEmailString);
        }
        if (emails.isEmpty()) return null;

        Club club = new Club();
        club.setName(name);
        club.setWebId(id);
        club.setAddress(address);
        club.setEmails(emails);

        return club;
    }

    private Address getAddress(String street, String city_zip) {
        if (street.equals("") && city_zip.equals("")) return null;
        if (street.equals("0") && city_zip.equals("")) return null;
        if (street.equals("") && city_zip.equals("0")) return null;
        if (street.equals("0") && city_zip.equals("0")) return null;
        Address address = new Address();
        address.setStreet(street);
        if (!city_zip.equals("")) {
            String[] citySplit = city_zip.trim().split(" ");
            if (citySplit.length <= 1) {
                address.setCity(citySplit[0]);
            } else {
                String zip = citySplit[0];
                String city = "";
                int cityNameIdx = 0;
                try {
                    int zip2 = Integer.parseInt(citySplit[1]);
                    zip += citySplit[1];
                    cityNameIdx = 2;
                } catch (NumberFormatException ex) {
                    cityNameIdx = 1;
                }
                for (int i = cityNameIdx; i < citySplit.length; i++) {
                    city += citySplit[i] + " ";
                }
                address.setCity(city);
                address.setZip(zip);
            }
        }

        return address;
    }

    private Collection<String> extractEmails(String email) {
        Collection<String> emails = new ArrayList<>();
        if (email.equals("")) return emails;
        String[] split = email.split(",");
        if (split.length == 1 && email.contains(";")) {
            split = email.split(";");
        }
        for (String s : split) {
            String trimmed = s.trim();
            if (StringUtils.isValidEmail(trimmed)) {
                emails.add(trimmed);
            }
        }
        return emails;
    }

    private void assertNonNullElement(Element element, String name) {
        if (element == null) {
            throw new WebScrapingException("Unable to find element " + name);
        }
    }

}
