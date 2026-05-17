package com.thanh.githubrepoexplorer.domain.model.exception

import com.thanh.githubrepoexplorer.domain.model.error.DataError
import java.io.IOException

class DomainErrorException(val error: DataError.Network) :
    IOException("Domain error: $error")

