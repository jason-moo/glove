package com.tf.util;

import com.tf.prop.HsDataSourceProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.*;

/**
 * 只用来校验其他数据源的配置
 *
 * @author guoqw
 * @since 2017-04-18 16:31
 */
public class HsDatasourcePropertiesValidator implements SmartValidator {

    private static final Set<String> INTERNAL_ANNOTATION_ATTRIBUTES = new HashSet<>(3);

    private static javax.validation.Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == HsDataSourceProperties.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        HsDataSourceProperties properties = (HsDataSourceProperties) target;

        if (!properties.getEnable()) {
            return;
        }
        if (properties.getMainDatasource() == null) {
            throw new IllegalArgumentException("主数据源配置不能为空");
        }
        if (CollectionUtils.isEmpty(properties.getOtherDataSources())) {
            return;
        }
        for (Map.Entry<String, HsDataSourceProperties.DruidDatasourceProperties> entry : properties
                .getOtherDataSources()
                .entrySet()) {
            String beanName = entry.getKey();
            if (StringUtils.isBlank(beanName)) {
                throw new IllegalArgumentException("数据源的beanName不能为空,请检查配置");
            }
            processConstraintViolations(beanName, validator.validate(entry.getValue()), errors);
        }
    }

    protected void processConstraintViolations(Set<ConstraintViolation<Object>> violations, Errors errors) {
        processConstraintViolations(null, violations, errors);
    }

    protected void processConstraintViolations(String beanName, Set<ConstraintViolation<Object>> violations, Errors errors) {
        for (ConstraintViolation<Object> violation : violations) {
            String field = determineField(violation);
            FieldError fieldError = errors.getFieldError(field);
            if (fieldError == null || !fieldError.isBindingFailure()) {
                try {
                    ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
                    String errorCode = determineErrorCode(cd);
                    Object[] errorArgs = getArgumentsForConstraint(errors.getObjectName(), field, cd);
                    if (errors instanceof BindingResult) {
                        // Can do custom FieldError registration with invalid value from ConstraintViolation,
                        // as necessary for Hibernate Validator compatibility (non-indexed set path in field)
                        BindingResult bindingResult = (BindingResult) errors;
                        String nestedField = bindingResult.getNestedPath() + field;
                        if ("".equals(nestedField)) {
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode);
                            bindingResult.addError(new ObjectError(
                                    errors.getObjectName(), errorCodes, errorArgs, violation.getMessage()));
                        } else {
                            Object rejectedValue = getRejectedValue(field, violation, bindingResult);
                            String[] errorCodes = bindingResult.resolveMessageCodes(errorCode, field);
                            String defaultMessage;
                            if (StringUtils.isNotBlank(beanName)) {
                                defaultMessage = "数据源[" + beanName + "]:" + violation.getMessage();
                            } else {
                                defaultMessage = violation.getMessage();
                            }
                            bindingResult.addError(new FieldError(
                                    errors.getObjectName(), nestedField, rejectedValue, false,
                                    errorCodes, errorArgs, defaultMessage));
                        }
                    } else {
                        // got no BindingResult - can only do standard rejectValue call
                        // with automatic extraction of the current field value
                        errors.rejectValue(field, errorCode, errorArgs, violation.getMessage());
                    }
                } catch (NotReadablePropertyException ex) {
                    throw new IllegalStateException("JSR-303 validated property '" + field +
                            "' does not have a corresponding accessor for Spring data binding - " +
                            "check your DataBinder's configuration (bean property versus direct field access)", ex);
                }
            }
        }
    }

    protected String determineField(ConstraintViolation<Object> violation) {
        return violation.getPropertyPath().toString();
    }

    protected String determineErrorCode(ConstraintDescriptor<?> descriptor) {
        return descriptor.getAnnotation().annotationType().getSimpleName();
    }

    protected Object[] getArgumentsForConstraint(String objectName, String field, ConstraintDescriptor<?> descriptor) {
        List<Object> arguments = new LinkedList<Object>();
        arguments.add(getResolvableField(objectName, field));
        // Using a TreeMap for alphabetical ordering of attribute names
        Map<String, Object> attributesToExpose = new TreeMap<String, Object>();
        for (Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            String attributeName = entry.getKey();
            Object attributeValue = entry.getValue();
            if (!INTERNAL_ANNOTATION_ATTRIBUTES.contains(attributeName)) {
                if (attributeValue instanceof String) {
                    attributeValue = new ResolvableAttribute(attributeValue.toString());
                }
                attributesToExpose.put(attributeName, attributeValue);
            }
        }
        arguments.addAll(attributesToExpose.values());
        return arguments.toArray(new Object[arguments.size()]);
    }

    protected MessageSourceResolvable getResolvableField(String objectName, String field) {
        String[] codes = new String[]{objectName + Errors.NESTED_PATH_SEPARATOR + field, field};
        return new DefaultMessageSourceResolvable(codes, field);
    }

    protected Object getRejectedValue(String field, ConstraintViolation<Object> violation, BindingResult bindingResult) {
        Object invalidValue = violation.getInvalidValue();
        boolean canGetValueDirect = !"".equals(field) && (invalidValue == violation.getLeafBean() ||
                (!field.contains("[]") && (field.contains("[") || field.contains("."))));
        if (canGetValueDirect) {
            // Possibly a bean constraint with property path: retrieve the actual property value.
            // However, explicitly avoid this for "address[]" style paths that we can't handle.
            invalidValue = bindingResult.getRawFieldValue(field);
        }
        return invalidValue;
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        if (validator != null) {
            Set<Class<?>> groups = new LinkedHashSet<Class<?>>();
            if (validationHints != null) {
                for (Object hint : validationHints) {
                    if (hint instanceof Class) {
                        groups.add((Class<?>) hint);
                    }
                }
            }
            processConstraintViolations(validator.validate(target,
                    groups.toArray(new Class<?>[groups.size()])), errors);
        }
    }

    private static class ResolvableAttribute implements MessageSourceResolvable, Serializable {

        private final String resolvableString;

        public ResolvableAttribute(String resolvableString) {
            this.resolvableString = resolvableString;
        }

        @Override
        public String[] getCodes() {
            return new String[]{this.resolvableString};
        }

        @Override
        public Object[] getArguments() {
            return null;
        }

        @Override
        public String getDefaultMessage() {
            return this.resolvableString;
        }
    }
}
