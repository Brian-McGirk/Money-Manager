package MoneyManager.controllers;

import MoneyManager.models.User;
import MoneyManager.models.data.UserDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
@RequestMapping("price-comparison")
public class JsoupController {

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String displayPriceComparison(Model model, HttpSession httpSession) throws IOException {

        Object userInSession = httpSession.getAttribute("user");

        if(userInSession == null){
            return "redirect:/user/login";
        }

        User user = userDao.findByUserName(userInSession.toString());

        model.addAttribute("user", user);

        return "jsoup/index";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String processPriceComparison(Model model, HttpSession httpSession, @RequestParam String searchTerm) throws IOException {

        Object userInSession = httpSession.getAttribute("user");
        User user = userDao.findByUserName(userInSession.toString());

        String[] searchTermSplit = searchTerm.split("\\s+");

        String searchTermJoined = String.join("+", searchTermSplit);

        Document doc = Jsoup.connect("https://slickdeals.net/newsearch.php?src=SearchBarV2&" +
                "q="+ searchTermJoined +"&pp=20&sort=relevance&previousdays=-1&forumid%5B%5D=25&forumid%5B" +
                "%5D=30&forumid%5B%5D=9").get();

        Elements titleElements = doc.select("div.resultRow:contains("+ searchTerm +")"); // a.dealTitle

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> prices = new ArrayList<>();
        ArrayList<String> stores = new ArrayList<>();
        ArrayList<String> links = new ArrayList<>();


        for (Element title : titleElements) {

            // Check to make sure all the required information is there
            if( title.select("div.priceCol span.store").text().length() != 0 && title.select("div.priceCol span.price").text().length() !=0) {


               // Check if deal is expired
               if(title.select("div.expired").text().length() > 0){
                    continue;
                }

                // Add title to the appropriate array list and remove the prices from the title
                if (title.select("a.dealTitle").text().contains("-")) {
                    titles.add(title.select("a.dealTitle").text().substring(0, title.text().indexOf("-")));
                } else {
                    titles.add(title.select("a.dealTitle").text().substring(0, title.text().indexOf("$")));
                }

                // Find link path for slick deals
                String href = title.select("a.dealTitle").attr("href");

                // Go to the link
                Document hrefDoc = Jsoup.connect("https://slickdeals.net" + href).get();

                // Extract the link from the buy now button
                Element link = hrefDoc.select("div#buyNowButton a").first();

                // Add the appropriate link to the deal to the links array list
                links.add(link.attr("href"));

                // Add the prices to the appropriate array list
                prices.add(title.select("div.priceCol span.price").text());

                // Add the stores to the appropriate array list
                stores.add(title.select("div.priceCol span.store").text());
            }
        }


        model.addAttribute("titles", titles);
        model.addAttribute("prices", prices);
        model.addAttribute("stores", stores);
        model.addAttribute("links", links);
        model.addAttribute("user", user);

        return "jsoup/view";
    }
}