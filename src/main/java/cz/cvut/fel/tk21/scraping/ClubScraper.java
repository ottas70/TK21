package cz.cvut.fel.tk21.scraping;

import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.service.ClubService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClubScraper {

    @Autowired
    private ClubService clubService;

    private static final String url = "http://cztenis.cz/adresar-klubu";

    public void findAllClubs() throws IOException {
        List<Club> clubs = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();

        for (int i = 0; i <= 7; i++) {
            doc = docWithRegion(doc, i);

            Element clubTableBody = doc.select("table tbody").first();
            assertNonNullElement(clubTableBody, "Club Table");
            Elements rows = clubTableBody.select("tr");

            for (Element row : rows){
                Elements cells = row.select("td");
                String webId = cells.get(0).select("a").html();
                Club club = loadClubFromId(Integer.parseInt(webId));
                if(club != null){
                    clubs.add(club);
                }
            }
        }

        clubService.persist(clubs);
    }

    private Document docWithRegion(Document doc, int value) throws IOException {
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

        Elements streetCells = rows.get(6).select("td");
        String street = streetCells.get(1).html();
        Elements cityCells = rows.get(7).select("td");
        String city = cityCells.get(1).html();
        Address address = getAddress(street, city);

        Elements emailCells = rows.get(11).select("td");
        String email = emailCells.get(1).html();
        if(email.equals("")) email = null;

        Club club = new Club();
        club.setName(name);
        club.setWebId(id);
        club.setAddress(address);
        club.setEmail(email);

        return club;
    }

    private Address getAddress(String street, String city_zip){
        if(street.equals("") && city_zip.equals("")) return null;
        Address address = new Address();
        address.setStreet(street);
        if(!city_zip.equals("")){
            String[] citySplit = city_zip.trim().split(" ");
            if(citySplit.length <= 1){
                address.setCity(citySplit[0]);
            }else{
                String zip = citySplit[0];
                String city = "";
                int cityNameIdx = 0;
                try{
                    int zip2 = Integer.parseInt(citySplit[1]);
                    zip += citySplit[1];
                    cityNameIdx = 2;
                } catch (NumberFormatException ex){
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

    private void assertNonNullElement(Element element, String name){
        if(element == null){
            throw new WebScrapingException("Unable to find element " + name);
        }
    }

}
