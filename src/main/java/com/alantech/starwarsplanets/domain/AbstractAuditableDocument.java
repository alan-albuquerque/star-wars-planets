package com.alantech.starwarsplanets.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class AbstractAuditableDocument {
	@Id
	protected String id;

	@CreatedDate
	protected Date createdDate;

	@LastModifiedDate
	protected Date lastModifiedDate;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AbstractAuditableDocument)) {
			return false;
		}
		return id != null && id.equals(((AbstractAuditableDocument) o).id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
