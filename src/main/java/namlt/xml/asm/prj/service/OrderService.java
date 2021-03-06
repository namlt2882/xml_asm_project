package namlt.xml.asm.prj.service;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.naming.NamingException;
import namlt.xml.asm.prj.common.OrderCommon;
import namlt.xml.asm.prj.model.Order;
import namlt.xml.asm.prj.model.OrderDetail;
import namlt.xml.asm.prj.repository.OrderDetailRepository;
import namlt.xml.asm.prj.repository.OrderRepository;

public class OrderService implements OrderCommon {

    public Order addOrder(Order o) throws Exception {
        o.setStatus(STATUS_ORDER_NEW);
        o.setInsertDate(new Date());
        //insert order
        OrderRepository orderRepository = new OrderRepository();
        Order newOrder = orderRepository.insert(o);
        //update order id for order detail
        List<OrderDetail> orderDetails = o.getOrderDetails();
        orderDetails.forEach(od -> {
            od.setOrderId(newOrder.getId());
        });
        //insert order detail
        OrderDetailRepository orderDetailRepository = new OrderDetailRepository();
        for (OrderDetail orderDetail : orderDetails) {
            orderDetailRepository.insert(orderDetail);
        }
        return newOrder;
    }

    public boolean cancelOrder(int id) throws Exception {
        OrderRepository repository = new OrderRepository();
        Order order = repository.get(id);
        if (order == null) {
            return false;
        }
        order.setStatus(STATUS_ORDER_CANCEL);
        repository.update(order);
        return true;
    }

    public List<Order> getOrderByCustomerId(String customerId) throws NamingException, SQLException {
        List<Order> rs = new OrderRepository().getByCustomerId(customerId.trim());
        List<OrderDetail> orderDetails;
        OrderDetailRepository orderDetailRepository = new OrderDetailRepository();
        for (Order r : rs) {
            orderDetails = orderDetailRepository.getByOrderId(r.getId());
            r.setOrderDetails(orderDetails);
        }
        return rs;
    }
}
