package app.xtreamvod

import android.app.Application
import app.xtreamvod.data.CredentialsStore
import app.xtreamvod.data.XtreamRepository
import app.xtreamvod.data.cache.CategoryListingCache

class XtreamVodApp : Application() {
    lateinit var credentialsStore: CredentialsStore
        private set

    lateinit var repository: XtreamRepository
        private set

    override fun onCreate() {
        super.onCreate()
        credentialsStore = CredentialsStore(this)
        repository = XtreamRepository(credentialsStore, CategoryListingCache(this))
    }
}
