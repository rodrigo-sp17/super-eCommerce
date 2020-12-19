package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        List<Item> items = getItems();
        when(itemRepository.findAll()).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> results = response.getBody();
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(items, results);
    }

    @Test
    public void testGetItemById() {
        List<Item> items = getItems();
        Item item1 = items.get(0);
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        final ResponseEntity<Item> response = itemController.getItemById(item1.getId());
        Item result = response.getBody();

        assertNotNull(result);
        assertEquals(item1, result);
    }

    @Test
    public void testGetItemsByName() {
        List<Item> items = getItems();
        Item item2 = items.get(1);
        when(itemRepository.findByName(item2.getName())).thenReturn(Arrays.asList(item2));

        final ResponseEntity<List<Item>> response = itemController.getItemsByName(item2.getName());
        Item result = response.getBody().get(0);

        assertNotNull(result);
        assertEquals(item2, result);
    }

    private List<Item> getItems() {
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

        return Arrays.asList(item1, item2);
    }
}
