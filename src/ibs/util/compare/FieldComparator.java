package ibs.util.compare;

import java.text.DateFormat;
import java.util.Date;

public abstract class FieldComparator {
	private final DateFormat dateFormat;
	private StringBuffer result;

	protected FieldComparator(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public final String compare(Object _old, Object _new) {
		result = new StringBuffer();
		compareBeans(_old, _new);
		return result.toString();
	}
	
	protected abstract void compareBeans(Object _old, Object _new);

	protected void compareField(String fieldName, long oldValue, long newValue) {
		compareField(fieldName, format(oldValue), format(newValue));
	}

	protected void compareField(String fieldName, Date oldValue, Date newValue) {
		compareField(fieldName, format(oldValue), format(newValue));
	}

	protected void compareField(String fieldName, String oldValue, String newValue) {
		compareField(fieldName, format(oldValue), format(newValue));
	}

	protected void compareField(String fieldName, Object oldValue, Object newValue) {
		if(null == oldValue && null != newValue) {
			changePrefix(fieldName).append("добавлено значение \"").append(newValue).append('"');
		} else if (null == newValue && null != oldValue) {
			changePrefix(fieldName).append("удалено значение \"").append(oldValue).append('"');
		} else if (null != oldValue && null != newValue && !oldValue.equals(newValue)) {
			changePrefix(fieldName).append("изменилось значение с \"")
				.append(oldValue).append("\" на \"").append(newValue).append('"');
		}
	}

	private StringBuffer changePrefix(String fieldName) {
		if(result.length() > 0) {
			result.append(", ");
		}
		return result.append('"').append(fieldName).append("\" - "); 
	}

	protected Object format(Date value) {
		return null == value ? null : dateFormat.format(value); 
	}

	protected Object format(String value) {
		return null != value && 0 == value.length() ? null : value; 
	}
	
	protected Object format(long value) {
		return new Long(value); 
	}
}
