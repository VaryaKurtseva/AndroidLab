package com.example.my4lab.model

import java.io.Serializable

data class Account (
    val name: String,
    val login: String,
    val gender: Boolean = false) : Serializable {}