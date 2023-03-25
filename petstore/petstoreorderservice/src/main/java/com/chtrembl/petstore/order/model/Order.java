package com.chtrembl.petstore.order.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.threeten.bp.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.annotations.ApiModelProperty;

/**
 * Order
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2021-12-23T15:16:30.446-05:00")

public class Order implements Serializable {
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("email")
	private String email = null;

	@JsonProperty("products")
	@Valid
	private List<Product> products = null;

	@JsonProperty("shipDate")
	private OffsetDateTime shipDate = null;

	@JsonProperty("tags")
	@Valid
	private List<Tag> tags = null;

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
	private Boolean complete = null;

	public Order id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@ApiModelProperty(value = "")

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
	@ApiModelProperty(value = "")

	@Valid

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
	@ApiModelProperty(value = "")

	@Valid

	public OffsetDateTime getShipDate() {
		return shipDate;
	}

	public void setShipDate(OffsetDateTime shipDate) {
		this.shipDate = shipDate;
	}

	public Order tags(List<Tag> tags) {
		this.tags = tags;
		return this;
	}

	public Order addTagsItem(Tag tagsItem) {
		if (this.tags == null) {
			this.tags = new ArrayList<Tag>();
		}
		this.tags.add(tagsItem);
		return this;
	}

	/**
	 * Get tags
	 * 
	 * @return tags
	 **/
	@ApiModelProperty(value = "")

	@Valid

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
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
	@ApiModelProperty(value = "Order Status")

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
	@ApiModelProperty(value = "")

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
				&& Objects.equals(this.shipDate, order.shipDate) && Objects.equals(this.tags, order.tags)
				&& Objects.equals(this.status, order.status) && Objects.equals(this.complete, order.complete);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, products, shipDate, tags, status, complete);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Order {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    products: ").append(toIndentedString(products)).append("\n");
		sb.append("    shipDate: ").append(toIndentedString(shipDate)).append("\n");
		sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
		sb.append("    email: ").append(toIndentedString(email)).append("\n");
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
