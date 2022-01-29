package com.mirkamalg.flowadapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.mirkamalg.flowadapter.databinding.ActivityMainBinding
import com.mirkamalg.flowadapter.databinding.ItemSampleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: FlowAdapter<ViewHolder, SampleData>

    private val dataList = arrayListOf<SampleData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonUp.setOnClickListener {
            binding.recyclerView.scrollToPosition(0)
        }

        binding.editTextSearch.doAfterTextChanged { editable ->
            val searched = editable.toString()
            adapter.apply {
                dataFlow = if (searched.isBlank()) {
                    dataList.asFlow()
                } else {
                    dataList.asFlow().filter {
                        it.title.contains(searched, true) || it.body.contains(searched, true)
                    }
                }
                startFlowing(lifecycleScope)
            }

        }

        lifecycleScope.launchWhenStarted {
            withContext(Dispatchers.IO) {

                //Create fake data
                repeat(2000) {
                    dataList.add(
                        SampleData(
                            "Title $it",
                            "Body $it",
                            UUID.randomUUID()
                        )
                    )
                }
                adapter = FlowAdapter(
                    onBuildViewHolder = { viewGroup, _ ->
                        ViewHolder(ItemSampleBinding.inflate(layoutInflater, viewGroup, false))
                    },
                    onBind = { holder, data, _ ->
                        holder.bind(data)
                    })
                withContext(Dispatchers.Main) {
                    binding.recyclerView.adapter = adapter
                }
                adapter.dataFlow = dataList.asFlow()
                adapter.startFlowing(lifecycleScope)
            }

        }
    }
}