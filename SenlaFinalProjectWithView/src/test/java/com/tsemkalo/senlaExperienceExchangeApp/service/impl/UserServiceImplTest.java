package com.tsemkalo.senlaExperienceExchangeApp.service.impl;

import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.Role;
import com.tsemkalo.senlaExperienceExchangeApp.dao.entities.User;
import com.tsemkalo.senlaExperienceExchangeApp.enums.RoleType;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.IncorrectDataException;
import com.tsemkalo.senlaExperienceExchangeApp.exceptions.UserExistsException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest extends AbstractServiceTest {
	@InjectMocks
	private UserServiceImpl userService = new UserServiceImpl();

	@Test
	@Order(1)
	public void loadUserByUsername_whenUsernameExists_thenUSerReturned() {
		String username = "sashenka";

		assertEquals(username, userService.loadUserByUsername(username).getUsername());
	}

	@Test
	@Order(2)
	public void loadUserByUsername_whenUsernameDoesNotExist_thenUsernameNotFoundExceptionThrown() {
		String username = "mysterious";

		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
	}

	@Test
	@Order(3)
	public void saveUser_whenAllDataIsCorrect_thenSuccess() {
		String username = "antoha";
		String password = "in_the_hood";
		String name = "Anatoliy";
		String surname = "English";
		String email = "tsemkaloalena@gmail.com";
		Role role = new Role(RoleType.TEACHER);
		User newUser = new User(username, password, name, surname, role, email);

		userService.saveUser(newUser);

		assertNotEquals(newUser.getPassword(), password);
		assertTrue(getBCryptPasswordEncoder().matches(password, newUser.getPassword()));
		assertNotNull(newUser.getId());
		assertEquals(getUserTable().get(newUser.getId()), newUser);
	}

	@Test
	@Order(4)
	public void saveUser_whenUsernameExists_thenUserExistsExceptionThrown() {
		String username = "antoha";
		String password = "otherPassword";
		String name = "otherName";
		String surname = "otherSurname";
		String email = "tsemkaloalena@gmail.com";
		Role role = new Role(RoleType.STUDENT);
		User newUser = new User(username, password, name, surname, role, email);

		assertThrows(UserExistsException.class, () -> userService.saveUser(newUser));
	}

	@Test
	@Order(5)
	public void changePassword_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "marik";
		String oldPassword = "who";
		String newPassword = "theWho";
		User user = getUserTable().get(10L);

		userService.changePassword(currentUsername, oldPassword, newPassword);

		assertNotEquals(newPassword, user.getPassword());
		assertTrue(getBCryptPasswordEncoder().matches(newPassword, user.getPassword()));
		assertFalse(getBCryptPasswordEncoder().matches(oldPassword, user.getPassword()));
	}

	@Test
	@Order(6)
	public void changePassword_whenOldPasswordIsNotCorrectAndBelongsToOtherUser_thenAccessDeniedExceptionThrown() {
		String currentUsername = "marik";
		String oldPassword = "male";
		String newPassword = "newPswd";

		assertThrows(AccessDeniedException.class, () -> userService.changePassword(currentUsername, oldPassword, newPassword));
	}

	@Test
	@Order(7)
	public void changePassword_whenOldPasswordIsNotCorrect_thenAccessDeniedExceptionThrown() {
		String currentUsername = "marik";
		String oldPassword = "somePswd";
		String newPassword = "newPswd";

		assertThrows(AccessDeniedException.class, () -> userService.changePassword(currentUsername, oldPassword, newPassword));
	}

	@Test
	@Order(8)
	public void changeUsername_whenAllDataIsCorrect_thenSuccess() {
		String currentUsername = "sashenka";
		String newUsername = "sahar";
		String password = "sladkiy";

		userService.changeUsername(currentUsername, newUsername, password);

		assertNotEquals(getUserTable().get(13L).getUsername(), currentUsername);
		assertEquals(getUserTable().get(13L).getUsername(), newUsername);
	}

	@Test
	@Order(9)
	public void changeUsername_whenUsernameIsNotChanged_thenSuccess() {
		String currentUsername = "sahar";
		String newUsername = "sahar";
		String password = "sladkiy";

		userService.changeUsername(currentUsername, newUsername, password);

		assertEquals(getUserTable().get(13L).getUsername(), currentUsername);
		assertEquals(getUserTable().get(13L).getUsername(), newUsername);
	}

	@Test
	@Order(10)
	public void changeUsername_whenUsernameExists_thenUserExistsExceptionThrown() {
		String currentUsername = "sahar";
		String newUsername = "marik";
		String password = "sladkiy";

		assertThrows(UserExistsException.class, () -> userService.changeUsername(currentUsername, newUsername, password));
	}

	@Test
	@Order(11)
	public void changeUsername_whenPasswordIsWrong_thenAccessDeniedExceptionThrown() {
		String currentUsername = "sahar";
		String newUsername = "danya";
		String password = "saharok";
		String email = "tsemkaloalena@gmail.com";

		assertThrows(AccessDeniedException.class, () -> userService.changeUsername(currentUsername, newUsername, password));
	}

	@Test
	@Order(12)
	public void editInfo_whenNoChangesAreSet_thenIncorrectDataExceptionThrown() {
		String currentUsername = "marik";
		User editedUser = new User();

		assertThrows(IncorrectDataException.class, () -> userService.editInfo(currentUsername, editedUser));
	}

	@Test
	@Order(13)
	public void editInfo_whenNewNameIsSet_thenNameChanged() {
		String currentUsername = "marik";
		String oldName = getUserTable().get(10L).getName();
		String oldSurname = getUserTable().get(10L).getSurname();
		User editedUser = new User();
		editedUser.setName("Yarik");

		userService.editInfo(currentUsername, editedUser);

		assertEquals("Yarik", getUserTable().get(10L).getName());
		assertNotNull(getUserTable().get(10L).getSurname());
		assertNotEquals(oldName, getUserTable().get(10L).getName());
		assertEquals(oldSurname, getUserTable().get(10L).getSurname());
	}

	@Test
	@Order(14)
	public void editInfo_whenNewSurnameIsSet_thenNameChanged() {
		String currentUsername = "marik";
		User editedUser = new User();
		editedUser.setSurname("VseResheno");
		String oldName = getUserTable().get(10L).getName();
		String oldSurname = getUserTable().get(10L).getSurname();

		userService.editInfo(currentUsername, editedUser);

		assertEquals("VseResheno", getUserTable().get(10L).getSurname());
		assertNotNull(getUserTable().get(10L).getName());
		assertEquals(oldName, getUserTable().get(10L).getName());
		assertNotEquals(oldSurname, getUserTable().get(10L).getSurname());
	}

	@Test
	@Order(15)
	public void editInfo_whenNewNameAndSurnameAreSet_thenNameAndSurnameChanged() {
		String currentUsername = "marik";
		User editedUser = new User();
		editedUser.setName("Valentin");
		editedUser.setSurname("Strikalo");
		String oldName = getUserTable().get(10L).getName();
		String oldSurname = getUserTable().get(10L).getSurname();

		userService.editInfo(currentUsername, editedUser);

		assertEquals("Valentin", getUserTable().get(10L).getName());
		assertEquals("Strikalo", getUserTable().get(10L).getSurname());
		assertNotEquals(oldName, getUserTable().get(10L).getName());
		assertNotEquals(oldSurname, getUserTable().get(10L).getSurname());
	}
}
