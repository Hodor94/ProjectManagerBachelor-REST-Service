package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "register")
public class RegisterEntity extends GenericEntity {

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

	public RegisterEntity() {
		super();
		users = new ArrayList<UserEntity>();
		color = DEFAULt_COLOR;
	}

	public RegisterEntity(String name, ArrayList<UserEntity> users,
						  TeamEntity team, String color) {
		super();
		this.name = name;
		this.users = users;
		this.team = team;
		this.color = color;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<UserEntity> users) {
		this.users = users;
	}

	public RegisterEntity(String name, TeamEntity team) {
		this.name = name;
		this.team = team;
		this.users = new ArrayList<UserEntity>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TeamEntity getTeam() {
		return team;
	}

	public void setTeam(TeamEntity team) {
		this.team = team;
	}

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

	private String appendJSONColor(String color) {
		if (color != null && !(color.equals(""))) {
			return "\"color\": \"" + color + "\"";
		} else {
			return "\"color\": " + null + "";
		}
	}

	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + name + "\", ";
		} else {
			return "\"name\": " + null + ", ";
		}
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}

