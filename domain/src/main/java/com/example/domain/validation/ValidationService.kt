package com.example.domain.validation

class ValidationService {

    data class ValidationResult(val isValid: Boolean, val errorMessage: String = "")

    fun validateUsername(username: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult(false, "Ім'я користувача не може бути порожнім.")
        }

        val regex = Regex("^[a-z0-9_]+$")
        if (!regex.matches(username)) {
            return ValidationResult(false, "Ім'я користувача може містити лише малі літери, цифри, та нижні підкреслювання.")
        }
        return ValidationResult(true)
    }

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(false, "Пошта не може бути порожньою.")
        }

        val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        if (!regex.matches(email)) {
            return ValidationResult(false, "Введіть справжню електронну адресу.")
        }
        return ValidationResult(true)
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(false, "Пароль не може бути порожнім.")
        }
        if (password.length < 8) {
            return ValidationResult(false, "Пароль повинен складатися принаймні з 8 символів.")
        }
        if (!password.any { it.isUpperCase() }) {
            return ValidationResult(false, "Пароль повинен містити принаймні одну велику літеру.")
        }
        if (!password.any { it.isLowerCase() }) {
            return ValidationResult(false, "Пароль повинен містити принаймні одну малу літеру.")
        }
        if (!password.any { it.isDigit() }) {
            return ValidationResult(false, "Пароль повинен містити принаймні одну цифру.")
        }
        return ValidationResult(true)
    }

    fun validateEnglishWord(word: String): ValidationResult {
        if (word.isBlank()) {
            return ValidationResult(false, "Англійське слово не може бути порожнім.")
        }

        val regex = Regex("^[a-zA-Z\\s'-]+$")
        if (!regex.matches(word)) {
            return ValidationResult(false, "Англійське слово може містити лише літери латинського алфавіту, апострофи та дефіси.")
        }

        return ValidationResult(true)
    }

    fun validateUkrainianWord(word: String): ValidationResult {
        if (word.isBlank()) {
            return ValidationResult(false, "Українське слово не може бути порожнім.")
        }

        val regex = Regex("^[а-яА-ЯіІїЇєЄґҐ'\\s-]+$")
        if (!regex.matches(word)) {
            return ValidationResult(false, "Українське слово має містити лише символи українського алфавіту, апострофи та дефіси.")
        }

        return ValidationResult(true)
    }
}
