package com.alantech.starwarsplanets.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@Document(collection = "planets")
public class Planet extends AbstractAuditableDocument {
	@Indexed(unique = true)
	private String name;
	private String climate;
	private String terrain;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Planet)) {
			return false;
		}
		return id != null && id.equals(((Planet) o).id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
