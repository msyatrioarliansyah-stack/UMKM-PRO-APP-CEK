package com.example

import com.example.data.Product
import com.example.store.UmkmZustandStore
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 * Includes comprehensive tests for our custom Kotlin Zustand Store implementation.
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testZustandStore_initialState() {
    val store = UmkmZustandStore()
    val state = store.getState()
    assertTrue(state.isLoading)
    assertTrue(state.products.isEmpty())
    assertTrue(state.bahanBaku.isEmpty())
    assertTrue(state.kalkulasiCartIds.isEmpty())
  }

  @Test
  fun testZustandStore_actions() {
    val store = UmkmZustandStore()
    
    // Set loading
    store.setLoading(false)
    assertFalse(store.getState().isLoading)

    // Set products
    val mockProducts = listOf(
      Product(id = 1, name = "Product A", category = "Cat A", price = "10000", type = "Satuan", packageName = "", packageDesc = "")
    )
    store.setProducts(mockProducts)
    assertEquals(1, store.getState().products.size)
    assertEquals("Product A", store.getState().products.first().name)

    // Toggle kalkulasi cart
    store.toggleKalkulasiCart(1)
    assertTrue(store.getState().kalkulasiCartIds.contains(1))

    store.toggleKalkulasiCart(1)
    assertFalse(store.getState().kalkulasiCartIds.contains(1))
  }

  @Test
  fun testZustandStore_persistence() {
    val store = UmkmZustandStore()
    
    // Create a mock StorageEngine
    val mockStorage = object : com.example.store.StorageEngine {
      private val map = mutableMapOf<String, String>()
      override fun getItem(key: String): String? = map[key]
      override fun setItem(key: String, value: String) {
        map[key] = value
      }
    }
    
    val moshi = com.squareup.moshi.Moshi.Builder()
      .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
      .build()
    val adapter = moshi.adapter(com.example.store.UmkmState::class.java)
    
    // Initialize persistence middleware
    com.example.store.persist(
      store = store,
      name = "test_store",
      storage = mockStorage,
      serializer = { adapter.toJson(it) },
      deserializer = { adapter.fromJson(it) }
    )
    
    // Change state
    store.setLoading(false)
    store.toggleKalkulasiCart(42)
    
    // Check that state was serialized into storage
    val savedJson = mockStorage.getItem("test_store")
    assertNotNull(savedJson)
    
    // Check if we can rehydrate a new store from the same storage
    val newStore = UmkmZustandStore()
    com.example.store.persist(
      store = newStore,
      name = "test_store",
      storage = mockStorage,
      serializer = { adapter.toJson(it) },
      deserializer = { adapter.fromJson(it) }
    )
    
    // Verify rehydrated state
    assertFalse(newStore.getState().isLoading)
    assertTrue(newStore.getState().kalkulasiCartIds.contains(42))
  }
}
