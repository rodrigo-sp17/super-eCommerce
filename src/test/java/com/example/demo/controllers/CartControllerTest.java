package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

    }

    @Test
    public void testAddToCart() {
        User user = getUser();
        Cart cart = getCart();
        //user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(cart.getItems().get(0)));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(2);
        request.setUsername(user.getUsername());
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        Cart result = response.getBody();

        assertNotNull(result);
        assertEquals(cart.getItems(), result.getItems());
    }

    @Test
    public void testRemoveFromCart() {
        User user = getUser();
        Cart cart = getCart();
        user.setCart(cart);
        when(userRepository.findByUsername(any())).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(cart.getItems().get(0)));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(2);
        request.setUsername(user.getUsername());
        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        Cart result = response.getBody();

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());

    }

    private User getUser() {
        User u = new User();
        u.setId(1);
        u.setUsername("testName");
        u.setPassword("hashedPassword");
        u.setSalt("testSalt");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>());
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
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item1);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(19.98));

        return cart;
    }
}
