package net.plshark.users.model

import org.springframework.data.annotation.Id

data class Application(@Id val id: Long = 0, val name: String)
