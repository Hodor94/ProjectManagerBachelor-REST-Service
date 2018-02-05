package entity;

import javax.persistence.*;

/**
 * The Superclass for all entities. It offers a unique automatically
 * generated identifier for the entries of the database.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
@MappedSuperclass
public class GenericEntity {
	protected static final String DATE_FORMAT = "dd.MM.yyyy hh:mm:ss";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	/**
	 * Sets the value of the identifier.
	 *
	 * @param id - The new value of the identifier.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Returns the identifier of an entity.
	 *
	 * @return The identifier of an entity.
	 */
	public long getId() {
		return id;
	}

}
