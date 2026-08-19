package com.fireants.template.domain.usecase.player

import com.fireants.template.data.repository.UserRepository
import javax.inject.Inject

class UseTicketUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Boolean =
        repository.useTicket()
}
