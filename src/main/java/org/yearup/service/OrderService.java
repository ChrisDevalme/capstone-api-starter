package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDateTime;

@Service
public class OrderService
{
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartService shoppingCartService;

    public OrderService(OrderRepository orderRepository,
                        OrderLineItemRepository orderLineItemRepository,
                        ShoppingCartService shoppingCartService)
    {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartService = shoppingCartService;
    }

    @Transactional
    public Order checkout(int userId)
    {
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        if (cart.getItems().isEmpty())
        {
            throw new RuntimeException("Cannot checkout with an empty cart.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());

        order.setAddress("");
        order.setCity("");
        order.setState("");
        order.setZip("");
        order.setShippingAmount(0);

        Order savedOrder = orderRepository.save(order);

        for (ShoppingCartItem cartItem : cart.getItems().values())
        {
            OrderLineItem lineItem = new OrderLineItem();

            lineItem.setOrderId(savedOrder.getOrderId());
            lineItem.setProductId(cartItem.getProduct().getProductId());
            lineItem.setSalesPrice(cartItem.getProduct().getPrice());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setDiscount(cartItem.getDiscountPercent());

            orderLineItemRepository.save(lineItem);
        }

        shoppingCartService.clearCart(userId);

        return savedOrder;
    }
}