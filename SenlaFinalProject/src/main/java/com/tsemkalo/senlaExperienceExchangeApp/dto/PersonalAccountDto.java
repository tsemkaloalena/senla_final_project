package com.tsemkalo.senlaExperienceExchangeApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalAccountDto extends AbstractDto {
	private String username;
	private String name;
	private String surname;
	private String role;
	private String email;

	public PersonalAccountDto(Long id, String username, String name, String surname, String role, String email) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.surname = surname;
		this.role = role;
		this.email = email;
	}
}
