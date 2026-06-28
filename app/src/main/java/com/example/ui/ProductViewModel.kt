package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BahanBaku
import com.example.data.Product
import com.example.data.ProductRepository
import com.example.data.ProductWithBahanBaku
import com.example.data.ProductCost
import com.example.store.UmkmZustandStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository, context: android.content.Context? = null) : ViewModel() {
    val store = UmkmZustandStore(context)

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            store.setLoading(false)
        }

        viewModelScope.launch {
            repository.allProducts.collect {
                store.setProducts(it)
            }
        }

        viewModelScope.launch {
            repository.allBahanBaku.collect {
                store.setBahanBaku(it)
            }
        }

        viewModelScope.launch {
            repository.allProductsWithBahanBaku.collect {
                store.setProductsWithBahanBaku(it)
            }
        }

        viewModelScope.launch {
            repository.productProductionCosts.collect {
                store.setProductProductionCosts(it)
            }
        }
    }

    val uiState: StateFlow<List<Product>> = store.state
        .map { it.products }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bahanBakuState: StateFlow<List<BahanBaku>> = store.state
        .map { it.bahanBaku }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val productsWithBahanBakuState: StateFlow<List<ProductWithBahanBaku>> = store.state
        .map { it.productsWithBahanBaku }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val productProductionCostsState: StateFlow<List<ProductCost>> = store.state
        .map { it.productProductionCosts }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val kalkulasiCartIds: StateFlow<Set<Int>> = store.state
        .map { it.kalkulasiCartIds }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val isLoading: StateFlow<Boolean> = store.state
        .map { it.isLoading }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun toggleKalkulasiCart(productId: Int) {
        store.toggleKalkulasiCart(productId)
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.insert(product)
        }
    }

    fun addBahanBaku(bahanBaku: BahanBaku) {
        viewModelScope.launch {
            repository.insertBahanBaku(bahanBaku)
        }
    }

    fun deleteBahanBaku(bahanBaku: BahanBaku) {
        viewModelScope.launch {
            repository.deleteBahanBaku(bahanBaku)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}

class ProductViewModelFactory(private val repository: ProductRepository, private val context: android.content.Context? = null) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
