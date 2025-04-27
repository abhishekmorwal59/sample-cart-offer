# sample-cart-offer

# Cart Offer API Automation

---

## Project Overview
This project is an API Automation Test Suite for testing the Cart Offer functionality of a food ordering platform (similar to Zomato).

Customers belong to different segments (p1, p2, p3) and offers are applied accordingly:
- **FLATX**: Flat amount off (e.g., Rs 10 off)
- **FLATX%**: Percentage off (e.g., 10% off)

The framework validates offer application based on:
- User Segment
- Offer Type
- Cart Value
- Discount logic

---

## Technologies Used
- Java 11
- Spring Boot
- JUnit 4
- MockServer (for mocking external dependencies)
- REST-Assured (for API testing)
- Maven (Build tool)

---

## How to Setup & Run

1. **Start Spring Boot Application**
   ```bash
   mvn spring-boot:run
   ```
   App should run on `localhost:9001`

2. **Start MockServer**
   MockServer is automatically started in tests (`localhost:1080`).

3. **Run the Test Suite**
   - From IDE: Right-click `CartOfferApplicationTests` and "Run All Tests"
   - From Command Line:
   ```bash
   mvn clean test
   ```

---

## How MockServer Works
- Mocks the `/api/v1/user_segment` endpoint
- Based on `user_id`, returns appropriate `segment`
- MockServer listens at `localhost:1080`
- No real external dependencies needed

---

## How API Automation Works
- **Add Offer API**: `POST /api/v1/offer`
- **Apply Offer API**: `POST /api/v1/cart/apply_offer`

Each test:
- Creates required offer using `/offer`
- Applies offer on cart using `/cart/apply_offer`
- Asserts cart value or error responses

Reusable Helper Methods:
- `addOffer(OfferRequest offerRequest)`
- `applyOffer(int cartValue, int userId, int restaurantId)`

---

## Test Coverage
- Positive scenarios (Flat X, Flat X%)
- Negative scenarios (Invalid User, Invalid Segment, Invalid Discount)
- Boundary Conditions (Cart value 0, High Cart value)
- Edge Cases (Large Discount %, Negative Cart Value)
- Concurrency tests

> Total Test Cases: 30+

---

## Important URLs
- Base App URL: `http://localhost:9001`
- MockServer URL: `http://localhost:1080`

---

## How to Add New Test Cases
1. Add a new `@Test` method inside `CartOfferApplicationTests.java`
2. Use `addOffer()` to create offer
3. Use `applyOffer()` to apply on cart
4. Add appropriate `Assert` to validate result

---
