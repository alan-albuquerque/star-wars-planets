package com.alantech.starwarsplanets.domain;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AbstractAuditableDocument extends AbstractDocument {
	@CreatedDate
	private Date createdDate;

	@LastModifiedDate
	private Date lastModifiedDate;
}
