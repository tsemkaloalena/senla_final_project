package com.tsemkalo.senlaExperienceExchangeApp.dao.entities;

import com.tsemkalo.senlaExperienceExchangeApp.enums.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Permission extends AbstractEntity {
	@Enumerated(EnumType.STRING)
	@Column
	private PermissionType name;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
}
