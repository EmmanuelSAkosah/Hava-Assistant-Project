package edu.dartmouth.cs.havvapa.models;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExampleNewsResponse {

    String mResponse = "{\"status\":\"ok\",\"totalResults\":20,\"articles\":[{\"source\":{\"id\":null,\"name\":\"Staradvertiser.com\"},\"author\":null,\"title\":\"Lava destroys Puna geothermal plant warehouse; company says worries over lava encroachment overblown\",\"description\":\"UPDATE: 8:55 a.m.\",\"url\":\"http:\\/\\/www.staradvertiser.com\\/2018\\/05\\/21\\/breaking-news\\/kilauea-volcano-updates-may-21\\/\",\"urlToImage\":\"http:\\/\\/www.staradvertiser.com\\/wp-content\\/uploads\\/2018\\/05\\/web1_20180522_brk_stm01.jpg\",\"publishedAt\":\"2018-05-22T19:12:43Z\"},{\"source\":{\"id\":\"cnn\",\"name\":\"CNN\"},\"author\":\"Paul P. Murphy, Eric Levenson and Janet DiGiacomo, CNN\",\"title\":\"Authorities responding to suspect barricaded in apartment in Panama City, Florida\",\"description\":\"A suspect is barricaded Tuesday in an apartment in Panama City, Florida, following an earlier shooting, according to a spokeswoman for Panama City.\",\"url\":\"https:\\/\\/www.cnn.com\\/2018\\/05\\/22\\/us\\/panama-city-active-shooting\\/index.html\",\"urlToImage\":\"https:\\/\\/cdn.cnn.com\\/cnnnext\\/dam\\/assets\\/180522140632-01-panama-city-shooting-0522-super-tease.jpg\",\"publishedAt\":\"2018-05-22T18:54:00Z\"},{\"source\":{\"id\":null,\"name\":\"Bbc.com\"},\"author\":\"https:\\/\\/www.facebook.com\\/bbcnews\",\"title\":\"Zuckerberg's European Parliament testimony criticised\",\"description\":\"The Facebook founder faces questions from European lawmakers over the data scandal and fake news.\",\"url\":\"http:\\/\\/www.bbc.com\\/news\\/technology-44210800\",\"urlToImage\":\"https:\\/\\/ichef.bbci.co.uk\\/news\\/1024\\/branded_news\\/8E74\\/production\\/_101686463_4dba97cd-13e6-4848-8f72-4ebb97d3c9b3.jpg\",\"publishedAt\":\"2018-05-22T18:45:00Z\"},{\"source\":{\"id\":\"abc-news\",\"name\":\"ABC News\"},\"author\":\"ABC News\",\"title\":\"The Latest: Teen held without bail in officer's slaying\",\"description\":\"The Latest on the slaying of a police officer in Baltimore County, Maryland (all times local): 2:15 p.m. A 16-year-old charged with first-degree murder in the death of a Maryland police officer has been ordered held without bail by a judge who called him a \\\"o…\",\"url\":\"https:\\/\\/abcnews.go.com\\/US\\/wireStory\\/latest-schools-open-time-officer-death-55345699\",\"urlToImage\":\"https:\\/\\/s.abcnews.com\\/images\\/US\\/WireAP_ef17d9b851194eae9f88f6da8db3dff7_16x9_992.jpg\",\"publishedAt\":\"2018-05-22T18:44:00Z\"},{\"source\":{\"id\":null,\"name\":\"Yahoo.com\"},\"author\":null,\"title\":\"Palestinians ask ICC to investigate alleged crimes by Israel\",\"description\":\"THE HAGUE, Netherlands (AP) — Accusing Israel of systematic crimes, including apartheid in the occupied territories, Palestinians on Tuesday urged the International Criminal Court to open an investigation that could ultimately lead to charges against Israeli …\",\"url\":\"https:\\/\\/www.yahoo.com\\/news\\/palestinian-foreign-minister-asks-icc-investigate-israel-081656794.html\",\"urlToImage\":\"https:\\/\\/s.yimg.com\\/uu\\/api\\/res\\/1.2\\/bjJCyb0c0PO5oAVDLeEdFA--~B\\/aD0yODY1O3c9NDI1NztzbT0xO2FwcGlkPXl0YWNoeW9u\\/http:\\/\\/media.zenfs.com\\/en_us\\/News\\/ap_webfeeds\\/51f7d10d96dc4dabacb7ff4a7be47964.jpg\",\"publishedAt\":\"2018-05-22T18:40:45Z\"}]}";

    public ExampleNewsResponse(){

    }

    public JSONObject getNews(){
        try {
            JSONObject response = new JSONObject(mResponse);
            return response;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
