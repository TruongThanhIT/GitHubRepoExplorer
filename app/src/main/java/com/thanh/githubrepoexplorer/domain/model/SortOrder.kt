package com.thanh.githubrepoexplorer.domain.model

enum class SortOrder {
    DEFAULT,
    STARS
}

fun SortOrder.label() = when (this) {
    SortOrder.DEFAULT -> "Default"
    SortOrder.STARS   -> "Sort by Stars ⭐"
}
