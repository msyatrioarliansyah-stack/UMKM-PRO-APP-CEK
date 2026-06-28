package com.example.store

import android.content.Context
import com.example.data.Product
import com.example.data.BahanBaku
import com.example.data.ProductWithBahanBaku
import com.example.data.ProductCost
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centered state holding all UMKM-related dashboard information,
 * inspired by Javascript's Zustand state management paradigm.
 */
data class UmkmState(
    val products: List<Product> = emptyList(),
    val bahanBaku: List<BahanBaku> = emptyList(),
    val productsWithBahanBaku: List<ProductWithBahanBaku> = emptyList(),
    val productProductionCosts: List<ProductCost> = emptyList(),
    val kalkulasiCartIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true
)

/**
 * Storage Engine interface for persistence, mirroring Zustand's StateStorage interface.
 */
interface StorageEngine {
    fun getItem(key: String): String?
    fun setItem(key: String, value: String)
}

/**
 * Android SharedPreferences implementation of StorageEngine.
 */
class SharedPreferencesStorageEngine(context: Context) : StorageEngine {
    private val prefs = context.getSharedPreferences("umkm_zustand_prefs", Context.MODE_PRIVATE)

    override fun getItem(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun setItem(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}

/**
 * Generic Kotlin implementation of the Zustand Store pattern.
 * Allows atomic state updates, consistent reactive state tracking, and subscription middleware hooks.
 */
open class ZustandStore<S>(initialState: S) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val listeners = mutableListOf<(S) -> Unit>()

    fun getState(): S = _state.value

    /**
     * Subscribe to state updates. Listeners will be notified of any changes.
     */
    fun subscribe(listener: (S) -> Unit) {
        listeners.add(listener)
    }

    /**
     * Set the state using an updater lambda. Similar to Zustand's set((state) => ({ ... })).
     */
    fun set(updater: (S) -> S) {
        val updated = updater(_state.value)
        _state.value = updated
        notifyListeners(updated)
    }

    /**
     * Replace the state entirely.
     */
    fun set(newState: S) {
        _state.value = newState
        notifyListeners(newState)
    }

    private fun notifyListeners(state: S) {
        listeners.forEach { it(state) }
    }
}

/**
 * Middleware function to persist Zustand state to local storage.
 * Synchronizes with the storage engine on updates and rehydrates state on start.
 */
fun <S> persist(
    store: ZustandStore<S>,
    name: String,
    storage: StorageEngine,
    serializer: (S) -> String,
    deserializer: (String) -> S?
) {
    // 1. Rehydrate: Load saved state from storage at startup
    try {
        val saved = storage.getItem(name)
        if (saved != null) {
            val deserialized = deserializer(saved)
            if (deserialized != null) {
                store.set(deserialized)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // 2. Subscribe to updates: Auto-persist updated state whenever it changes
    store.subscribe { newState ->
        try {
            val serialized = serializer(newState)
            storage.setItem(name, serialized)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Concrete Zustand Store for UMKM Dashboard State.
 * Acts as the centralized, single source of truth for the entire application.
 * Accepts optional Context to auto-configure persist middleware.
 */
class UmkmZustandStore(context: Context? = null) : ZustandStore<UmkmState>(UmkmState()) {
    
    init {
        if (context != null) {
            enablePersistence(context)
        }
    }

    fun enablePersistence(context: Context) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(UmkmState::class.java)

        persist(
            store = this,
            name = "umkm_dashboard_data",
            storage = SharedPreferencesStorageEngine(context),
            serializer = { state -> adapter.toJson(state) },
            deserializer = { json ->
                try {
                    adapter.fromJson(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        )
    }

    // Actions/Mutators
    fun setProducts(products: List<Product>) {
        set { it.copy(products = products) }
    }

    fun setBahanBaku(bahanBaku: List<BahanBaku>) {
        set { it.copy(bahanBaku = bahanBaku) }
    }

    fun setProductsWithBahanBaku(productsWithBahanBaku: List<ProductWithBahanBaku>) {
        set { it.copy(productsWithBahanBaku = productsWithBahanBaku) }
    }

    fun setProductProductionCosts(productProductionCosts: List<ProductCost>) {
        set { it.copy(productProductionCosts = productProductionCosts) }
    }

    fun setLoading(isLoading: Boolean) {
        set { it.copy(isLoading = isLoading) }
    }

    fun toggleKalkulasiCart(productId: Int) {
        set { state ->
            val current = state.kalkulasiCartIds
            val updated = if (current.contains(productId)) {
                current - productId
            } else {
                current + productId
            }
            state.copy(kalkulasiCartIds = updated)
        }
    }

    fun clearKalkulasiCart() {
        set { it.copy(kalkulasiCartIds = emptySet()) }
    }
}
