package com.example.mobilecomputingproject

import com.example.mobilecomputingproject.helpers.Utls
import org.junit.Test

import org.junit.Assert.*

class UtlsTest {

    @Test
    fun test_string_sanitizer_trivial()
    {
        var dot = Utls.sanitize_str_for_db(".")

        assertFalse(dot == ".")
        assertTrue(dot == "✔")
    }

    @Test
    fun test_string_sanitizer_email()
    {
        var test_email = "test@test.com"
        var sanitized_email = Utls.sanitize_str_for_db(test_email)

        assertFalse(sanitized_email == test_email)
        assertTrue(sanitized_email == "test@test✔com")
    }

    @Test
    fun test_string_sanitizer_restorer_trivial()
    {
        var dot = Utls.restore_str_from_db("✔")

        assertFalse(dot == "✔")
        assertTrue(dot == ".")
    }

    @Test
    fun test_string_sanitizer_restorer_email()
    {
        var test_email = "test@test✔com"
        var restored_email = Utls.restore_str_from_db(test_email)

        assertFalse(restored_email == test_email)
        assertTrue(restored_email == "test@test.com")
    }

    @Test
    fun test_chat_index_creator()
    {
        var a = "aaaaaaaaa"
        var b = "bbbb"
        var result = Utls.get_chat_idx_str(a,b)
        assertTrue(result == "aaaaaaaaabbbb")

        result = Utls.get_chat_idx_str(b,a)
        assertTrue(result == "aaaaaaaaabbbb")
    }

    @Test
    fun test_chat_index_creator_emails()
    {
        var a = "test@test✔com"
        var b = "anothertest@dal✔ca"
        var result = Utls.get_chat_idx_str(a,b)
        assertTrue(result == "anothertest@dal✔catest@test✔com")

        result = Utls.get_chat_idx_str(b,a)
        assertTrue(result == "anothertest@dal✔catest@test✔com")
    }

}