var bookDetailFrame;

window.addEventListener('load', function () {
    bookDetailFrame = new BookDetailFrame();
    bookDetailFrame.init();
}, false);

function BookDetailFrame() {
    this.bookDetailModel;
    this._bookDetailId;
    this._bookDetailAuthor;
    this._bookDetailTranslator;
    this._bookDetailSize;
    this._bookDetailPageNumber;
    this._bookDetailPrice;
    this._bookDetailDescription;
    this._bookDetailTitle;
    this._bookDetailImageUrl;
    this._bookDetailUrl;
    this._bookDetailIsbn;
    this._bookDetailSubmit;

    this.addAllBook = function () {
        var tmp = bookCache.getAll();
        var books = [];
        for (var i = 0; i < tmp.length; i++) {
            var b = tmp[i];
            if (b.existedInDb == 'false') {
                books.push(b);
            }
        }
        var validateBooks = [];
        for (var i = 0; i < books.length; i++) {
            var b = books[i];
            if (this.validateBook(b)) {
                validateBooks.push(b);
            }
        }
        var invalidateBookCounter = books.length - validateBooks.length;
        var confirmMessage = "Có " + invalidateBookCounter + " sản phẩm lỗi trên \n\
tổng số " + books.length + " sản phẩm có thể thêm. Hành động này sẽ thêm " + validateBooks.length + " sản phẩm, chắc chứ?";
        if (confirm(confirmMessage)) {
            bookController.addListBook(validateBooks, function (xhr) {
                var success = parseInt(xhr.responseText);
                alert("Thêm thành công " + success + " sản phẩm!");
                window.location.reload();
            }, function (xhr) {
                alert("Fail hết rồi :v !");
            });
        }
        return false;
    };

    this.validateBook = function (book) {
        if (book.id === null || book.id === '') {
            return false;
        }
        if (book.title === null || book.title === '') {
            return false;
        }
        if (book.author === null || book.author === '') {
            return false;
        }
        if (book.price === null || book.price === '') {
            return false;
        }
        return true;
    };

    this.addOrUpdate = function () {
        var id = this._bookDetailId.value;
        var book = bookCache.findBook(id);
        if (book != null) {
            this.updateBookObj(book);
            var isExisted = (book.existedInDb == 'true');
            if (!isExisted) {
                bookController.add(book, function (xhr) {
                    alert("Thêm sản phẩm thành công với id '" + xhr.responseText + "'!");
                    window.location.reload();
                }, function (xhr) {
                    alert("Thất bại toàn tập!");
                });
            } else {
                bookController.update(book, function (xhr) {
                    alert("Cập nhật sản phẩm thành công với id '" + xhr.responseText + "'!");
                    window.location.reload();
                }, function (xhr) {
                    alert("Thất bại toàn tập!");
                });
            }
        } else {
            alert("Đã xảy ra lỗi! Vui lòng thử lại sau!");
        }
        return false;
    };

    this.updateBookObj = function (book) {
        book.isbn = this._bookDetailIsbn.value;
        book.author = this._bookDetailAuthor.value;
        book.translator = this._bookDetailTranslator.value;
        book.pageSize = this._bookDetailSize.value;
        book.pageNumber = this._bookDetailPageNumber.value;
        book.price = this._bookDetailPrice.value;
        book.description = this._bookDetailDescription.value;
    };

    this.init = function () {
        this.bookDetailModel = document.getElementById("book_detail_model");
        this.bookDetailClose = document.getElementsByClassName("book_detail_close")[0];
        this.bookDetailClose.model = this.bookDetailModel;
        this.bookDetailClose.onclick = function () {
            this.model.style.display = "none";
        };
        this._bookDetailId = document.getElementById("book_detail_id");
        this._bookDetailAuthor = document.getElementById("book_detail_author");
        this._bookDetailTranslator = document.getElementById("book_detail_translator");
        this._bookDetailSize = document.getElementById("book_detail_size");
        this._bookDetailPageNumber = document.getElementById("book_detail_page_number");
        this._bookDetailPrice = document.getElementById("book_detail_price");
        this._bookDetailDescription = document.getElementById("book_detail_description");
        this._bookDetailIsbn = document.getElementById("book_detail_isbn");
        this._bookDetailTitle = document.getElementById("book_detail_title");
        this._bookDetailImageUrl = document.getElementById("book_detail_imageUrl");
        this._bookDetailUrl = document.getElementById("book_detail_url");
        //error
        this._bookDetailSubmit = document.getElementById("book_detail_submit");
    };

    this.isNullOrEmpty = function (s) {
        return (s != null && s !== "") ? false : true;
    };

    this.showBookDetailModel = function (id) {
        var bookObj = bookCache.findBook(id);
        var author = bookObj.author;
        var translator = bookObj.translator;
        var size = bookObj.pageSize;
        var pageNumber = bookObj.pageNumber;
        var price = bookObj.price;
        var description = bookObj.description;
        var title = bookObj.title;
        var imageUrl = bookObj.imageUrl;
        var url = bookObj.url;
        var isbn = bookObj.isbn;
        if (bookObj.existedInDb == 'true') {
            this._bookDetailSubmit.value = "Cập nhật sản phẩm";
        } else {
            this._bookDetailSubmit.value = "Thêm sản phẩm";
        }
        var utitlity = new Utility();

        this._bookDetailId.value = id;
        this._bookDetailAuthor.value = utitlity.utf8Decode(author);
        this._bookDetailTranslator.value = utitlity.utf8Decode(translator);
        this._bookDetailSize.value = size;
        this._bookDetailPageNumber.value = pageNumber;
        this._bookDetailPrice.value = price;
        this._bookDetailIsbn.value = isbn;
        this._bookDetailDescription.innerHTML = utitlity.htmlEntitiesDecode(description);
        this._bookDetailTitle.innerHTML = utitlity.htmlEntitiesDecode(title);
        this._bookDetailImageUrl.src = imageUrl;
        this._bookDetailUrl.href = url;
        this.bookDetailModel.style.display = "block";
    };
}




