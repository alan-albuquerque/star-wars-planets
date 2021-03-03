package com.alantech.starwarsplanets.domain;

import com.alantech.starwarsplanets.common.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractAuditableDocumentUnitTest {
	@Test
	void Should_ValidateEqualsVerifier_When_Valid() throws Exception {
		TestUtil.equalsVerifier(AbstractAuditableDocument.class);
		AbstractAuditableDocument document = AbstractAuditableDocument.builder().id("1").build();
		AbstractAuditableDocument document1 = AbstractAuditableDocument.builder().id("1").build();
		AbstractAuditableDocument document2 = AbstractAuditableDocument.builder().id("2").build();
		assertThat(document).isEqualTo(document1).isNotEqualTo(document2);
		assertThat(document1).isNotEqualTo(document2);
	}
}
