package com.tsemkalo.senlaExperienceExchangeApp.dao.entities;

import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User extends AbstractEntity implements UserDetails {
	/**
	 * Field for holding a unique username for authorization
	 */
	@Column
	private String username;

	/**
	 * Field for holding a password for authorization
	 */
	@Column
	private String password;

	@Column
	private String name;

	@Column
	private String surname;

	@OneToOne
	@JoinColumn(name = "role_id")
	private Role role;

	/**
	 * Used only if the role is a student. List for holding lessons to which the user is subscribed
	 */
	@OneToMany(mappedBy = "user")
	private List<Subscription> subscriptions;

	/**
	 * List for holding all reviews written by this user
	 */
	@OneToMany(mappedBy = "user")
	private List<Review> reviews;

	@Column
	private String email;

	public User(String username, String password, String name, String surname, Role role, String email) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.role = role;
		this.email = email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Permission permission : getRole().getPermissions()) {
			authorities.add(new SimpleGrantedAuthority(permission.getName().name()));
		}
		return authorities;
	}

	public void setEmail(String email) {
		if (!email.contains("@") || !email.contains(".") || email.indexOf("@") > email.lastIndexOf(".")) {
			throw new IncorrectDataException(email + " is not correct email");
		}
		this.email = email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
