package com.mirkamalg.flowadapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

/**
 * Created by Mirkamal on 29 January 2022
 */

class FlowAdapter<VH : RecyclerView.ViewHolder, D>(
    private val onBuildViewHolder: (ViewGroup, Int) -> VH,
    private val onBind: (VH, D, Int) -> Unit
) : RecyclerView.Adapter<VH>() {

    /**
     * [Flow] to be used when [startFlowing] is invoked. Initial value is null
     */
    var dataFlow: Flow<D>? = null
        set(value) {
            field = value
            val size = mList.size
            mList.clear()
            notifyItemRangeRemoved(0, size)
        }

    private val mList = arrayListOf<D>()

    private var currentJob: Job? = null

    /**
     * Starts collecting data from the [dataFlow]
     *
     * Previously added data will be wiped.
     *
     * @param [scope] [CoroutineScope] which will be used to launch a new coroutine and start collecting the [Flow]
     */
    @SuppressLint("NotifyDataSetChanged")
    fun startFlowing(scope: CoroutineScope) {
        currentJob?.cancel()
        mList.clear()
        notifyDataSetChanged()
        currentJob = scope.launch(Dispatchers.Default) {
            delay(300) //Recyclerview is slow :( wait for it
            dataFlow?.collect {
                mList.add(it)
                withContext(Dispatchers.Main) {
                    notifyItemInserted(mList.lastIndex)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onBuildViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) =
        onBind(holder, mList[position], position)

    override fun getItemCount() = mList.size
}