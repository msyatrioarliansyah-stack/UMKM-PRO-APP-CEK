package com.example.ui.validation

sealed class ValidationResult<out T> {
    data class Success<out T>(val value: T) : ValidationResult<T>()
    data class Invalid(val errors: Map<String, String>) : ValidationResult<Nothing>() {
        fun getError(field: String): String? = errors[field]
    }
}

class ZodString {
    private val rules = mutableListOf<(String, Map<String, Any?>, (String) -> Unit) -> Unit>()

    fun nonEmpty(message: String): ZodString {
        rules.add { value, _, onError ->
            if (value.trim().isEmpty()) {
                onError(message)
            }
        }
        return this
    }

    fun min(length: Int, message: String): ZodString {
        rules.add { value, _, onError ->
            if (value.length < length) {
                onError(message)
            }
        }
        return this
    }

    fun max(length: Int, message: String): ZodString {
        rules.add { value, _, onError ->
            if (value.length > length) {
                onError(message)
            }
        }
        return this
    }

    fun isDouble(message: String): ZodString {
        rules.add { value, _, onError ->
            if (value.isNotBlank() && value.toDoubleOrNull() == null) {
                onError(message)
            }
        }
        return this
    }

    fun positiveDouble(message: String): ZodString {
        rules.add { value, _, onError ->
            val num = value.toDoubleOrNull()
            if (value.isNotBlank() && (num == null || num <= 0)) {
                onError(message)
            }
        }
        return this
    }

    fun custom(message: String, predicate: (String, Map<String, Any?>) -> Boolean): ZodString {
        rules.add { value, context, onError ->
            if (!predicate(value, context)) {
                onError(message)
            }
        }
        return this
    }

    fun validate(value: String, context: Map<String, Any?>, onFieldError: (String) -> Unit) {
        for (rule in rules) {
            var errorReported = false
            rule(value, context) { errMsg ->
                onFieldError(errMsg)
                errorReported = true
            }
            if (errorReported) {
                break // Stop on first error for this field
            }
        }
    }
}

class ZodObjectSchema {
    private val fieldValidators = mutableMapOf<String, ZodString>()

    fun field(name: String, builder: ZodString.() -> Unit) {
        val zodString = ZodString().apply(builder)
        fieldValidators[name] = zodString
    }

    fun validate(data: Map<String, String>, context: Map<String, Any?> = emptyMap()): ValidationResult<Map<String, String>> {
        val errors = mutableMapOf<String, String>()
        for ((fieldName, validator) in fieldValidators) {
            val value = data[fieldName] ?: ""
            validator.validate(value, context) { errMsg ->
                errors[fieldName] = errMsg
            }
        }
        return if (errors.isEmpty()) {
            ValidationResult.Success(data)
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}

object Zod {
    fun objectSchema(builder: ZodObjectSchema.() -> Unit): ZodObjectSchema {
        return ZodObjectSchema().apply(builder)
    }
}
