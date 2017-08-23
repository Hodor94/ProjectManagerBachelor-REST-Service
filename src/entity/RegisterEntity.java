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

	// Attributes without any relations to other entities
	@ColumnTransformer(read = "AES_DECRYPT(name, 'DataService.secretKey')",
					   write = "AES_ENCRYPT(?, 'DataService.secretKey')")
	@Column(name = "name")
	private String name;

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
	}

	public RegisterEntity(String name, ArrayList<UserEntity> users,
						  TeamEntity team) {
		super();
		this.name = name;
		this.users = users;
		this.team = team;
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
		stringBuilder.append("}");
		result = stringBuilder.toString();
		return result;
	}

	private String appendJSONName(String name) {
		if (name != null && !(name.equals(""))) {
			return "\"name\": " + "\"" + encodeToUTF8(name) + "\"";
		} else {
			return "\"name\": " + null;
		}
	}

}

