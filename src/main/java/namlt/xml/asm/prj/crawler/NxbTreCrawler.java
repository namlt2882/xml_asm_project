package namlt.xml.asm.prj.crawler;

import namlt.xml.asm.prj.parser.BaseParser;
import namlt.xml.asm.prj.parser.BoundReachedException;
import namlt.xml.asm.prj.parser.ParserHelper;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import namlt.xml.asm.prj.parser.NestedTagResolver;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import namlt.xml.asm.prj.model.Book;
import static namlt.xml.asm.prj.utils.CommonUtils.parseInt;
import static namlt.xml.asm.prj.utils.CommonUtils.parseDouble;
import static namlt.xml.asm.prj.utils.InternetUtils.crawl;

public class NxbTreCrawler extends BaseParser {
    
    private XMLInputFactory inputFactory = XMLInputFactory.newFactory();
    
    public NxbTreCrawler() {
        inputFactory.setProperty(
                XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
    }
    
    public static void main(String[] args) {
        NxbTreCrawler crawler = new NxbTreCrawler();
//        Book book = crawler.crawlBookPage("https://www.nxbtre.com.vn/sach/24269.html");
//        System.out.println(book);
        List<Book> books = crawler.crawlNextNewBooks(4, 5);
        System.out.println("Book number:" + books.size());
        books.forEach(System.out::println);
        
    }
    
    public Book crawlBookPage(String url) {
        Book book = null;
        ValueIdentifier identifier = new Book().getIdentifier();
        try {
            String htmlSource = crawl(url);
            if (htmlSource != null) {
                StringBuilder sb = new StringBuilder();
                NestedTagResolver.formatNestedTag(htmlSource, "html").forEach(sb::append);
                htmlSource = sb.toString();
            }
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(htmlSource));
            ParserHelper fragmentParser = new ParserHelper(reader);
            ParserHelper detailParser = new ParserHelper(reader);
            int event;
            while (reader.hasNext()) {
                event = reader.next();
                if (event == START_ELEMENT) {
                    if (isTag("div", reader) && equalClass(reader, null, "autBigImg")) {
                        fragmentParser.mark();
                        try {
                            fragmentParser.skipTo("img");
                            //in case book has no image
                            book = (book == null) ? new Book(url) : book;
                            String imageUrl = reader.getAttributeValue("", "src");
                            book.setImageUrl(imageUrl);
                        } catch (BoundReachedException e) {
                        }
                    } else if (isTag("div", reader) && equalClass(reader, null, "aut-detail")) {
                        book = (book == null) ? new Book(url) : book;
                        //read title
                        fragmentParser.mark();
                        try {
                            fragmentParser.skipToCharacter();
                            String title = fragmentParser.readTextInside();
                            book.setTitle(title != null ? title.trim() : null);
                        } catch (BoundReachedException e) {
                            //end fragment
                        }
                    } else if (isTag("ul", reader) && equalClass(reader, null, "itemDetail-cat")) {
                        Map<String, String> valueMap = new HashMap<>();
                        StringBuilder sb = new StringBuilder();
                        try {
                            while (true) {
                                String key = null;
                                String value = null;
                                //skip to new fragment detail
                                try {
                                    fragmentParser.skipTo("li");
                                } catch (BoundReachedException e) {
                                    break;
                                }
                                try {
                                    detailParser.mark();
                                    //get the key
                                    detailParser.skipToCharacter();
                                    key = detailParser.readTextInside();

                                    //get the value
                                    detailParser.skipToCharacter();
                                    sb = new StringBuilder();
                                    detailParser.skipToBound(sb);
                                } catch (BoundReachedException e) {
                                    //do nothing
                                } finally {
                                    //end fragment detail
                                    fragmentParser.addCounter(-1);
                                }
                                value = sb.toString();
                                identifier.indentify(key != null ? key.trim() : null,
                                        value != null ? value.trim() : null);
                            }
                        } catch (Exception e) {
                        }
                        Map<String, String> values = identifier.values();
                        String author = values.get("AUTHOR");
                        String translator = values.get("TRANSLATOR");
                        String pageNumber = values.get("PAGE_NUMBER");
                        String isbn = values.get("ISBN");
                        String price = values.get("PRICE");
                        
                        book.setAuthor(author != null ? author.replace("\n", "") : null);
                        book.setTranslator(translator != null ? translator.replace("\n", "") : null);
                        book.setPageSize(values.get("SIZE"));
                        //process page number here
                        book.setPageNumber(parseInt(pageNumber).orElse(null));
                        book.setIsbn(isbn != null ? isbn.replace("\n", "").replace(" ", "") : null);
                        //process price here
                        book.setPrice(parseDouble(price).orElse(null));
                        book.setDescription(values.get("DESCRIPTION"));
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return book;
    }
    
    public List<String> crawlNewBookUrls(String url) {
        List<String> urls = new ArrayList<>();
        try {
            String htmlSource = crawl(url);
            if (htmlSource != null) {
                StringBuilder sb = new StringBuilder();
                NestedTagResolver.formatNestedTag(htmlSource, "html").forEach(sb::append);
                htmlSource = sb.toString();
                
            }
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(htmlSource));
            ParserHelper fragmentParser = new ParserHelper(reader);
            int event;
            String link;
            while (reader.hasNext()) {
                event = reader.next();
                if (event == START_ELEMENT) {
                    if (isTag("div", reader) && equalClasses(reader, null, "bBox", "book-item")) {
                        fragmentParser.mark();
                        fragmentParser.skipTo("a");
                        link = reader.getAttributeValue("", "href");
                        if (link != null && !"".equals((link = link.trim()))) {
                            urls.add(link);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return urls;
    }
    
    public List<Book> crawlNextNewBooks(int start, int time) {
        List<Book> rs = new ArrayList<>();
        String tmp = "https://www.nxbtre.com.vn/tu-sach/trang-";
        List<String> urls = new ArrayList<>();
        if (start <= 0) {
            start = 1;
        }
        for (int i = start; i < time; i++) {
            String url = tmp + (i + 1) + '/';
            crawlNewBookUrls(url).forEach(s -> urls.add("https://www.nxbtre.com.vn" + s.replace("xem-them", "sach")));
        }
        urls.parallelStream().map(s -> crawlBookPage(s))
                .filter(b -> {
                    //check not null
                    if (b == null) {
                        return false;
                    }
                    //normalize image url
                    b.setImageUrl("https://www.nxbtre.com.vn" + b.getImageUrl());
                    //check title and price
                    if (b.getTitle() == null || b.getPrice() == null) {
                        return false;
                    }
                    //generate id
                    String url = b.getUrl().replace("https://www.nxbtre.com.vn/sach/", "");
                    Integer id = parseInt(url).orElse(null);
                    if (id == null) {
                        return false;
                    } else {
                        b.setId("nxbtre-" + id);
                    }
                    return true;
                })
                .forEach(b -> {
                    synchronized (rs) {
                        rs.add(b);
                    }
                });
        return rs;
    }
    
}
