package com.alantech.starwarsplanets.common;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class MapperTestUtil {

	private static final ObjectMapper mapper = createObjectMapper();

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.registerModule(new JavaTimeModule());
		return mapper;
	}

	/**
	 * Convert an object to JSON string.
	 *
	 * @param object the object to convert.
	 * @return the JSON string.
	 * @throws IOException if it receives a invalid object.
	 */
	public static String toJson(Object object) throws IOException {
		return mapper.writeValueAsString(object);
	}

	/**
	 * Convert a JSON string to a list of objects.
	 *
	 * @param jsonInput the JSON string.
	 * @return the converted objects list.
	 * @throws IOException if it receives a invalid JSON string.
	 */
	public static <T> List<T> toObjectList(String jsonInput, Class<T> myClass) throws IOException {
		return mapper.readValue(jsonInput, mapper.getTypeFactory().constructCollectionType(List.class, myClass));
	}

	/**
	 * Convert a JSON string to a object.
	 *
	 * @param jsonInput the JSON string.
	 * @return the converted object.
	 * @throws IOException if it receives a invalid JSON string.
	 */
	public static <T> T toObject(String jsonInput, Class<T> myClass) throws IOException {
		return mapper.readValue(jsonInput, mapper.getTypeFactory().constructType(myClass));
	}
}
