package util

class UrlProvider(
        private val modInvUrls: List<String>,
        private val modExpUrls: List<String>
) {
    fun provideModInvUrl(): String = modInvUrls.random()

    fun provideModExpUrl(): String = modExpUrls.random()
}
