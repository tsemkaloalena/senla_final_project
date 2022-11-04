package com.tsemkalo.senlaExperienceExchangeApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto extends AbstractDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonSerialize(using = ToStringSerializer.class)
	private RoleType name;

	public RoleDto(Long id, RoleType name) {
		this.id = id;
		this.name = name;
	}
}
