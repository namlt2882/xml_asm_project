var bookCache;

window.addEventListener('load', function () {
    bookCache = new BookCache();
    bookCache.initData();
}, false);

function BookCache() {
    var xmlTree;
    this.initData = function () {
        var holder = document.getElementById("xmlData");
        if (holder == null) {
            return;
        }
        var xmlData = holder.text;
        var parser = new DOMParser();
        this.xmlTree = parser.parseFromString(xmlData, "text/xml");
    }

    this.getAll = function () {
        var rs = [];
        var books = this.xmlTree.getElementsByTagName("book");
        for (var i = 0; i < books.length; i++) {
            var book = books[i];
            rs.push(this.transformBookToObject(book));
        }
        return rs;
    }

    this.findBook = function (id) {
        var books = this.xmlTree.getElementsByTagName("book");
        for (var i = 0; i < books.length; i++) {
            var book = books[i];
            if (id === book.getAttribute("id")) {
                return this.transformBookToObject(book);
            }
        }
        return null;
    }

    this.getBookAttribute = function (book, attributeName) {
        var attr = book.getElementsByTagName(attributeName)[0];
        if (attr == null) {
            return null;
        }
        var childNode = attr.childNodes[0];
        if (childNode == null) {
            return "";
        }
        return childNode.nodeValue;
    }

    this.transformBookToObject = function (book) {
        var rs = new Book();
        rs.id = book.getAttribute("id");
        rs.title = this.getBookAttribute(book, "title");
        rs.author = this.getBookAttribute(book, "author");
        rs.isbn = this.getBookAttribute(book, "isbn");
        rs.translator = this.getBookAttribute(book, "translator");
        rs.pageSize = this.getBookAttribute(book, "pageSize");
        rs.pageNumber = this.getBookAttribute(book, "pageNumber");
        rs.price = this.getBookAttribute(book, "price");
        rs.url = this.getBookAttribute(book, "url");
        rs.imageUrl = this.getBookAttribute(book, "imageUrl");
        rs.status = this.getBookAttribute(book, "status");
        rs.quantity = this.getBookAttribute(book, "quantity");
        rs.existedInDb = this.getBookAttribute(book, "existedInDb");
        rs.description = this.getBookAttribute(book, "description");
        return rs;
    }
}

function Book() {
    var id;
    var title;
    var author;
    var isbn;
    var translator;
    var pageSize;
    var pageNumber;
    var price;
    var url;
    var imageUrl;
    var status;
    var quantity;
    var existedInDb;
    var description;

    this.toXml = function () {
        var utitlity = new Utility();
        var rs = "<book id=\"" + (this.id === null ? "" : this.id) + "\">";
        rs += "<title>" + (this.title === null ? "" : utitlity.htmlEntitiesDecode(this.title)) + "</title>";
        rs += "<author>" + (this.author === null ? "" : utitlity.htmlEntitiesDecode(this.author)) + "</author>";
        rs += "<isbn>" + (this.isbn === null ? "" : this.isbn) + "</isbn>";
        rs += "<translator>" + (this.translator === null ? "" : utitlity.htmlEntitiesDecode(this.translator)) + "</translator>";
        rs += "<pageSize>" + (this.pageSize === null ? "" : this.pageSize) + "</pageSize>";
        rs += "<pageNumber>" + (this.pageNumber === null ? "" : this.pageNumber) + "</pageNumber>";
        rs += "<price>" + (this.price === null ? "" : this.price) + "</price>";
        rs += "<url>" + (this.url === null ? "" : this.url) + "</url>";
        rs += "<imageUrl>" + (this.imageUrl === null ? "" : this.imageUrl) + "</imageUrl>";
        rs += "<status>" + (this.status === null ? "" : this.status) + "</status>";
        rs += "<quantity>" + (this.quantity === null ? "" : this.quantity) + "</quantity>";
        rs += "<existedInDb>" + (this.existedInDb === null ? "" : this.existedInDb) + "</existedInDb>";
        var description = (this.description === null ? "" : utitlity.htmlEntitiesDecode(this.description));
        rs += "<description>" + description + "</description>";
        rs += "</book>"
        return rs;
    }
}