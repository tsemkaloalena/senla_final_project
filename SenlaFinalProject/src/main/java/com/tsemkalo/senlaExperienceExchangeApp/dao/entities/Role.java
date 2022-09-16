package com.tsemkalo.senlaExperienceExchangeApp.dao.entities;

import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
@Setter
public class Role extends AbstractEntity {
	@Enumerated(EnumType.STRING)
	@Column
	private RoleType name;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
	private List<Permission> permissions;

	public Role(RoleType name) {
		this.name = name;
	}
}
