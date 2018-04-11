package com.example.pjakwert.uidemo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.example.pjakwert.uidemo.model.Item
import com.example.pjakwert.uidemo.model.SearchResult
import com.example.pjakwert.uidemo.network.SearchRequestInterface
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RecyclerViewFragment : Fragment(), DataAdapter.Listener {

    lateinit var mRecyclerView : RecyclerView
    lateinit var mSearchEditText : EditText
    lateinit var mProgressBar : ProgressBar
    lateinit var mGoButton : Button
    lateinit var mDataAdapter : DataAdapter
    lateinit var mSearchLayout : LinearLayout
    lateinit var mScrollListener : EndlessRecyclerViewScrollListener
    lateinit var mLoadingSnackBar : Snackbar

    lateinit var mCompositeDisposable : CompositeDisposable
    lateinit var mDataList : ArrayList<Item>

    val TAG = this::class.java.simpleName
    val BASE_URL = "http://www.omdbapi.com/"
    val API_KEY = "3110888f"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val v = getView()
        v?.let {
            mSearchEditText = v.findViewById(R.id.searchEditText)
            mGoButton = v.findViewById(R.id.goButton)
            mSearchLayout = v.findViewById(R.id.searchLayout)
            mProgressBar = v.findViewById(R.id.loadingProgress)
            mRecyclerView = v.findViewById(R.id.recyclerView)
        }

        mLoadingSnackBar = Snackbar.make(mSearchLayout, "Loading ...", Snackbar.LENGTH_INDEFINITE)
                .applyBackgroundColor(R.color.colorPrimaryDark)

        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false

        var layoutManager = GridLayoutManager(activity, 3)
        //var layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL )
        mRecyclerView.layoutManager = layoutManager

        mScrollListener = object: EndlessRecyclerViewScrollListener(layoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                mLoadingSnackBar.show()
                loadJSON(page+1)
            }
        }
        mRecyclerView.addOnScrollListener(mScrollListener)

        mDataList = ArrayList<Item>()
        mDataAdapter = DataAdapter(mDataList, this)
        mRecyclerView.adapter = mDataAdapter

        mCompositeDisposable = CompositeDisposable()

        mGoButton.setOnClickListener {
            if (!searchEditText.text.toString().isEmpty()) {
                hideKeyboard()
                mLoadingSnackBar.show()
                mProgressBar.visibility = View.VISIBLE
                mDataList.clear()
                mDataAdapter.notifyDataSetChanged()
                mScrollListener.resetState()
                loadJSON(1)
            } else {
                searchEditText.setError("Enter movie title")
            }

        }


    }

    override fun onDetach() {
        super.onDetach()
        mCompositeDisposable.clear()
    }


    fun loadJSON(page : Int) {
        val requestInterface = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(SearchRequestInterface::class.java)

        val searchString =  mSearchEditText.text.toString().trim()

        mCompositeDisposable.add(requestInterface
                .getData( API_KEY, searchString, page) // page 1
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError))
    }


    private fun handleResponse(data: SearchResult) {
        mLoadingSnackBar.dismiss()
        mProgressBar.visibility = View.GONE
        mDataList.addAll(data.Search)
        mDataAdapter.notifyDataSetChanged()

        Log.d(TAG, data.Search.joinToString(separator = ",", prefix = "adding movies: { ", postfix = "}"))
    }


    private fun handleError(error: Throwable) {
        mProgressBar.visibility = View.GONE
        mLoadingSnackBar.dismiss()
        Log.d(TAG, error.localizedMessage)
        //showErrorMessage( error.localizedMessage )
    }


    override fun onItemClick(item: Item) {
        //showInfoMessage(item.Title)

        val ft : FragmentTransaction = fragmentManager.beginTransaction()

        val prev : Fragment? = fragmentManager.findFragmentByTag("detailsDialog")
        if (prev!=null) {
            ft.remove(prev)
        }

        ft.addToBackStack(null)

        val fragment = makeDetailsDialogFragment( imageUrl =  item.Poster,
                title =  item.Title,
                imdbId = item.imdbID) as DialogFragment?

        fragment?.show(ft, "detailsDialog")
    }

    fun showInfoMessage(text : String) {
        Snackbar.make(mSearchLayout, text, Snackbar.LENGTH_SHORT).show()
    }

    fun showErrorMessage(text : String) {
        Snackbar.make(mSearchLayout, text, Snackbar.LENGTH_SHORT).applyBackgroundColor(R.color.RED).show()
    }


    fun hideKeyboard() {
        val imManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imManager.hideSoftInputFromWindow( mSearchEditText.windowToken, 0)
    }


}




class DataAdapter (private val itemsList: ArrayList<Item>, private val listener : DataAdapter.Listener) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    interface Listener {
        fun onItemClick(item : Item)
    }

    class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {

        fun bind (item: Item, listener: Listener, position : Int) {
            view.imageView.setBackgroundColor(Color.BLACK)

            Picasso.with(view.context).load(item.Poster)
                    .placeholder(R.drawable.empty_placeholder)
                    .error(R.drawable.empty_placeholder)
                    //.resize(270,400)
                    .fit()
                    .into(view.imageView)

            // or
            /*
            Glide.with(view.context).load(item.Poster)
                    .placeholder(android.R.drawable.progress_horizontal)
                    .error(R.drawable.empty_placeholder)
                   // .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.crossFade()
                    .into(view.imageView)
*/
            view.titleView.setText(item.Title)

            view.setOnClickListener{ listener.onItemClick(item) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemsList[position], listener, position)
    }

    override fun getItemCount(): Int = itemsList.count()

}


