package site.deiv70.springboot.healthcare.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.experimental.UtilityClass;
import site.deiv70.springboot.healthcare.infrastructure.in.ApiErrorException;

@UtilityClass
public final class Utils {

	public static String convertCamelCaseToSnakeCase(String camelCase) {
		return camelCase.replaceAll("([A-Z])", "_$1").toLowerCase();
	}

	public static String convertSnakeCaseToCamelCase(String snakeCase) {
		String[] words = snakeCase.split("_");
		StringBuilder camelCase = new StringBuilder();
		for (String word : words) {
			camelCase.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
		}
		return camelCase.toString();
	}

	/**
	 * Method for setting a Class (DTO) fields from a HashMap
	 * @param fieldValueHashMap HashMap with Class Field as KEY and Request Body Value as VALUE
	 * @param dtoClassObject Class (DTO) to set the fields
	 * @return Class (DTO) with the fields set
	 */
	public static <T> T setDTOObjectFromHashMap(Map<Field, Object> fieldValueHashMap, T dtoClassObject) {
		fieldValueHashMap.forEach((field, value) -> {
			try {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, dtoClassObject, value);
			} catch (Exception e) {
				throw new ApiErrorException("Error setting field: ".concat(field.getName())
					.concat(" with value: ").concat(value.toString()));
			}
		});

		return dtoClassObject;
	}

	/**
	 * Method for getting from a Request Body HashMap and a Class (DTO) a new mixed HashMap (fieldValueHashMap)
	 * with the Class Field as KEY and the Request Body Value as VALUE
	 * @param bodyHashMap Request Body HashMap
	 * @param clazz Class (DTO) to get the JsonProperty Value and Field
	 * @return HashMap with Class Field as KEY and Request Body Value as VALUE
	 */
	public static Map<Field, Object> getDTOClassFieldValueHashMap(Map<String, Object> bodyHashMap, Class<?> clazz) {
		Map<String, Field> jsonPropertyHashMap = getJsonProperyHashMap(clazz);
		return getFieldValueHashMap(bodyHashMap, jsonPropertyHashMap);
	}

	/**
	 * Method for getting from a Class (DTO) a HashMap with the JsonProperty Value as KEY
	 * and corresponding Class Field as VALUE
	 * @param clazz Class (DTO) to get the JsonProperty Value and Field
	 * @return HashMap with JsonProperty Value as KEY and corresponding Class Field as VALUE
	 */
	public static Map<String, Field> getJsonProperyHashMap(Class<?> clazz) {
		Map<String, Field> jsonPropertyHashMap = new HashMap<>();
		for (Field field : clazz.getDeclaredFields()) {
			String jsonPropertyValue = "";
			if (field.isAnnotationPresent(JsonProperty.class) && null != field.getAnnotation(JsonProperty.class).value()) {
				// Get the Value of JsonProperty annotation if present
				jsonPropertyValue = field.getAnnotation(JsonProperty.class).value();
			} else {
				// Or convert field name to snake_case if missing
				jsonPropertyValue = convertCamelCaseToSnakeCase(field.getName());
			}
			// Create the HashMap with JsonProperty Value as KEY and corresponding Field as VALUE
			jsonPropertyHashMap.put(jsonPropertyValue, field);
		}
		return jsonPropertyHashMap;
	}

	/**
	 * Method for getting from the Request Body HashMap and the JsonProperty HashMap
	 * a new mixed HashMap with the Class Field as KEY and the Request Body Value as VALUE
	 * @param bodyHashMap Request Body HashMap
	 * @param jsonPropertyHashMap JsonProperty HashMap
	 * @return HashMap with Class Field as KEY and Request Body Value as VALUE
	 */
	public static Map<Field, Object> getFieldValueHashMap(Map<String, Object> bodyHashMap, Map<String, Field> jsonPropertyHashMap) {
		Map<Field, Object> fieldValueHashMap = new HashMap<>();
		bodyHashMap.forEach((jsonKey, jsonValue) -> {
			if (null != jsonPropertyHashMap.get(jsonKey)) {
				Field field = jsonPropertyHashMap.get(jsonKey);
				fieldValueHashMap.put(field, jsonValue);
			} else {
				throw new ApiErrorException("Field not found: " + jsonKey);
			}
		});
		return fieldValueHashMap;
	}

	/**
	 * Method for getting from the fieldValueHashMap a List of Null Fields
	 * @param fieldValueHashMap HashMap with Class Field as KEY and Request Body Value as VALUE
	 * @return List of Null Fields
	 */
	public static List<String> getNullFieldList(Map<Field, Object> fieldValueHashMap) {
		List<String> nullFields = new ArrayList<>();
		fieldValueHashMap.forEach((field, value) -> {
			if (null == value) {
				nullFields.add(field.getName());
			}
		});
		return nullFields;
	}

	/**
	 * Method for setting to NULL the fields of an Object from a List of Null Fields
	 * @param object Object to set the fields
	 * @param nullFields List of Null Fields
	 * @return Object with the fields set to NULL
	 */
	public static <T> T setObjectFromNullFieldList(T object, List<String> nullFields) {
		nullFields.forEach(nullField -> {
			Field field = ReflectionUtils.findField(object.getClass(), nullField);
			if (null != field) {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, object, null);
			} else {
				throw new ApiErrorException("Field not found: " + nullField);
			}
		});
		return object;
	}

	public static <T> T setObjectFromNullFieldHashMap(T object, Map<String, String> nullFields) {
		nullFields.forEach((nullField, nullValue) -> {
			Field field = ReflectionUtils.findField(object.getClass(), nullField);
			if (null != field) {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, object, null);
			} else {
				throw new ApiErrorException("Field not found: " + nullField);
			}
		});
		return object;
	}

	/**
	 * Method for mapping a List of Null Fields with a HashMap
	 * @param nullFieldList List of Null Fields to map
	 * @param map HashMap with the current Field Name as KEY and the new Field Name as VALUE
	 * @return List of mapped Null Fields
	 */
	public static List<String> mapListWithHashMap(List<String> nullFieldList, Map<String, String> map) {
		List<String> mappedList = new ArrayList<>();
		// If nullFieldList is also present, replace it with the value from map. Otherwise, leave it as is.
		nullFieldList.forEach(nullField -> mappedList.add(map.getOrDefault(nullField, nullField)));
		return mappedList;
	}



}
