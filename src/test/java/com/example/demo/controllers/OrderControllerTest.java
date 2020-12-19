package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    //private UserOrder userOrder = mock(UserOrder.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

    }

    @Test
    public void testSubmit() {
        User user = getUser();
        Cart cart = getCart();
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
        UserOrder result = response.getBody();
        assertNotNull(result);
        assertEquals(result.getItems(), cart.getItems());
        assertEquals(result.getUser(), user);
    }

    @Test
    public void testGetOrdersForUser() {
        User user = getUser();
        UserOrder order = getOrder(user);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order));

        final ResponseEntity<List<UserOrder>> response = orderController
                .getOrdersForUser(user.getUsername());
        List<UserOrder> orders = response.getBody();

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertTrue(orders.contains(order));

    }

    private User getUser() {
        User u = new User();
        u.setId(1);
        u.setUsername("testName");
        u.setPassword("hashedPassword");
        u.setSalt("testSalt");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(Collections.emptyList());
        u.setCart(cart);
        return u;
    }

    private UserOrder getOrder(User user) {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Ball");
        item1.setDescription("Round toy");
        item1.setPrice(BigDecimal.valueOf(9.99));

        Item item2 = new Item();
        item1.setId(2L);
        item1.setName("Doll");
        item1.setDescription("Just a doll");
        item1.setPrice(BigDecimal.valueOf(19.99));

        UserOrder o = new UserOrder();
        o.setId(2L);
        o.setItems(Arrays.asList(item1, item2));
        o.setTotal(BigDecimal.valueOf(29.98));
        o.setUser(user);

        return o;
    }

    private Cart getCart() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Ball");
        item1.setDescription("Round toy");
        item1.setPrice(BigDecimal.valueOf(9.99));

        Item item2 = new Item();
        item1.setId(2L);
        item1.setName("Doll");
        item1.setDescription("Just a doll");
        item1.setPrice(BigDecimal.valueOf(19.99));

        Cart cart = new Cart();
        cart.setItems(Arrays.asList(item1, item2));
        cart.setTotal(BigDecimal.valueOf(29.98));

        return cart;
    }
}
