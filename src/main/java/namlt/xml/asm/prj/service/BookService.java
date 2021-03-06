package namlt.xml.asm.prj.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import namlt.xml.asm.prj.common.BookCommon;
import namlt.xml.asm.prj.model.Book;
import namlt.xml.asm.prj.repository.BookRepository;
import static namlt.xml.asm.prj.service.BookUtilityService.copyBook;

public class BookService implements BookCommon {

    public Book add(Book b) throws NamingException, SQLException {
        b.setStatus(STATUS_ACTIVE);
        b.setQuantity(0);
        b.setInsertDate(new Date());
        return new BookRepository().insert(b);
    }

    public void update(Book b) throws NamingException, SQLException {
        BookRepository repository = new BookRepository();
        Book origin = repository.get(b.getId());
        copyBook(b, origin);
        repository.update(origin);
    }

    public Book get(String id) {
        try {
            Book rs = new BookRepository().get(id);
            if (rs != null) {
                rs.setExistedInDb(true);
            }
            return rs;
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void checkBooksExisted(List<Book> books) {
        if (books == null) {
            return;
        }
        books.parallelStream().forEach(book -> {
            book.setExistedInDb(true);
        });
    }

    public List<Book> getNew(Integer startAt, Integer nextRow) {
        List<Book> rs = null;
        if (startAt == null || nextRow == null) {
            startAt = 0;
            nextRow = 10;
        }
        try {
            rs = new BookRepository().getNew(startAt, nextRow);
            checkBooksExisted(rs);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        return rs;
    }

    public List<Book> search(String search, Integer startAt, Integer nextRow) {
        List<Book> rs = null;
        if (startAt == null || nextRow == null) {
            startAt = 0;
            nextRow = 10;
        }
        try {
            rs = new BookRepository().find(search, startAt, nextRow);
            checkBooksExisted(rs);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        return rs;
    }

    public List<Book> getOutOfStock(Integer startAt, Integer nextRow) {
        List<Book> rs = null;
        if (startAt == null || nextRow == null) {
            startAt = 0;
            nextRow = 10;
        }
        try {
            rs = new BookRepository().getOutOfStock(startAt, nextRow);
            checkBooksExisted(rs);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        return rs;
    }

    public List<Book> getDisable(Integer startAt, Integer nextRow) {
        List<Book> rs = null;
        if (startAt == null || nextRow == null) {
            startAt = 0;
            nextRow = 10;
        }
        try {
            rs = new BookRepository().getDisable(startAt, nextRow);
            checkBooksExisted(rs);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        return rs;
    }

    public int count() {
        try {
            return new BookRepository().count();
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int countActive() {
        try {
            return new BookRepository().countActive();
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int countOutOfStock() {
        try {
            return new BookRepository().countOutOfStock();
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int countDisable() {
        try {
            return new BookRepository().countDisable();
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public int countSearch(String s) {
        try {
            return new BookRepository().countSearch(s);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public List<Book> searchByTitleAndType(String search, int type, Integer startAt, Integer nextRow) {
        List<Book> rs = null;
        if (startAt == null || nextRow == null) {
            startAt = 0;
            nextRow = 10;
        }
        try {
            rs = new BookRepository().findByTitleAndType(search, type, startAt, nextRow);
            checkBooksExisted(rs);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            rs = new ArrayList<>();
        }
        return rs;
    }

    public int countSearchByTitleAndType(String s, int type) {
        try {
            return new BookRepository().countSearchByTitleAndType(s, type);
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean setActiveBook(String id){
        return changeBookStatus(id, STATUS_ACTIVE);
    }
    public boolean setDisableBook(String id){
        return changeBookStatus(id, STATUS_DISABLE);
    }
    public boolean setOutOfStockBook(String id){
        return changeBookStatus(id, STATUS_OUT_OF_STOCK);
    }
    
    private boolean changeBookStatus(String id, int status) {
        BookRepository repository = new BookRepository();
        try {
            Book book = repository.get(id);
            if (book == null) {
                return false;
            }
            book.setStatus(status);
            repository.update(book);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BookService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
