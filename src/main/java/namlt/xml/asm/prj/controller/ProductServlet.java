package namlt.xml.asm.prj.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import namlt.xml.asm.prj.common.BookCommon;
import static namlt.xml.asm.prj.controller.ProductController.MAX_ITEM_PER_PAGE;
import namlt.xml.asm.prj.model.Book;
import namlt.xml.asm.prj.model.BookList;
import namlt.xml.asm.prj.service.BookService;
import namlt.xml.asm.prj.utils.MarshallerUtils;

@WebServlet(name = "ProductServlet", urlPatterns = {"/product_data"})
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer page = calculatePage(request.getParameter("page"));

        Integer startAt = MAX_ITEM_PER_PAGE * (page - 1);
        Integer nextRow = MAX_ITEM_PER_PAGE;
        String type = "active";
        String tmp = request.getParameter("type");
        List<Book> rs = null;
        if (tmp != null) {
            type = tmp;
        }
        int maxPage = 0;
        BookService bookService = new BookService();
        String search = request.getParameter("search");
        if (search == null) {
            switch (type) {
                case "active":
                    rs = bookService.getNew(startAt, nextRow);
                    maxPage = (int) Math.ceil(bookService.countActive() * 1.0 / MAX_ITEM_PER_PAGE);
                    break;
                case "out-of-stock":
                    rs = bookService.getOutOfStock(startAt, nextRow);
                    maxPage = (int) Math.ceil(bookService.countOutOfStock() * 1.0 / MAX_ITEM_PER_PAGE);
                    break;
                case "disable":
                    rs = bookService.getDisable(startAt, nextRow);
                    maxPage = (int) Math.ceil(bookService.countDisable() * 1.0 / MAX_ITEM_PER_PAGE);
                    break;
            }
        } else if (!"".equals(search.trim())) {
            int bookType = BookCommon.STATUS_ACTIVE;
            switch (type) {
                case "out-of-stock":
                    bookType = BookCommon.STATUS_OUT_OF_STOCK;
                    break;
                case "disable":
                    bookType = BookCommon.STATUS_DISABLE;
                    break;
            }
            rs = bookService.searchByTitleAndType(search, bookType, startAt, nextRow);
            maxPage = (int) Math.ceil(bookService.countSearchByTitleAndType(search, bookType));
        }
        //marshalization
        String xmlData = "";
        if (rs != null) {
            try {
                BookList bl = new BookList(rs);
                xmlData = MarshallerUtils.marshall(bl);
            } catch (JAXBException ex) {
                Logger.getLogger(CrawlServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        request.setAttribute("books", rs);
        request.setAttribute("xmlData", xmlData);
        request.setAttribute("page", page);
        request.setAttribute("pageQuantity", maxPage);
    }

    private int calculatePage(String tmp) {
        Integer page = null;
        if (tmp != null) {
            try {
                page = Integer.parseInt(tmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (page == null) {
            page = 1;
        }
        if (page <= 0) {
            page = 1;
        }
        return page;
    }

}
