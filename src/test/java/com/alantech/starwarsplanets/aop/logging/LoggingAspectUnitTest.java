package com.alantech.starwarsplanets.aop.logging;

import com.alantech.starwarsplanets.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
class LoggingAspectUnitTest {
	@Mock
	Environment environment;

	@Test
	void Should_CorrectInitialize_When_ReceivesAEnvironment() {
		LoggingAspect loggingAspect = new LoggingAspect(environment);
		assertThat(loggingAspect).isNotNull();
		loggingAspect.applicationPackagePointcut();
		loggingAspect.springBeanPointcut();
	}

	@Test
	void Should_CreateLogger_When_ReceivesJoinPoint(@Mock JoinPoint joinPoint,  @Mock Signature signature) {
		LoggingAspect loggingAspect = new LoggingAspect(environment);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.getDeclaringTypeName()).thenReturn("signature name");
		assertThat(loggingAspect.logger(joinPoint)).isNotNull();
	}

	@Test
	void Should_logAfterThrowingLogError_When_ReceiveException(
		@Mock JoinPoint joinPoint, @Mock Signature signature, @Mock Logger logger
	) {
		when(joinPoint.getSignature()).thenReturn(signature);
		LoggingAspect loggingAspect = new LoggingAspect(environment, logger);
		when(signature.getName()).thenReturn("signature name");
		RuntimeException runtimeException = new RuntimeException("Error message");

		loggingAspect.logAfterThrowing(joinPoint, runtimeException);

		verify(joinPoint, atLeastOnce()).getSignature();
		verify(logger, atLeastOnce()).error(
			"Exception in {}() with cause = {}",
			"signature name",
			"NULL"
		);
	}

	@Test
	void Should_logAfterThrowingLogError_When_ReceiveExceptionAndItIsDevelopment(
		@Mock JoinPoint joinPoint, @Mock Signature signature, @Mock Environment env, @Mock Logger logger
	) {
		when(signature.getName()).thenReturn("signature name");
		when(joinPoint.getSignature()).thenReturn(signature);
		when(env.acceptsProfiles(Profiles.of(AppProperties.DEVELOPMENT_PROFILE))).thenReturn(true);
		LoggingAspect loggingAspect = new LoggingAspect(env, logger);
		RuntimeException runtimeException = new RuntimeException("Error message");

		loggingAspect.logAfterThrowing(joinPoint, runtimeException);

		verify(joinPoint, atLeastOnce()).getSignature();
		verify(logger, atLeastOnce()).error(
			"Exception in {}() with cause = '{}' and exception = '{}'",
			"signature name",
			"NULL",
			"Error message",
			runtimeException
		);

	}

	@Test
	void Should_logAround_When_IsDebugEnabled(
		@Mock ProceedingJoinPoint joinPoint, @Mock Signature signature, @Mock Environment env, @Mock Logger logger
	) throws Throwable {
		when(signature.getName()).thenReturn("signature name");
		when(joinPoint.getSignature()).thenReturn(signature);
		when(logger.isDebugEnabled()).thenReturn(true);
		LoggingAspect loggingAspect = new LoggingAspect(env, logger);

		loggingAspect.logAround(joinPoint);

		verify(logger, times(1)).debug(
			"Exit: {}() with result = {}",
			"signature name",
			null
		);
		verify(logger, times(1)).debug(
			"Enter: {}() with argument[s] = {}",
			"signature name",
			"null"
		);
	}

	@Test
	void Should_NotLogAround_When_IsDebugIsDisabled(
		@Mock ProceedingJoinPoint joinPoint, @Mock Environment env, @Mock Logger logger
	) throws Throwable {
		when(logger.isDebugEnabled()).thenReturn(false);
		LoggingAspect loggingAspect = new LoggingAspect(env, logger);

		loggingAspect.logAround(joinPoint);

		verify(logger, never()).debug(
			anyString(),
			anyString(),
			any()
		);
		verify(logger, never()).debug(
			anyString(),
			anyString(),
			anyString()
		);
	}

	@Test
	void Should_logAroundThrowException_When_proceedThrowsError(
		@Mock ProceedingJoinPoint joinPoint, @Mock Environment env, @Mock Signature signature, @Mock Logger logger
	) throws Throwable {
		when(joinPoint.proceed()).thenThrow(new IllegalArgumentException("Invalid argument"));
		when(signature.getName()).thenReturn("signature name");
		when(joinPoint.getSignature()).thenReturn(signature);
		LoggingAspect loggingAspect = new LoggingAspect(env, logger);

		Throwable exception = assertThrows(IllegalArgumentException.class, ()-> loggingAspect.logAround(joinPoint));

		assertThat(exception.getMessage()).isEqualTo("Invalid argument");
	}

}
