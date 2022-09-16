package com.tsemkalo.senlaExperienceExchangeApp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.tsemkalo.senlaExperienceExchangeApp.enums.PermissionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto extends AbstractDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonSerialize(using = ToStringSerializer.class)
	private PermissionType name;
	private Long roleId;

	public PermissionDto(Long id, PermissionType name, Long roleId) {
		this.id = id;
		this.name = name;
		this.roleId = roleId;
	}
}
