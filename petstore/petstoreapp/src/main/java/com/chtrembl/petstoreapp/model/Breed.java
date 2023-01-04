package com.chtrembl.petstoreapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Breed {
	    @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

	    private String name;

	    public Breed() {  }

	    public Breed(String name) {
	        this.setName(name);
	    }

	    public Breed(int id, String name) {
	        this.setId(id);
	        this.setName(name);
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	   
	    public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
	    public String toString() {
	        return "Breed{" +
	                "id=" + id +
	                ", name='" + name + '"' +
	                '}';
	    }
	}