# Trendy Threads E-Commerce API

## Description of the Project

Trendy Threads is a Spring Boot e-commerce backend API for a clothing store. This project was built as a capstone where the frontend store already existed, and the main responsibility was to complete, debug, and extend the backend API.

The API allows users to browse product categories, search and filter products, manage a shopping cart, and check out by converting their cart into an order. The project uses a layered Spring Boot architecture with controllers, services, repositories, JPA models, and a MySQL database.

A major focus of this project was working like a backend developer on an existing codebase: reading starter code, fixing bugs, completing unfinished features, testing endpoints in Insomnia, and making sure the API returns the correct data for the frontend.

---

## User Stories

### Categories

* As a shopper, I want to view all categories so that I can browse products by section.
* As a shopper, I want to view a category by ID so that I can see details about a specific category.
* As a shopper, I want to view products by category so that I can browse related clothing items.
* As an admin, I want to add new categories so that the store can support new product groups.
* As an admin, I want to update categories so that category information stays accurate.
* As an admin, I want to delete categories so that outdated categories can be removed.

### Products

* As a shopper, I want to search and filter products so that I can quickly find clothing items that match what I am looking for.
* As a shopper, I want product filters to return accurate results so that I do not miss products that exist in the store.
* As an admin, I want to update product details so that prices, descriptions, images, and stock stay current.
* As an admin, I want stock updates to save correctly so that inventory remains accurate.

### Shopping Cart

* As a logged-in user, I want to view my shopping cart so that I can review what I plan to buy.
* As a logged-in user, I want to add products to my cart so that I can purchase them later.
* As a logged-in user, I want adding the same product again to increase the quantity so that duplicate cart rows are not created.
* As a logged-in user, I want to update item quantities so that I can control how many of each product I buy.
* As a logged-in user, I want to clear my cart so that I can remove all items at once.

### Checkout

* As a logged-in user, I want to check out my cart so that my selected products become an order.
* As a logged-in user, I want an order line item created for every cart item so that my order accurately records what I bought.
* As a logged-in user, I should not be able to check out with an empty cart so that invalid orders are not created.

---

## Features Implemented

### Phase 1: Categories Controller

Completed the `CategoriesController` by adding REST annotations, endpoint mappings, service calls, and admin-only security for create, update, and delete actions.

Implemented endpoints:

```text
GET    /categories
GET    /categories/{id}
GET    /categories/{categoryId}/products
POST   /categories
PUT    /categories/{id}
DELETE /categories/{id}
```

Admin-only actions:

```text
POST   /categories
PUT    /categories/{id}
DELETE /categories/{id}
```

---

### Phase 2: Product Bug Fixes

Fixed product search/filter behavior so that `GET /products` returns the full product list instead of only featured products.

The issue was caused by this filter:

```java
.filter(Product::isFeatured)
```

Removing that line allowed all products to appear when no filter is selected.

Also fixed the product update bug where stock changes were not saving. The update method was missing:

```java
existing.setStock(product.getStock());
```

After adding this line, product stock updates persisted correctly in the database.

---

### Phase 3: Shopping Cart

Implemented shopping cart functionality for logged-in users.

Implemented endpoints:

```text
GET    /cart
POST   /cart/products/{productId}
PUT    /cart/products/{productId}
DELETE /cart
```

Cart behavior:

* Gets the current user from the authenticated `Principal`
* Looks up the user's database ID
* Loads all cart rows for that user
* Looks up full product details for each cart item
* Builds a frontend-friendly `ShoppingCart` response
* Calculates item line totals and cart total
* Increases quantity when the same product is added again
* Clears all cart items for the current user

---

### Phase 5: Checkout / Orders

Implemented checkout functionality that converts the current user's cart into an order.

Implemented endpoint:

```text
POST /orders
```

Checkout behavior:

* Requires the user to be logged in
* Loads the current user's shopping cart
* Prevents checkout if the cart is empty
* Creates a new row in the `orders` table
* Creates one row in `order_line_items` for each cart item
* Clears the user's cart after checkout
* Returns the created order with status `201 Created`

---

## Interesting Code Highlight

One of the most interesting parts of the project was building the `ShoppingCart` response from database cart rows.

The `shopping_cart` table only stores simple information: `userId`, `productId`, and `quantity`. However, the frontend needs more detailed data, including the full product object, quantity, line total, and cart total.

The service layer solves this by loading the cart rows, looking up each product, and building a response model:

```java
public ShoppingCart getByUserId(int userId)
{
    ShoppingCart cart = new ShoppingCart();

    List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

    for (CartItem cartItem : cartItems)
    {
        Product product = productService.getById(cartItem.getProductId());

        if (product != null)
        {
            ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
            shoppingCartItem.setProduct(product);
            shoppingCartItem.setQuantity(cartItem.getQuantity());

            cart.add(shoppingCartItem);
        }
    }

    return cart;
}
```

This is a good example of why the service layer is important. The repository retrieves raw database records, while the service combines multiple pieces of data and shapes the response for the frontend.

---

## What I Struggled With

One challenge was understanding how the different layers worked together in an existing Spring Boot project. The controller receives the request, the service handles business logic, the repository communicates with the database, and the model represents the data.

Another challenge was debugging issues where the API returned a successful status code, but the database did not actually change as expected. For example, the product update endpoint returned OK, but the stock field was not being saved. This taught me to verify changes by checking the response, rerunning GET requests, and confirming the database values.

I also had to get more comfortable with authentication. The cart and checkout features are user-specific, so I had to use the logged-in user's `Principal`, find that user in the database, and make sure the correct user's cart and orders were being modified.

---

## Setup

### Prerequisites

* Java 17
* IntelliJ IDEA
* MySQL Workbench
* MySQL Server
* Insomnia or Postman
* Git

---

### Database Setup

1. Open MySQL Workbench.
2. Locate the database script for the clothing store.
3. Run the script to create the database and seed data.
4. Confirm the database includes tables such as:

```text
users
profiles
categories
products
shopping_cart
orders
order_line_items
```

The starter database includes demo users. The default password for demo users is:

```text
password
```

---

### Running the Backend API in IntelliJ

1. Open IntelliJ IDEA.
2. Select **Open** and choose the API project folder.
3. Wait for Maven to load dependencies.
4. Check `application.properties` and confirm the database connection settings match your local MySQL setup.
5. Run the Spring Boot application.
6. Confirm the API starts on:

```text
http://localhost:8080
```

---

### Testing with Insomnia

Use Insomnia to test the API endpoints.

Common test flow:

1. Register or log in.
2. Copy the JWT token from the login response.
3. Add the token to protected requests.
4. Test category endpoints.
5. Test product search and update behavior.
6. Add products to the cart.
7. Check out by creating an order.

Example login endpoint:

```text
POST http://localhost:8080/login
```

Example cart endpoint:

```text
GET http://localhost:8080/cart
```

Example checkout endpoint:

```text
POST http://localhost:8080/orders
```

---

## Technologies Used

* Java 17
* Spring Boot
* Spring Web
* Spring Security
* JWT Authentication
* Spring Data JPA
* Hibernate
* MySQL
* Maven
* Insomnia
* Git and GitHub
* IntelliJ IDEA

---

## API Endpoints

### Authentication

```text
POST /register
POST /login
```

### Categories

```text
GET    /categories
GET    /categories/{id}
GET    /categories/{categoryId}/products
POST   /categories
PUT    /categories/{id}
DELETE /categories/{id}
```

### Products

```text
GET    /products
GET    /products/{id}
POST   /products
PUT    /products/{id}
DELETE /products/{id}
```

Product search supports filters such as:

```text
/products?cat=1
/products?minPrice=25
/products?maxPrice=100
/products?minPrice=25&maxPrice=100
/products?cat=1&subCategory=Jackets
```

### Shopping Cart

```text
GET    /cart
POST   /cart/products/{productId}
PUT    /cart/products/{productId}
DELETE /cart
```

### Orders

```text
POST /orders
```

---

## Future Improvements

* Add user profile-based shipping information during checkout
* Add order history so users can view previous purchases
* Add product image upload support
* Add pagination for product search results
* Add sorting by price, name, or newest products
* Add inventory checks before checkout
* Add admin dashboard features
* Add more complete automated unit and integration tests
* Improve frontend styling and product display
* Add payment processing simulation

---

## Team Members

* **Christopher Devalme** — Backend developer responsible for completing API endpoints, fixing product bugs, implementing shopping cart functionality, creating checkout/order logic, testing with Insomnia, and connecting backend behavior to the existing frontend.

---

## Thanks

* Thank you to Raymound for guidance throughout the capstone.
* Thank you to classmates and peers for feedback, debugging help, and support during development.
