package entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used by the framework Hibernate to work with the database
 * and represents a group in the system,
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "register")
public class RegisterEntity extends GenericEntity {

	// The default color of the register.
	private static final String DEFAULt_COLOR = "#ffffff";

	// Attributes without any relations to other entities
	@Column(name = "name")
	private String name;

	@Column(name = "color")
	private String color;

	// Attributes related to other entities
	@JsonIgnore
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy = "register", targetEntity = UserEntity.class)
	private List<UserEntity> users;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "team")
	private TeamEntity team;

	/**
	 * Creates a RegisterEntity object with all the attributes having the
	 * default values.
	 */
	public RegisterEntity() {
		super();
		users = new ArrayList<UserEntity>();
		color = DEFAULt_COLOR;
	}

	/**
	 * Creates a RegisterEntity object with the attributes getting the
	 * parameter as values.
	 *
	 * @param name The name of the register.
	 * @param users The users being grouped in this register.
	 * @param team The team the register belongs to.
	 * @param color The color of the register.
	 */
	public RegisterEntity(String name, ArrayList<UserEntity> users,
						  TeamEntity team, String color) {
		super();
		this.name = name;
		this.users = users;
		this.team = team;
		this.color = color;
	}

	/**
	 * Returns a {@see List} of the users in this register.
	 *
	 * @return The users being grouped in this register.
	 */
	public List<UserEntity> getUsers() {
		return users;
	}

	/**
	 * Sets the users taking part in this register.
	 *
	 * @param users The users taking part in this register.
	 */
	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	/**
	 * Creates a new RegisterEntity object with a name and a team but default
	 * color and no users added.
	 *
	 * @param name The name of the register.
	 * @param team The team of the register.
	 */
	public RegisterEntity(String name, TeamEntity team) {
		this.name = name;
		this.team = team;
		this.users = new ArrayList<UserEntity>();
	}

	/**
	 * Returns the name of the register.
	 *
	 * @return The name of the register.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the register.
	 *
	 * @param name The new name of this register.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the {@see TeamEntitiy} the register belongs to.
	 *
	 * @return The team of this register.
	 */
	public TeamEntity getTeam() {
		return team;
	}

	/**
	 * Sets the {@see TeamEntity} this register belongs to.
	 *
	 * @param team The new team of this register.
	 */
	public void setTeam(TeamEntity team) {
		this.team = team;
	}

	/**
	 * Transforms this register into a JSON format String.
	 *
	 * @return A JSON format of this register.
	 */
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		String result;
		stringBuilder.append("{");
		stringBuilder.append("\"id\": " + "\"" + this.getId() + "\",");
		stringBuilder.append(appendJSONName(name));
		stringBuilder.append(appendJSONColor(color));
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	/*
	Returns the color of this register as a JSON attribute.
	 */
	private String appendJSONColor(String color) {
		if (color != null && !(color.equals(""))) {
			return "\"color\": \"" + color + "\"";
		} else {
			return "\"color\": " + null + "";
		}
	}

	/*
	Returns the name of this register as a JSON attribute.
	 */
	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + name + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	/**
	 * Returns the color of this register.
	 *
	 * @return The color of this register.
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the color of this register.
	 *
	 * @param color The new color of this register.
	 */
	public void setColor(String color) {
		this.color = color;
	}
}

