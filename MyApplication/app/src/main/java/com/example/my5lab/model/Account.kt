package com.example.my5lab.model

import java.io.Serializable

data class Account (
    val name: String,
    val login: String,
    val gender: Boolean = false) : Serializable {}