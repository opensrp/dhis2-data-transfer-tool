package model

data class Pager(val page: Int, var pageSize: Int, var pageCount: Int, var total: Int, var nextPage: String?)
