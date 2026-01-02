package me.liwenkun.demo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import me.liwenkun.demo.demoframework.DemoBook

class MainActivityModel : ViewModel(), Observer<DemoBook.Category> {
    private var currentCategory: MutableLiveData<DemoBook.Category> = MutableLiveData()

    init {
        // 上游数据源
        DemoBook.INSTANCE.rootCategory.observeForever(this)
    }

    fun setCurrentCategory(currentCategory: DemoBook.Category?) {
        this.currentCategory.value = currentCategory
    }

    fun getCurrentCategory(): MutableLiveData<DemoBook.Category> {
        return currentCategory
    }

    override fun onChanged(category: DemoBook.Category) {
        currentCategory.value = category
    }

    override fun onCleared() {
        super.onCleared()
        DemoBook.INSTANCE.rootCategory.removeObserver(this)
    }
}