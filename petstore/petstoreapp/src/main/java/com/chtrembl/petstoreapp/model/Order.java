package com.chtrembl.petstoreapp.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Order
 */
@SuppressWarnings("serial")
@Component
public class Order implements Serializable {
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("email")
	private String email = null;

	@JsonProperty("products")
	private List<Product> products = null;

	@JsonProperty("shipDate")
	private OffsetDateTime shipDate = null;

	/**
	 * Order Status
	 */
	public enum StatusEnum {
		PLACED("placed"),

		APPROVED("approved"),

		DELIVERED("delivered");

		private String value;

		StatusEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static StatusEnum fromValue(String text) {
			for (StatusEnum b : StatusEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	@JsonProperty("status")
	private StatusEnum status = null;

	@JsonProperty("complete")
	private Boolean complete = false;

	public Order id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Order products(List<Product> products) {
		this.products = products;
		return this;
	}

	public Order addProductsItem(Product productsItem) {
		if (this.products == null) {
			this.products = new ArrayList<Product>();
		}
		this.products.add(productsItem);
		return this;
	}

	/**
	 * Get products
	 * 
	 * @return products
	 **/
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Order shipDate(OffsetDateTime shipDate) {
		this.shipDate = shipDate;
		return this;
	}

	/**
	 * Get shipDate
	 * 
	 * @return shipDate
	 **/
	public OffsetDateTime getShipDate() {
		return shipDate;
	}

	public void setShipDate(OffsetDateTime shipDate) {
		this.shipDate = shipDate;
	}

	public Order status(StatusEnum status) {
		this.status = status;
		return this;
	}

	/**
	 * Order Status
	 * 
	 * @return status
	 **/
	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public Order complete(Boolean complete) {
		this.complete = complete;
		return this;
	}

	/**
	 * Get complete
	 * 
	 * @return complete
	 **/
	public Boolean isComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Order order = (Order) o;
		return Objects.equals(this.id, order.id) && Objects.equals(this.products, order.products)
				&& Objects.equals(this.shipDate, order.shipDate) && Objects.equals(this.status, order.status)
				&& Objects.equals(this.complete, order.complete);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, products, shipDate, status, complete);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Order {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    products: ").append(toIndentedString(products)).append("\n");
		sb.append("    shipDate: ").append(toIndentedString(shipDate)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
