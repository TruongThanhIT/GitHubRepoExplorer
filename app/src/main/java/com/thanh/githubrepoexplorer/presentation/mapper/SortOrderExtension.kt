package com.thanh.githubrepoexplorer.presentation.mapper

import com.thanh.githubrepoexplorer.domain.model.SortOrder

fun SortOrder.label() = when (this) {
    SortOrder.DEFAULT -> "Default"
    SortOrder.STARS   -> "Sort by Stars ⭐"
}