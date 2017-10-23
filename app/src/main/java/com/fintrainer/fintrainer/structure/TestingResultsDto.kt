package com.fintrainer.fintrainer.structure

/**
 * Created by deadf on 17.03.2017.
 */

data class TestingResultsDto(
        var right: Int,
        var wrong: Int,
        var weight: Int,
        var testWeight: Int,
        var worthChapter: Int
)