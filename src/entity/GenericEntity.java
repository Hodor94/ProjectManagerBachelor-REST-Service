package entity;

/**
 * Created by Raphael on 14.06.2017.
 */

import javax.persistence.*;

// TODO : search for way to encrypt double and integer

@MappedSuperclass
public class GenericEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

}
