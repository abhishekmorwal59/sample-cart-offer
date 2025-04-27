package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.ApplyOfferRequest;
import com.springboot.controller.ApplyOfferResponse;
import com.springboot.controller.OfferRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {

	// ✅ URL Constants
	private static final String URL_OFFER = "http://localhost:9001/api/v1/offer";
	private static final String URL_APPLY_OFFER = "http://localhost:9001/api/v1/cart/apply_offer";

	@Before
	public void setup() {
		// If you have any setup before test cases
	}

	// ✅ Helper: Add Offer
	private boolean addOffer(OfferRequest offerRequest) throws Exception {
		URL url = new URL(URL_OFFER);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestMethod("POST");

		ObjectMapper mapper = new ObjectMapper();
		String postParams = mapper.writeValueAsString(offerRequest);

		try (OutputStream os = con.getOutputStream()) {
			os.write(postParams.getBytes());
			os.flush();
		}

		int responseCode = con.getResponseCode();
		return responseCode == HttpURLConnection.HTTP_OK;
	}

	// ✅ Helper: Apply Offer
	private int applyOffer(int cartValue, int userId, int restaurantId) throws Exception {
		URL url = new URL(URL_APPLY_OFFER);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestMethod("POST");

		ApplyOfferRequest request = new ApplyOfferRequest();
		request.setCart_value(cartValue);
		request.setUser_id(userId);
		request.setRestaurant_id(restaurantId);

		ObjectMapper mapper = new ObjectMapper();
		String postParams = mapper.writeValueAsString(request);

		try (OutputStream os = con.getOutputStream()) {
			os.write(postParams.getBytes());
			os.flush();
		}

		InputStream responseStream = con.getInputStream();
		ApplyOfferResponse response = mapper.readValue(responseStream, ApplyOfferResponse.class);
		return response.getCart_value();
	}

	// Basic Success Tests
	@Test public void testFlatXAmountOfferApplied() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(1,"FLATX",10,segments)); Assert.assertEquals(190, applyOffer(200,1,1)); }
	@Test public void testFlatXPercentageOfferApplied() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(2,"FLATX%",10,segments)); Assert.assertEquals(180, applyOffer(200,1,2)); }
	@Test public void testNoOfferAvailable() throws Exception { Assert.assertEquals(200, applyOffer(200,1,999)); }
	@Test public void testUserSegmentMismatch() throws Exception { List<String> segments = Arrays.asList("p2"); addOffer(new OfferRequest(3,"FLATX",10,segments)); Assert.assertEquals(200, applyOffer(200,1,3)); }
	@Test public void testZeroOfferValue() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(4,"FLATX",0,segments)); Assert.assertEquals(200, applyOffer(200,1,4)); }
	@Test public void testNegativeOfferValue() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(5,"FLATX",-10,segments)); Assert.assertEquals(210, applyOffer(200,1,5)); }
	@Test public void testInvalidOfferType() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(6,"INVALID",10,segments)); Assert.assertEquals(180, applyOffer(200,1,6)); }

	// Multi-offers and Segment tests
	@Test public void testMultipleOffersCorrectOneApplied() throws Exception { List<String> seg1 = Arrays.asList("p1"); List<String> seg2 = Arrays.asList("p2"); addOffer(new OfferRequest(7,"FLATX",20,seg1)); addOffer(new OfferRequest(7,"FLATX%",10,seg2)); Assert.assertEquals(180, applyOffer(200,1,7)); }
	@Test public void testCartValueZero() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(8,"FLATX",10,segments)); Assert.assertEquals(-10, applyOffer(0,1,8)); }
	@Test public void testHighCartValue() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(9,"FLATX%",10,segments)); Assert.assertEquals(900000, applyOffer(1000000,1,9)); }
	@Test public void testOfferForMultipleSegments() throws Exception { List<String> segments = Arrays.asList("p1","p2"); addOffer(new OfferRequest(10,"FLATX",20,segments)); Assert.assertEquals(180, applyOffer(200,1,10)); }
	@Test public void testDuplicateOfferAdded() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(11,"FLATX",10,segments)); addOffer(new OfferRequest(11,"FLATX",20,segments)); Assert.assertEquals(190, applyOffer(200,1,11)); }

	// Edge cases
	@Test public void testLargePercentageOffer() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(12,"FLATX%",150,segments)); Assert.assertEquals(-100, applyOffer(200,1,12)); }
	@Test public void testSpecialCharactersInSegmentName() throws Exception { List<String> segments = Arrays.asList("p1#"); addOffer(new OfferRequest(13,"FLATX",20,segments)); Assert.assertEquals(200, applyOffer(200,1,13)); }
	@Test public void testSmallCartValue() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(14,"FLATX",2,segments)); Assert.assertEquals(-1, applyOffer(1,1,14)); }
	@Test public void testRoundingPercentageOffer() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(15,"FLATX%",33,segments)); Assert.assertEquals(133, applyOffer(199,1,15)); }
	@Test public void testEmptySegmentList() throws Exception { List<String> segments = new ArrayList<>(); addOffer(new OfferRequest(16,"FLATX",10,segments)); Assert.assertEquals(200, applyOffer(200,1,16)); }

	// Boundary tests
	@Test public void testCartValueOne() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(17,"FLATX",1,segments)); Assert.assertEquals(0, applyOffer(1,1,17)); }
	@Test public void testApplyOfferWrongRestaurant() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(18,"FLATX",5,segments)); Assert.assertEquals(200, applyOffer(200,1,19)); }
	@Test public void testMultipleOffersSameSegment() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(20,"FLATX",5,segments)); addOffer(new OfferRequest(20,"FLATX%",10,segments)); Assert.assertEquals(195, applyOffer(200,1,20)); }
	@Test public void testNegativePercentageOffer() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(21,"FLATX%",-10,segments)); Assert.assertEquals(220, applyOffer(200,1,21)); }

	// Additional Advanced Tests
	@Test public void testMissingCartValue() throws Exception { Assert.assertEquals(-10, applyOffer(0,1,8)); }
	@Test public void testEmptySegmentFromServer() throws Exception { Assert.assertEquals(190, applyOffer(200,1,1)); }
	@Test public void testBadSegmentApiResponse() throws Exception { Assert.assertEquals(190, applyOffer(200,1,1)); }
	@Test public void testHugeCartValue() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(23,"FLATX%",10,segments)); Assert.assertTrue(applyOffer(Integer.MAX_VALUE,1,23) > 0); }
	@Test public void testNonExistingUserId() throws Exception { Assert.assertEquals(200, applyOffer(200,999,1)); }
	@Test public void testMultipleOffersPriority() throws Exception { List<String> segments = Arrays.asList("p1"); addOffer(new OfferRequest(24,"FLATX",5,segments)); addOffer(new OfferRequest(24,"FLATX",15,segments)); Assert.assertEquals(195, applyOffer(200,1,24)); }

}