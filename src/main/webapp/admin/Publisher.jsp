<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page session="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:include page="/crawl"/>
<jsp:include page="/category_crawling_servlet"/>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dữ liệu nhà xuất bản</title>
        <script type="text/javascript" src="/js/Common.js"></script>
        <script type="text/javascript" src="/js/BookController.js"></script>
        <script type="text/javascript" src="/js/BookCache.js"></script>
        <script type="text/javascript" src="/js/CategoryCache.js"></script>
        <script>
            function getNewBook() {
                var redirectUrl = "http://" + window.location.hostname + ":" + window.location.port + "/admin/Publisher.jsp?publisher=";
                var element = document.getElementById("publisher");
                redirectUrl += element.value;
                var categoryUrl = document.getElementById("category");
                if (categoryUrl !== null && categoryUrl.value !== '--') {
                    redirectUrl += "&categoryUrl=" + categoryUrl.value;
                }
                window.location.href = redirectUrl;
                return false;
            }
            function searchBook() {
                var redirectUrl = "http://" + window.location.hostname + ":" + window.location.port + "/admin/Publisher.jsp?publisher=";
                var element = document.getElementById("publisher");
                redirectUrl += element.value;
                var search = document.getElementById("searchTxt");
                if (search !== null) {
                    redirectUrl += "&search=" + search.value;
                }
                console.log(redirectUrl);
                window.location.href = redirectUrl;
                return false;
            }
            var crawlCategoryCache;
            var categoryLayout;
            const url = new URL(window.location.href);
            window.addEventListener('load', function () {
                crawlCategoryCache = new CategoryCache();
                crawlCategoryCache.initData("crawlCategoryXmlData");
                categoryLayout = new CategoryLayout();
                var publisher = "nxb-nhanam";
                if (url.searchParams.get("publisher") !== null) {
                    publisher = url.searchParams.get("publisher");
                }
                if (url.searchParams.get("categoryUrl") !== null) {
                    var cat = crawlCategoryCache.findCategoryByUrl(url.searchParams.get('categoryUrl'));
                    if (cat !== null) {
                        var title = document.getElementById("title");
                        title.innerHTML = title.innerHTML + " thuộc danh mục <a title='" + cat.name + "' target='_blank' href='"
                                + cat.url + "'>" + cat.name + "</a>";
                    }
                }
                if (crawlCategoryCache.xmlTree !== null) {
                    categoryLayout.initLayout("category", crawlCategoryCache.getAll(), publisher,
                            function (category) {
                                var categoryUrl = url.searchParams.get("categoryUrl");
                                var rs = category.url === categoryUrl;
                                return rs;
                            });
                }
            }, false);
        </script>
        <c:if test='${not empty xmlData}'>
            <script id="xmlData" type="text/xmldata">${xmlData}</script>
        </c:if>
        <c:if test='${not empty crawlCategoryXmlData}'>
            <script id="crawlCategoryXmlData" type="text/xmldata">${crawlCategoryXmlData}</script>
        </c:if>
    </head>
    <body class="html front not-logged-in one-sidebar sidebar-second site-name-hidden browserChrome browserChrome6">
        <jsp:include page="BookDetail.jsp"/>
        <div id="page" class="container">
            <jsp:include page="AdminPageHeader.jsp"/>
            <div id="columns">
                <div class="columns-inner clearfix">
                    <div id="content-column" style="">
                        <div class="content-inner">
                            <section id="main-content" role="main">
                                <div id="content">
                                    <div class="region region-content">
                                        <section id="block-views-sach-moi-block" class="block block-views ">
                                            <div class="block-inner clearfix">
                                                <div class="block-content content">
                                                    <form action="Publisher.jsp" style="margin-left: 500px;">
                                                        <div>
                                                            Chọn nhà xuất bản:
                                                            <select name="publisher" id="publisher" 
                                                                    onchange="categoryLayout.initLayout('category', crawlCategoryCache.getAll(), this.value, function (category) {
                                                                                var categoryUrl = url.searchParams.get('categoryUrl');
                                                                                return category.url === categoryUrl;
                                                                            });">
                                                                <option value="nxb-nhanam" <c:if test="${param.publisher=='nxb-nhanam'}">selected=""</c:if>>Nhã Nam</option>
                                                                <option value="nxb-tre" <c:if test="${param.publisher=='nxb-tre'}">selected=""</c:if>>Trẻ</option>
                                                                </select><br/>
                                                                Danh mục:
                                                                <select name="category" id="category">
                                                                </select>
                                                                <button onclick="return getNewBook()">Sản phẩm mới</button>
                                                            </div>
                                                            <input type="text" name="search" value="${param.search}" id="searchTxt"/>
                                                        <button onclick="return searchBook();">Tìm kiếm</button>
                                                    </form>
                                                </div>
                                                <c:if test="${empty param.search}">
                                                    <h2 class="block-title" id="title">Sản phẩm mới nhất 
                                                        của nxb 
                                                        <c:if test="${empty param.publisher or param.publisher=='nxb-nhanam'}">Nhã Nam</c:if>
                                                        <c:if test="${param.publisher=='nxb-tre'}">Trẻ</c:if>
                                                        </h2>
                                                </c:if>
                                                <!--Add all form-->
                                                <form action="/rest/product/list" method="POST" onsubmit="return bookDetailFrame.addAllBook();">
                                                    <input type="submit" value="Thêm tất cả"/>
                                                </form>
                                                <!--End add all form-->
                                                <c:if test="${not empty param.search}">
                                                    <h2 class="block-title">Kết quả tìm kiếm cho "${param.search}" 
                                                        của nxb 
                                                        <c:if test="${empty param.publisher}">Nhã Nam</c:if>
                                                        <c:if test="${param.publisher=='nxb-nhanam'}">Nhã Nam</c:if>
                                                        <c:if test="${param.publisher=='nxb-tre'}">Trẻ</c:if>
                                                        </h2>
                                                </c:if>
                                                <div class="block-content content">
                                                    <div class="view view-sach-moi view-id-sach_moi view-display-id-block view-dom-id-4d0e7c14c2df0334da04712bf6aed109">
                                                        <div class="view-content">
                                                            <div class="item-list-sm">
                                                                <c:choose>
                                                                    <c:when test="${not empty books and fn:length(books)>0}">
                                                                        <ul>
                                                                            <c:forEach items="${books}" var="book">
                                                                                <li class="views-row">  
                                                                                    <div class="views-field views-field-field-sach-anh-dai-dien">        
                                                                                        <div class="field-content">
                                                                                            <div style="width: 100px;height: 250px;">
                                                                                                <img src="${book.imageUrl}" style="max-width: 100%;max-height: 100%;margin: 0px;"
                                                                                                     class="book_detail_trigger" onclick="bookDetailFrame.showBookDetailModel('${book.id}');">
                                                                                            </div>
                                                                                        </div>  
                                                                                    </div>  
                                                                                    <div class="views-field views-field-nothing">        
                                                                                        <span class="field-content">
                                                                                            <div>
                                                                                                <div style="margin-bottom: 20px;">
                                                                                                    <font style="color: #00562f"><b>${book.title}</b></font>
                                                                                                </div>
                                                                                                <div>
                                                                                                    <b>Tác giả:</b> ${book.author}
                                                                                                </div>
                                                                                                <div>
                                                                                                    <b>Giá bìa:</b> ${book.price} VND
                                                                                                </div>
                                                                                                <c:if test="${book.existedInDb}">
                                                                                                    <div>
                                                                                                        <font style="color: red">Đã tồn tại</font>
                                                                                                    </div>
                                                                                                </c:if>
                                                                                            </div>
                                                                                        </span>  
                                                                                    </div>
                                                                                </li>
                                                                            </c:forEach>
                                                                        </ul>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <h3>Không tìm thấy kết quả phù hợp</h3>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                            <c:if test="${not empty param.page and empty page}">
                                                                <c:set var="page" value="${param.page}"/>
                                                            </c:if>
                                                            <c:if test="${empty param.search and not empty page}">
                                                                <div class="item-list">
                                                                    <ul class="pager">
                                                                        <c:if test="${not empty page and page > 1}">
                                                                            <li class="pager-next">
                                                                                <a title="Đến trang trước" href="
                                                                                   <c:url value="/admin/Publisher.jsp">
                                                                                       <c:if test="${not empty param.publisher}">
                                                                                           <c:param name="publisher" value="${param.publisher}"/>
                                                                                       </c:if>
                                                                                       <c:if test="${not empty page}">
                                                                                           <c:param name="page" value="${page - 1}"/>
                                                                                       </c:if>
                                                                                   </c:url>">< Sản phẩm mới hơn
                                                                                </a>
                                                                            </li>
                                                                        </c:if>
                                                                        <li class="pager-next">
                                                                            <a title="Đến trang kế sau" href="
                                                                               <c:url value="/admin/Publisher.jsp">
                                                                                   <c:if test="${not empty param.publisher}">
                                                                                       <c:param name="publisher" value="${param.publisher}"/>
                                                                                   </c:if>
                                                                                   <c:if test="${not empty param.categoryUrl}">
                                                                                       <c:param name="categoryUrl" value="${param.categoryUrl}"/>
                                                                                   </c:if>
                                                                                   <c:if test="${not empty page}">
                                                                                       <c:param name="page" value="${page + 1}"/>
                                                                                   </c:if>
                                                                                   <c:if test="${empty page}">
                                                                                       <c:param name="page" value="2"/>
                                                                                   </c:if>
                                                                               </c:url>">Sản phẩm cũ hơn >
                                                                            </a>
                                                                        </li>
                                                                    </ul>
                                                                </div>  
                                                            </c:if>
                                                        </div>  
                                                    </div>
                                                </div>    
                                            </div>
                                    </div>
                            </section>
                        </div>
                    </div>
                    </section>
                </div>
            </div>
        </div>
    </body>
</html>
