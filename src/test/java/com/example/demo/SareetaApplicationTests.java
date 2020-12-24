package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class SareetaApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JacksonTester<CreateUserRequest> userRequestJson;

	@Autowired
	private JacksonTester<User> userJson;

	@Autowired
	private JacksonTester<ModifyCartRequest> modifyCartJson;

	@Test
	public void contextLoads() {
	}

	@Test
	public void integrationTest() throws Exception {
		// creates user
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("Digaum");
		userRequest.setPassword("abcd1234");
		userRequest.setConfirmPassword("abcd1234");
		mvc.perform(
				post(new URI("/api/user/create"))
						.content(userRequestJson.write(userRequest).getJson())
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		// logins user
		User user = new User();
		user.setUsername(userRequest.getUsername());
		user.setPassword(userRequest.getPassword());

		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put("username", userRequest.getUsername());
		jsonObject1.put("password", userRequest.getPassword());

		MvcResult result = mvc.perform(
				post(new URI("/login"))
						.content(jsonObject1.toString())
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andReturn();

		String jwtToken = result.getResponse().getHeader("Authorization");

		// get items
		mvc.perform(
				get(new URI("/api/item")).header("Authorization", jwtToken))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Round Widget")))
				.andExpect(content().string(containsString("Square Widget")));

		// adds item to cart
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setUsername(user.getUsername());
		cartRequest.setItemId(1L);
		cartRequest.setQuantity(2);
		mvc.perform(
				post(new URI("/api/cart/addToCart"))
						.header("Authorization", jwtToken)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.content(modifyCartJson.write(cartRequest).getJson()))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Round Widget")));

		// submit order
		mvc.perform(
				post(new URI("/api/order/submit/" + user.getUsername()))
				.header("Authorization", jwtToken))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Round Widget")));

		// get orders for cart
		mvc.perform(
				get(new URI("/api/order/history/" + user.getUsername()))
				.header("Authorization", jwtToken))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Round Widget")));

	}

	@Test
	public void testUnauthorizedAccess() throws Exception {
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("Nique");
		userRequest.setPassword("alfafa1234");
		userRequest.setConfirmPassword("alfafa123");

		// Tries to access blocked resource
		mvc.perform(
				get(new URI("/api/item")))
				.andExpect(status().isForbidden());

		// Tries to create user with wrong parameters
		mvc.perform(
				post(new URI("/api/user/create"))
						.content(userRequestJson.write(userRequest).getJson())
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isBadRequest());

		// Creates user
		userRequest.setConfirmPassword("alfafa1234");
		mvc.perform(
				post(new URI("/api/user/create"))
						.content(userRequestJson.write(userRequest).getJson())
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());

		// Tests wrong login
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put("username", userRequest.getUsername());
		jsonObject1.put("password", "wrongpassword");

		mvc.perform(
				post(new URI("/login"))
						.content(jsonObject1.toString())
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isUnauthorized());

		// Test right login
		jsonObject1.put("password", userRequest.getPassword());

		mvc.perform(
				post(new URI("/login"))
						.content(jsonObject1.toString())
						.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk());
	}


}
